'use client';

import { useAuthStore, type AuthUser } from './auth-store';

// ============================================================
// Configuration
// ============================================================

/**
 * Path translator helper - maps frontend API paths to backend gateway endpoints.
 * Frontend uses short French paths; backend uses versioned English paths.
 */
const PATH_MAPPINGS: Record<string, string> = {
  '/api/membres': '/api/v1/members',
  '/api/paiements': '/api/v1/payments',
  '/api/remboursements': '/api/v1/refunds',
  '/api/epargne': '/api/v1/epargne',
  '/api/auth': '/api/v1/auth',
  '/api/keycloak': '/api/v1/keycloak',
};

function mapPath(path: string): string {
  for (const [frontend, backend] of Object.entries(PATH_MAPPINGS)) {
    if (path.startsWith(frontend)) {
      return path.replace(frontend, backend);
    }
  }
  return path;
}

// ============================================================
// Error class
// ============================================================

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
    this.name = 'ApiError';
  }
}

// ============================================================
// Core request helper with auto token refresh
// ============================================================

interface RequestOptions extends RequestInit {
  authenticated?: boolean;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { authenticated = true, headers: customHeaders, ...rest } = options;

  const mappedPath = mapPath(path);
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(customHeaders as Record<string, string>),
  };

  if (authenticated) {
    const { user } = useAuthStore.getState();
    if (user?.accessToken) {
      // Auto-refresh token if expired
      if (user.expiresAt && Date.now() > user.expiresAt - 30_000) {
        try {
          await refreshTokenAndStore();
          const updatedUser = useAuthStore.getState().user;
          if (updatedUser?.accessToken) {
            headers['Authorization'] = `${updatedUser.tokenType || 'Bearer'} ${updatedUser.accessToken}`;
          }
        } catch {
          // Refresh failed - proceed with existing token, will likely 401
          headers['Authorization'] = `${user.tokenType || 'Bearer'} ${user.accessToken}`;
        }
      } else {
        headers['Authorization'] = `${user.tokenType || 'Bearer'} ${user.accessToken}`;
      }
    }
  }

  const response = await fetch(mappedPath, { ...rest, headers });

  if (!response.ok) {
    // If 401 and we have a refresh token, try once more
    if (response.status === 401 && authenticated) {
      const { user } = useAuthStore.getState();
      if (user?.refreshToken) {
        try {
          await refreshTokenAndStore();
          const updatedUser = useAuthStore.getState().user;
          if (updatedUser?.accessToken) {
            const retryHeaders = { ...headers, Authorization: `${updatedUser.tokenType || 'Bearer'} ${updatedUser.accessToken}` };
            const retryResponse = await fetch(mappedPath, { ...rest, headers: retryHeaders });
            if (retryResponse.ok) {
              if (retryResponse.status === 204) return undefined as T;
              return retryResponse.json();
            }
          }
        } catch {
          // Refresh failed, force logout
          useAuthStore.getState().logout();
          if (typeof window !== 'undefined') window.location.href = '/login';
          throw new ApiError(401, 'Session expirée. Veuillez vous reconnecter.');
        }
      }
    }
    const errorBody = await response.text().catch(() => 'Unknown error');
    throw new ApiError(response.status, errorBody);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}

// ============================================================
// Token refresh helper
// ============================================================

async function refreshTokenAndStore(): Promise<void> {
  const { user, updateToken } = useAuthStore.getState();
  if (!user?.refreshToken) throw new Error('No refresh token');

  const body = new URLSearchParams({
    grant_type: 'refresh_token',
    client_id: 'serenity-ui',
    refresh_token: user.refreshToken,
  });

  const response = await fetch('/api/v1/auth/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: body.toString(),
  });

  if (!response.ok) {
    throw new ApiError(response.status, 'Session expirée');
  }

  const tokenData = await response.json();
  const expiresAt = Date.now() + (tokenData.expires_in || 300) * 1000;
  updateToken(tokenData.access_token, tokenData.refresh_token, expiresAt);
}

// ============================================================
// Types
// ============================================================

export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  token_type: string;
  expires_in: number;
}

export interface UserInfo {
  sub?: string;
  id?: string;
  preferred_username?: string;
  email?: string;
  given_name?: string;
  family_name?: string;
  realm_access?: { roles: string[] };
  resource_access?: Record<string, { roles: string[] }>;
}

export interface RealmDto {
  realmName: string;
  displayName?: string;
  enabled: boolean;
}

export interface ClientDto {
  id?: string;
  clientId: string;
  name?: string;
  description?: string;
  enabled: boolean;
  publicClient?: boolean;
  directAccessGrantsEnabled?: boolean;
  redirectUris?: string[];
  webOrigins?: string[];
}

export interface GroupDto {
  id?: string;
  name: string;
  path?: string;
}

export interface RoleDto {
  name: string;
  description?: string;
  composite?: boolean;
}

export interface UserAttributeDto {
  attributes: Record<string, string[]>;
}

export interface UserDto {
  id?: string;
  username?: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  enabled?: boolean;
  emailVerified?: boolean;
  attributes?: Record<string, string[]>;
}

// ============================================================
// Auth API
// ============================================================

const authApi = {
  /** Login with username/password via Keycloak OAuth2 token endpoint */
  login: async (username: string, password: string): Promise<TokenResponse> => {
    const body = new URLSearchParams({
      grant_type: 'password',
      client_id: 'serenity-ui',
      username,
      password,
    });

    const response = await fetch('/api/v1/auth/token', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString(),
    });

    if (!response.ok) {
      const error = await response.text().catch(() => 'Identifiants invalides');
      throw new ApiError(response.status, error);
    }

    return response.json();
  },

  /** Fetch authenticated user info from Keycloak userinfo endpoint */
  me: async (): Promise<UserInfo> => {
    return request<UserInfo>('/api/v1/auth/me', { method: 'GET' });
  },

  /** Register a new user with the MEMBRE role */
  register: async (data: {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
  }): Promise<any> => {
    return request('/api/v1/auth/register', {
      method: 'POST',
      authenticated: false,
      body: JSON.stringify(data),
    });
  },

  /** Logout from Keycloak by invalidating the refresh token */
  logout: async (): Promise<void> => {
    const { user } = useAuthStore.getState();
    if (user?.refreshToken) {
      try {
        const body = new URLSearchParams({
          client_id: 'serenity-ui',
          refresh_token: user.refreshToken,
        });

        await fetch('/api/v1/auth/logout', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: body.toString(),
        });
      } catch {
        // Silently handle logout errors
      }
    }
  },

  /** Request a password reset email via Keycloak */
  forgotPassword: async (email: string): Promise<any> => {
    return request('/api/v1/auth/forgot-password', {
      method: 'POST',
      authenticated: false,
      body: JSON.stringify({ email }),
    });
  },
};

// ============================================================
// Keycloak Administration API (ADMIN role required)
// ============================================================

const keycloakApi = {
  // --- Realms ---
  realms: {
    list: (): Promise<any> =>
      request('/api/v1/keycloak/realms', { method: 'GET' }),

    create: (data: RealmDto): Promise<any> =>
      request('/api/v1/keycloak/realms', { method: 'POST', body: JSON.stringify(data) }),

    get: (name: string): Promise<any> =>
      request(`/api/v1/keycloak/realms/${name}`, { method: 'GET' }),

    delete: (name: string): Promise<any> =>
      request(`/api/v1/keycloak/realms/${name}`, { method: 'DELETE' }),
  },

  // --- Clients ---
  clients: {
    list: (): Promise<any> =>
      request('/api/v1/keycloak/clients', { method: 'GET' }),

    create: (data: ClientDto): Promise<any> =>
      request('/api/v1/keycloak/clients', { method: 'POST', body: JSON.stringify(data) }),

    update: (id: string, data: Partial<ClientDto>): Promise<any> =>
      request(`/api/v1/keycloak/clients/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

    delete: (id: string): Promise<any> =>
      request(`/api/v1/keycloak/clients/${id}`, { method: 'DELETE' }),
  },

  // --- Groups ---
  groups: {
    list: (): Promise<any> =>
      request('/api/v1/keycloak/groups', { method: 'GET' }),

    create: (data: GroupDto): Promise<any> =>
      request('/api/v1/keycloak/groups', { method: 'POST', body: JSON.stringify(data) }),

    delete: (id: string): Promise<any> =>
      request(`/api/v1/keycloak/groups/${id}`, { method: 'DELETE' }),

    assignMember: (groupId: string, userId: string): Promise<any> =>
      request(`/api/v1/keycloak/groups/${groupId}/members/${userId}`, { method: 'POST' }),

    removeMember: (groupId: string, userId: string): Promise<any> =>
      request(`/api/v1/keycloak/groups/${groupId}/members/${userId}`, { method: 'DELETE' }),

    listMembers: (groupId: string): Promise<any> =>
      request(`/api/v1/keycloak/groups/${groupId}/members`, { method: 'GET' }),
  },

  // --- Roles ---
  roles: {
    list: (): Promise<any> =>
      request('/api/v1/keycloak/roles', { method: 'GET' }),

    create: (data: RoleDto): Promise<any> =>
      request('/api/v1/keycloak/roles', { method: 'POST', body: JSON.stringify(data) }),

    delete: (name: string): Promise<any> =>
      request(`/api/v1/keycloak/roles/${name}`, { method: 'DELETE' }),

    assignToUser: (name: string, userId: string): Promise<any> =>
      request(`/api/v1/keycloak/roles/${name}/users/${userId}`, { method: 'POST' }),

    removeFromUser: (name: string, userId: string): Promise<any> =>
      request(`/api/v1/keycloak/roles/${name}/users/${userId}`, { method: 'DELETE' }),
  },

  // --- User Attributes ---
  users: {
    getAttributes: (userId: string): Promise<any> =>
      request(`/api/v1/keycloak/users/${userId}/attributes`, { method: 'GET' }),

    updateAttributes: (userId: string, data: UserAttributeDto): Promise<any> =>
      request(`/api/v1/keycloak/users/${userId}/attributes`, { method: 'POST', body: JSON.stringify(data) }),
  },
};

// ============================================================
// Members API (public registration + authenticated)
// ============================================================

const membersApi = {
  register: async (data: {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
  }): Promise<any> => {
    return request('/api/v1/members/register', {
      method: 'POST',
      authenticated: false,
      body: JSON.stringify(data),
    });
  },

  verifyOtp: async (data: { email: string; code: string }): Promise<any> => {
    return request('/api/v1/members/verify-otp', {
      method: 'POST',
      authenticated: false,
      body: JSON.stringify(data),
    });
  },
};

// ============================================================
// Export
// ============================================================

export const apiClient = {
  auth: authApi,
  keycloak: keycloakApi,
  members: membersApi,
};
