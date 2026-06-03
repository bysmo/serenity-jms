// =============================================================================
// Serenity-JMS API Client
// Comprehensive client for ALL microservices through the API Gateway
// =============================================================================

import { API_BASE_URL } from './constants';
import type {
  ApiResponse,
  PagedResponse,
  ErrorResponse,
  // Identity
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  UserInfoResponse,
  RefreshRequest,
  ChangePasswordRequest,
  // Member
  Membre,
  MembreRequest,
  MembreRegistrationRequest,
  MembreDashboardResponse,
  Segment,
  SegmentRequest,
  KycVerification,
  KycVerificationRequest,
  ParrainageConfig,
  ParrainageCommission,
  ApplyParrainageRequest,
  CompteExterne,
  CompteExterneRequest,
  PinSetupRequest,
  PinVerifyRequest,
  PinEnableRequest,
  // Cotisation
  Cotisation,
  CotisationRequest,
  Engagement,
  EngagementRequest,
  Paiement,
  PaiementRequest,
  Adhesion,
  AdhesionRequest,
  VersementDemande,
  VersementDemandeRequest,
  Remboursement,
  RemboursementRequest,
  // Epargne
  EpargnePlan,
  EpargnePlanRequest,
  EpargneSouscription,
  SouscriptionRequest,
  EpargneEcheance,
  EpargneVersement,
  VersementRequest,
  // NanoCredit
  NanoCredit,
  NanoCreditRequest,
  EtudeRequest,
  NanoCreditPalier,
  NanoCreditPalierRequest,
  NanoCreditEcheance,
  NanoCreditVersement,
  NanoCreditGarant,
  // Admin
  AppSetting,
  AppSettingRequest,
  Tag,
  TagRequest,
  Annonce,
  AnnonceRequest,
  AuditLog,
  AutoNumberingConfig,
  AutoNumberingConfigRequest,
  EmailTemplate,
  EmailTemplateRequest,
  // Payment Gateway
  PaymentTransaction,
  PayDunyaConfig,
  PayDunyaConfigRequest,
  PiSpiConfig,
  PiSpiConfigRequest,
  PaymentMethod,
  PaymentMethodRequest,
  // Notification
  NotificationLog,
  SmsGateway,
  SmsGatewayRequest,
  SmtpConfiguration,
  SmtpConfigurationRequest,
  // Collector
  CollecteSession,
  Collecte,
  CollecteRequest,
  CollecteSessionSummary,
} from '@/types';

// -----------------------------------------------------------------------------
// Custom Errors
// -----------------------------------------------------------------------------

export class ApiError extends Error {
  status: number;
  errors?: { field: string; message: string }[];
  timestamp?: string;

  constructor(errorResponse: ErrorResponse) {
    super(errorResponse.message);
    this.name = 'ApiError';
    this.status = errorResponse.status;
    this.errors = errorResponse.errors;
    this.timestamp = errorResponse.timestamp;
  }
}

export class NetworkError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'NetworkError';
  }
}

export class AuthError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuthError';
  }
}

// -----------------------------------------------------------------------------
// Token Management
// -----------------------------------------------------------------------------

const TOKEN_KEY = 'serenity-auth';
const REFRESH_THRESHOLD_MS = 60_000; // refresh 1 minute before expiry

function getStoredAuth(): { token: string | null; refreshToken: string | null } {
  if (typeof window === 'undefined') return { token: null, refreshToken: null };
  try {
    const raw = localStorage.getItem(TOKEN_KEY);
    if (!raw) return { token: null, refreshToken: null };
    const parsed = JSON.parse(raw);
    return {
      token: parsed?.state?.token ?? null,
      refreshToken: parsed?.state?.refreshToken ?? null,
    };
  } catch {
    return { token: null, refreshToken: null };
  }
}

function getAccessToken(): string | null {
  return getStoredAuth().token;
}

function getRefreshToken(): string | null {
  return getStoredAuth().refreshToken;
}

// -----------------------------------------------------------------------------
// HTTP Helper
// -----------------------------------------------------------------------------

export function mapPath(path: string): string {
  if (path.startsWith('/api/membres')) {
    return path.replace('/api/membres', '/api/v1/members');
  }
  if (path.startsWith('/api/')) {
    return path.replace('/api/', '/api/v1/');
  }
  return path;
}

interface RequestOptions extends Omit<RequestInit, 'body'> {
  params?: Record<string, string | number | boolean | undefined>;
  body?: unknown;
  auth?: boolean;
}

async function request<T>(
  path: string,
  options: RequestOptions = {}
): Promise<T> {
  const { params, body, auth = true, headers: customHeaders, ...rest } = options;

  // Build URL with query params
  const url = new URL(`${API_BASE_URL}${mapPath(path)}`);
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        url.searchParams.set(key, String(value));
      }
    });
  }

  // Build headers
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    ...(customHeaders as Record<string, string>),
  };

  // Add auth token
  if (auth) {
    const token = getAccessToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }

  // Build fetch options
  const fetchOptions: RequestInit = {
    ...rest,
    headers,
  };

  if (body !== undefined) {
    fetchOptions.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(url.toString(), fetchOptions);

    // Handle 401 — attempt token refresh
    if (response.status === 401 && auth) {
      const refreshed = await attemptTokenRefresh();
      if (refreshed) {
        headers['Authorization'] = `Bearer ${getAccessToken()}`;
        const retryResponse = await fetch(url.toString(), { ...fetchOptions, headers });
        return handleResponse<T>(retryResponse);
      }
      throw new AuthError('Session expirée. Veuillez vous reconnecter.');
    }

    return handleResponse<T>(response);
  } catch (error) {
    if (error instanceof ApiError || error instanceof AuthError) {
      throw error;
    }
    throw new NetworkError(
      error instanceof Error ? error.message : 'Erreur réseau. Veuillez réessayer.'
    );
  }
}

async function handleResponse<T>(response: Response): Promise<T> {
  // 204 No Content
  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  let data: unknown;

  try {
    data = JSON.parse(text);
  } catch {
    data = text;
  }

  if (!response.ok) {
    const errorResponse: ErrorResponse =
      typeof data === 'object' && data !== null
        ? (data as ErrorResponse)
        : {
            status: response.status,
            message: typeof data === 'string' ? data : response.statusText,
            timestamp: new Date().toISOString(),
          };
    throw new ApiError(errorResponse);
  }

  return data as T;
}

// Token refresh logic
let refreshPromise: Promise<boolean> | null = null;

async function attemptTokenRefresh(): Promise<boolean> {
  if (refreshPromise) return refreshPromise;

  refreshPromise = (async () => {
    try {
      const refreshToken = getRefreshToken();
      if (!refreshToken) return false;

      const response = await fetch(`${API_BASE_URL}${mapPath('/api/auth/refresh')}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });

      if (!response.ok) return false;

      const result: ApiResponse<LoginResponse> = await response.json();
      // Update stored token (Zustand persist will sync to localStorage)
      if (typeof window !== 'undefined') {
        const raw = localStorage.getItem(TOKEN_KEY);
        if (raw) {
          const parsed = JSON.parse(raw);
          parsed.state = parsed.state || {};
          parsed.state.token = result.data.accessToken;
          if (result.data.refreshToken) {
            parsed.state.refreshToken = result.data.refreshToken;
          }
          localStorage.setItem(TOKEN_KEY, JSON.stringify(parsed));
        }
      }
      return true;
    } catch {
      return false;
    } finally {
      refreshPromise = null;
    }
  })();

  return refreshPromise;
}

// =============================================================================
// API Client Class
// =============================================================================

class SerenityApiClient {
  // ---------------------------------------------------------------------------
  // Identity Service — Auth
  // ---------------------------------------------------------------------------

  auth = {
    login: (data: LoginRequest) =>
      request<ApiResponse<LoginResponse>>('/api/auth/login', {
        method: 'POST',
        body: data,
        auth: false,
      }),

    logout: () =>
      request<ApiResponse<void>>('/api/auth/logout', { method: 'POST' }),

    refresh: (data: RefreshRequest) =>
      request<ApiResponse<LoginResponse>>('/api/auth/refresh', {
        method: 'POST',
        body: data,
        auth: false,
      }),

    me: () =>
      request<ApiResponse<UserInfoResponse>>('/api/auth/me', { method: 'GET' }),

    register: (data: RegisterRequest) =>
      request<ApiResponse<UserInfoResponse>>('/api/auth/register', {
        method: 'POST',
        body: data,
        auth: false,
      }),

    changePassword: (data: ChangePasswordRequest) =>
      request<ApiResponse<void>>('/api/auth/change-password', {
        method: 'POST',
        body: data,
      }),

    forgotPassword: (email: string) =>
      request<ApiResponse<void>>('/api/auth/forgot-password', {
        method: 'POST',
        params: { email },
        auth: false,
      }),
  };

  // ---------------------------------------------------------------------------
  // Member Service — Members
  // ---------------------------------------------------------------------------

  members = {
    list: (params?: { page?: number; size?: number; sort?: string }) =>
      request<PagedResponse<Membre>>('/api/membres', { method: 'GET', params }),

    getById: (id: string) =>
      request<ApiResponse<Membre>>(`/api/membres/${id}`, { method: 'GET' }),

    create: (data: MembreRequest) =>
      request<ApiResponse<Membre>>('/api/membres', {
        method: 'POST',
        body: data,
      }),

    register: (data: MembreRegistrationRequest) =>
      request<ApiResponse<Membre>>('/api/membres/register', {
        method: 'POST',
        body: data,
        auth: false,
      }),

    update: (id: string, data: MembreRequest) =>
      request<ApiResponse<Membre>>(`/api/membres/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/membres/${id}`, { method: 'DELETE' }),

    search: (query: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<Membre>>('/api/membres/search', {
        method: 'GET',
        params: { query, ...params },
      }),

    dashboard: (id: string) =>
      request<ApiResponse<MembreDashboardResponse>>(`/api/membres/${id}/dashboard`, {
        method: 'GET',
      }),

    verifyOtp: (membreId: string, otpCode: string) =>
      request<ApiResponse<void>>(`/api/membres/${membreId}/verify-otp`, {
        method: 'POST',
        body: { otpCode },
      }),
  };

  // ---------------------------------------------------------------------------
  // Member Service — Segments
  // ---------------------------------------------------------------------------

  segments = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Segment>>('/api/segments', { method: 'GET', params }),

    getAll: () =>
      request<ApiResponse<Segment[]>>('/api/segments/all', { method: 'GET' }),

    getById: (id: string) =>
      request<ApiResponse<Segment>>(`/api/segments/${id}`, { method: 'GET' }),

    create: (data: SegmentRequest) =>
      request<ApiResponse<Segment>>('/api/segments', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: SegmentRequest) =>
      request<ApiResponse<Segment>>(`/api/segments/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/segments/${id}`, { method: 'DELETE' }),
  };

  // ---------------------------------------------------------------------------
  // Member Service — KYC
  // ---------------------------------------------------------------------------

  kyc = {
    initiate: (membreId: string) =>
      request<ApiResponse<KycVerification>>(`/api/kyc/${membreId}/initiate`, {
        method: 'POST',
      }),

    upload: (membreId: string, data: KycVerificationRequest) =>
      request<ApiResponse<KycVerification>>(`/api/kyc/${membreId}/upload`, {
        method: 'POST',
        body: data,
      }),

    validate: (verificationId: string) =>
      request<ApiResponse<KycVerification>>(`/api/kyc/${verificationId}/validate`, {
        method: 'PUT',
      }),

    reject: (verificationId: string, motifRejet: string) =>
      request<ApiResponse<KycVerification>>(`/api/kyc/${verificationId}/reject`, {
        method: 'PUT',
        body: { motifRejet },
      }),

    getStatus: (membreId: string) =>
      request<ApiResponse<KycVerification>>(`/api/kyc/${membreId}/status`, {
        method: 'GET',
      }),
  };

  // ---------------------------------------------------------------------------
  // Member Service — Parrainage
  // ---------------------------------------------------------------------------

  parrainage = {
    getConfig: () =>
      request<ApiResponse<ParrainageConfig>>('/api/parrainage/config', {
        method: 'GET',
      }),

    updateConfig: (data: Partial<ParrainageConfig>) =>
      request<ApiResponse<ParrainageConfig>>('/api/parrainage/config', {
        method: 'PUT',
        body: data,
      }),

    apply: (data: ApplyParrainageRequest) =>
      request<ApiResponse<void>>('/api/parrainage/apply', {
        method: 'POST',
        body: data,
      }),

    commissions: (parrainId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<ParrainageCommission>>(
        `/api/parrainage/${parrainId}/commissions`,
        { method: 'GET', params }
      ),

    claimCommission: (commissionId: string) =>
      request<ApiResponse<void>>(`/api/parrainage/commissions/${commissionId}/claim`, {
        method: 'POST',
      }),
  };

  // ---------------------------------------------------------------------------
  // Member Service — Comptes Externes
  // ---------------------------------------------------------------------------

  comptesExternes = {
    list: (membreId: string) =>
      request<ApiResponse<CompteExterne[]>>(
        `/api/membres/${membreId}/comptes-externes`,
        { method: 'GET' }
      ),

    getById: (id: string) =>
      request<ApiResponse<CompteExterne>>(`/api/comptes-externes/${id}`, {
        method: 'GET',
      }),

    create: (membreId: string, data: CompteExterneRequest) =>
      request<ApiResponse<CompteExterne>>(
        `/api/membres/${membreId}/comptes-externes`,
        { method: 'POST', body: data }
      ),

    update: (id: string, data: CompteExterneRequest) =>
      request<ApiResponse<CompteExterne>>(`/api/comptes-externes/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/comptes-externes/${id}`, {
        method: 'DELETE',
      }),
  };

  // ---------------------------------------------------------------------------
  // Member Service — PIN Management
  // ---------------------------------------------------------------------------

  pin = {
    setup: (membreId: string, data: PinSetupRequest) =>
      request<ApiResponse<void>>(`/api/membres/${membreId}/pin/setup`, {
        method: 'POST',
        body: data,
      }),

    verify: (membreId: string, data: PinVerifyRequest) =>
      request<ApiResponse<boolean>>(`/api/membres/${membreId}/pin/verify`, {
        method: 'POST',
        body: data,
      }),

    enable: (membreId: string, data: PinEnableRequest) =>
      request<ApiResponse<void>>(`/api/membres/${membreId}/pin/enable`, {
        method: 'POST',
        body: data,
      }),

    disable: (membreId: string) =>
      request<ApiResponse<void>>(`/api/membres/${membreId}/pin/disable`, {
        method: 'POST',
      }),
  };

  // ---------------------------------------------------------------------------
  // Cotisation Service — Cotisations
  // ---------------------------------------------------------------------------

  cotisations = {
    list: (params?: {
      page?: number;
      size?: number;
      sort?: string;
      type?: string;
      actif?: boolean;
    }) =>
      request<PagedResponse<Cotisation>>('/api/cotisations', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<Cotisation>>(`/api/cotisations/${id}`, {
        method: 'GET',
      }),

    create: (data: CotisationRequest) =>
      request<ApiResponse<Cotisation>>('/api/cotisations', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: CotisationRequest) =>
      request<ApiResponse<Cotisation>>(`/api/cotisations/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/cotisations/${id}`, { method: 'DELETE' }),

    filter: (params?: {
      page?: number;
      size?: number;
      type?: string;
      frequence?: string;
      actif?: boolean;
    }) =>
      request<PagedResponse<Cotisation>>('/api/cotisations/filter', {
        method: 'GET',
        params,
      }),
  };

  // ---------------------------------------------------------------------------
  // Cotisation Service — Engagements
  // ---------------------------------------------------------------------------

  engagements = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Engagement>>('/api/engagements', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<Engagement>>(`/api/engagements/${id}`, {
        method: 'GET',
      }),

    create: (data: EngagementRequest) =>
      request<ApiResponse<Engagement>>('/api/engagements', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: EngagementRequest) =>
      request<ApiResponse<Engagement>>(`/api/engagements/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/engagements/${id}`, { method: 'DELETE' }),

    byMembre: (membreId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<Engagement>>(
        `/api/membres/${membreId}/engagements`,
        { method: 'GET', params }
      ),

    byCotisation: (
      cotisationId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<Engagement>>(
        `/api/cotisations/${cotisationId}/engagements`,
        { method: 'GET', params }
      ),

    updateStatut: (id: string, statut: string) =>
      request<ApiResponse<Engagement>>(`/api/engagements/${id}/statut`, {
        method: 'PUT',
        body: { statut },
      }),
  };

  // ---------------------------------------------------------------------------
  // Cotisation Service — Paiements
  // ---------------------------------------------------------------------------

  paiements = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Paiement>>('/api/paiements', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<Paiement>>(`/api/paiements/${id}`, { method: 'GET' }),

    create: (data: PaiementRequest) =>
      request<ApiResponse<Paiement>>('/api/paiements', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: PaiementRequest) =>
      request<ApiResponse<Paiement>>(`/api/paiements/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/paiements/${id}`, { method: 'DELETE' }),

    byMembre: (membreId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<Paiement>>(
        `/api/membres/${membreId}/paiements`,
        { method: 'GET', params }
      ),

    byCotisation: (
      cotisationId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<Paiement>>(
        `/api/cotisations/${cotisationId}/paiements`,
        { method: 'GET', params }
      ),

    cancel: (id: string) =>
      request<ApiResponse<Paiement>>(`/api/paiements/${id}/cancel`, {
        method: 'PUT',
      }),
  };

  // ---------------------------------------------------------------------------
  // Cotisation Service — Adhesions
  // ---------------------------------------------------------------------------

  adhesions = {
    request: (data: AdhesionRequest) =>
      request<ApiResponse<Adhesion>>('/api/adhesions', {
        method: 'POST',
        body: data,
      }),

    accept: (id: string) =>
      request<ApiResponse<Adhesion>>(`/api/adhesions/${id}/accept`, {
        method: 'PUT',
      }),

    reject: (id: string, motifRefus?: string) =>
      request<ApiResponse<Adhesion>>(`/api/adhesions/${id}/reject`, {
        method: 'PUT',
        body: motifRefus ? { motifRefus } : undefined,
      }),

    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Adhesion>>('/api/adhesions', {
        method: 'GET',
        params,
      }),

    byMembre: (membreId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<Adhesion>>(
        `/api/membres/${membreId}/adhesions`,
        { method: 'GET', params }
      ),

    byCotisation: (
      cotisationId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<Adhesion>>(
        `/api/cotisations/${cotisationId}/adhesions`,
        { method: 'GET', params }
      ),
  };

  // ---------------------------------------------------------------------------
  // Cotisation Service — Versement Demandes
  // ---------------------------------------------------------------------------

  versementDemandes = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<VersementDemande>>('/api/versement-demandes', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<VersementDemande>>(`/api/versement-demandes/${id}`, {
        method: 'GET',
      }),

    create: (data: VersementDemandeRequest) =>
      request<ApiResponse<VersementDemande>>('/api/versement-demandes', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: VersementDemandeRequest) =>
      request<ApiResponse<VersementDemande>>(`/api/versement-demandes/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/versement-demandes/${id}`, {
        method: 'DELETE',
      }),

    traite: (id: string) =>
      request<ApiResponse<VersementDemande>>(`/api/versement-demandes/${id}/traite`, {
        method: 'PUT',
      }),

    reject: (id: string, motifRejet: string) =>
      request<ApiResponse<VersementDemande>>(`/api/versement-demandes/${id}/reject`, {
        method: 'PUT',
        body: { motifRejet },
      }),

    byMembre: (membreId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<VersementDemande>>(
        `/api/membres/${membreId}/versement-demandes`,
        { method: 'GET', params }
      ),

    byCotisation: (
      cotisationId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<VersementDemande>>(
        `/api/cotisations/${cotisationId}/versement-demandes`,
        { method: 'GET', params }
      ),
  };

  // ---------------------------------------------------------------------------
  // Cotisation Service — Remboursements
  // ---------------------------------------------------------------------------

  remboursements = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Remboursement>>('/api/remboursements', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<Remboursement>>(`/api/remboursements/${id}`, {
        method: 'GET',
      }),

    create: (data: RemboursementRequest) =>
      request<ApiResponse<Remboursement>>('/api/remboursements', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: RemboursementRequest) =>
      request<ApiResponse<Remboursement>>(`/api/remboursements/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/remboursements/${id}`, {
        method: 'DELETE',
      }),

    approve: (id: string) =>
      request<ApiResponse<Remboursement>>(`/api/remboursements/${id}/approve`, {
        method: 'PUT',
      }),

    reject: (id: string, commentaire?: string) =>
      request<ApiResponse<Remboursement>>(`/api/remboursements/${id}/reject`, {
        method: 'PUT',
        body: commentaire ? { commentaire } : undefined,
      }),

    byMembre: (membreId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<Remboursement>>(
        `/api/membres/${membreId}/remboursements`,
        { method: 'GET', params }
      ),

    byCotisation: (
      cotisationId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<Remboursement>>(
        `/api/cotisations/${cotisationId}/remboursements`,
        { method: 'GET', params }
      ),
  };

  // ---------------------------------------------------------------------------
  // Epargne Service — Plans
  // ---------------------------------------------------------------------------

  epargnePlans = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<EpargnePlan>>('/api/epargne/plans', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<EpargnePlan>>(`/api/epargne/plans/${id}`, {
        method: 'GET',
      }),

    create: (data: EpargnePlanRequest) =>
      request<ApiResponse<EpargnePlan>>('/api/epargne/plans', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: EpargnePlanRequest) =>
      request<ApiResponse<EpargnePlan>>(`/api/epargne/plans/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/epargne/plans/${id}`, {
        method: 'DELETE',
      }),

    toggle: (id: string) =>
      request<ApiResponse<EpargnePlan>>(`/api/epargne/plans/${id}/toggle`, {
        method: 'PUT',
      }),
  };

  // ---------------------------------------------------------------------------
  // Epargne Service — Souscriptions
  // ---------------------------------------------------------------------------

  epargneSouscriptions = {
    subscribe: (data: SouscriptionRequest) =>
      request<ApiResponse<EpargneSouscription>>('/api/epargne/souscriptions', {
        method: 'POST',
        body: data,
      }),

    getById: (id: string) =>
      request<ApiResponse<EpargneSouscription>>(
        `/api/epargne/souscriptions/${id}`,
        { method: 'GET' }
      ),

    byMembre: (membreId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<EpargneSouscription>>(
        `/api/epargne/membres/${membreId}/souscriptions`,
        { method: 'GET', params }
      ),

    byPlan: (planId: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<EpargneSouscription>>(
        `/api/epargne/plans/${planId}/souscriptions`,
        { method: 'GET', params }
      ),

    cancel: (id: string) =>
      request<ApiResponse<EpargneSouscription>>(
        `/api/epargne/souscriptions/${id}/cancel`,
        { method: 'PUT' }
      ),

    echeances: (
      souscriptionId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<EpargneEcheance>>(
        `/api/epargne/souscriptions/${souscriptionId}/echeances`,
        { method: 'GET', params }
      ),

    versements: (
      souscriptionId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<EpargneVersement>>(
        `/api/epargne/souscriptions/${souscriptionId}/versements`,
        { method: 'GET', params }
      ),
  };

  // ---------------------------------------------------------------------------
  // Epargne Service — Versements
  // ---------------------------------------------------------------------------

  epargneVersements = {
    create: (data: VersementRequest) =>
      request<ApiResponse<EpargneVersement>>('/api/epargne/versements', {
        method: 'POST',
        body: data,
      }),

    bySouscription: (
      souscriptionId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<EpargneVersement>>(
        `/api/epargne/souscriptions/${souscriptionId}/versements`,
        { method: 'GET', params }
      ),

    totalBySouscription: (souscriptionId: string) =>
      request<ApiResponse<{ total: number }>>(
        `/api/epargne/souscriptions/${souscriptionId}/versements/total`,
        { method: 'GET' }
      ),
  };

  // ---------------------------------------------------------------------------
  // NanoCredit Service — Credits
  // ---------------------------------------------------------------------------

  nanoCredits = {
    list: (params?: { page?: number; size?: number; statut?: string }) =>
      request<PagedResponse<NanoCredit>>('/api/nano-credits', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}`, {
        method: 'GET',
      }),

    create: (data: NanoCreditRequest) =>
      request<ApiResponse<NanoCredit>>('/api/nano-credits', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: NanoCreditRequest) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/nano-credits/${id}`, {
        method: 'DELETE',
      }),

    etude: (id: string, data: EtudeRequest) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}/etude`, {
        method: 'PUT',
        body: data,
      }),

    accorder: (id: string) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}/accorder`, {
        method: 'PUT',
      }),

    debourser: (id: string) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}/debourser`, {
        method: 'PUT',
      }),

    rembourser: (id: string, montant: number) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}/rembourser`, {
        method: 'POST',
        body: { montant },
      }),

    annuler: (id: string) =>
      request<ApiResponse<NanoCredit>>(`/api/nano-credits/${id}/annuler`, {
        method: 'PUT',
      }),

    echeances: (id: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<NanoCreditEcheance>>(
        `/api/nano-credits/${id}/echeances`,
        { method: 'GET', params }
      ),

    versements: (id: string, params?: { page?: number; size?: number }) =>
      request<PagedResponse<NanoCreditVersement>>(
        `/api/nano-credits/${id}/versements`,
        { method: 'GET', params }
      ),

    garants: (id: string) =>
      request<ApiResponse<NanoCreditGarant[]>>(
        `/api/nano-credits/${id}/garants`,
        { method: 'GET' }
      ),
  };

  // ---------------------------------------------------------------------------
  // NanoCredit Service — Paliers
  // ---------------------------------------------------------------------------

  nanoCreditPaliers = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<NanoCreditPalier>>('/api/nano-credit-paliers', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<NanoCreditPalier>>(`/api/nano-credit-paliers/${id}`, {
        method: 'GET',
      }),

    create: (data: NanoCreditPalierRequest) =>
      request<ApiResponse<NanoCreditPalier>>('/api/nano-credit-paliers', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: NanoCreditPalierRequest) =>
      request<ApiResponse<NanoCreditPalier>>(`/api/nano-credit-paliers/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/nano-credit-paliers/${id}`, {
        method: 'DELETE',
      }),

    checkEligibility: (membreId: string, palierId: string) =>
      request<ApiResponse<{ eligible: boolean; motif?: string }>>(
        `/api/nano-credit-paliers/${palierId}/eligibility`,
        { method: 'GET', params: { membreId } }
      ),
  };

  // ---------------------------------------------------------------------------
  // Admin Service — Settings
  // ---------------------------------------------------------------------------

  adminSettings = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<AppSetting>>('/api/admin/settings', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<AppSetting>>(`/api/admin/settings/${id}`, {
        method: 'GET',
      }),

    create: (data: AppSettingRequest) =>
      request<ApiResponse<AppSetting>>('/api/admin/settings', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: AppSettingRequest) =>
      request<ApiResponse<AppSetting>>(`/api/admin/settings/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/admin/settings/${id}`, {
        method: 'DELETE',
      }),

    byGroup: (groupe: string) =>
      request<ApiResponse<AppSetting[]>>('/api/admin/settings/by-group', {
        method: 'GET',
        params: { groupe },
      }),

    byKey: (cle: string) =>
      request<ApiResponse<AppSetting>>('/api/admin/settings/by-key', {
        method: 'GET',
        params: { cle },
      }),
  };

  // ---------------------------------------------------------------------------
  // Admin Service — Tags
  // ---------------------------------------------------------------------------

  adminTags = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Tag>>('/api/admin/tags', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<Tag>>(`/api/admin/tags/${id}`, { method: 'GET' }),

    create: (data: TagRequest) =>
      request<ApiResponse<Tag>>('/api/admin/tags', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: TagRequest) =>
      request<ApiResponse<Tag>>(`/api/admin/tags/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/admin/tags/${id}`, { method: 'DELETE' }),

    byType: (type: string) =>
      request<ApiResponse<Tag[]>>('/api/admin/tags/by-type', {
        method: 'GET',
        params: { type },
      }),
  };

  // ---------------------------------------------------------------------------
  // Admin Service — Announcements
  // ---------------------------------------------------------------------------

  adminAnnouncements = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<Annonce>>('/api/admin/announcements', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<Annonce>>(`/api/admin/announcements/${id}`, {
        method: 'GET',
      }),

    create: (data: AnnonceRequest) =>
      request<ApiResponse<Annonce>>('/api/admin/announcements', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: AnnonceRequest) =>
      request<ApiResponse<Annonce>>(`/api/admin/announcements/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/admin/announcements/${id}`, {
        method: 'DELETE',
      }),

    active: () =>
      request<ApiResponse<Annonce[]>>('/api/admin/announcements/active', {
        method: 'GET',
      }),
  };

  // ---------------------------------------------------------------------------
  // Admin Service — Audit
  // ---------------------------------------------------------------------------

  adminAudit = {
    list: (params?: {
      page?: number;
      size?: number;
      actorType?: string;
      action?: string;
      model?: string;
      startDate?: string;
      endDate?: string;
    }) =>
      request<PagedResponse<AuditLog>>('/api/admin/audit', {
        method: 'GET',
        params,
      }),

    verify: (id: string) =>
      request<ApiResponse<{ valid: boolean; message?: string }>>(
        `/api/admin/audit/${id}/verify`,
        { method: 'GET' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Admin Service — Auto Numbering
  // ---------------------------------------------------------------------------

  adminAutoNumbering = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<AutoNumberingConfig>>(
        '/api/admin/auto-numbering',
        { method: 'GET', params }
      ),

    generate: (objectType: string) =>
      request<ApiResponse<string>>('/api/admin/auto-numbering/generate', {
        method: 'POST',
        body: { objectType },
      }),

    create: (data: AutoNumberingConfigRequest) =>
      request<ApiResponse<AutoNumberingConfig>>('/api/admin/auto-numbering', {
        method: 'POST',
        body: data,
      }),

    toggle: (id: string) =>
      request<ApiResponse<AutoNumberingConfig>>(
        `/api/admin/auto-numbering/${id}/toggle`,
        { method: 'PUT' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Admin Service — Email Templates
  // ---------------------------------------------------------------------------

  adminEmailTemplates = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<EmailTemplate>>('/api/admin/email-templates', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<EmailTemplate>>(`/api/admin/email-templates/${id}`, {
        method: 'GET',
      }),

    create: (data: EmailTemplateRequest) =>
      request<ApiResponse<EmailTemplate>>('/api/admin/email-templates', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: EmailTemplateRequest) =>
      request<ApiResponse<EmailTemplate>>(`/api/admin/email-templates/${id}`, {
        method: 'PUT',
        body: data,
      }),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/admin/email-templates/${id}`, {
        method: 'DELETE',
      }),
  };

  // ---------------------------------------------------------------------------
  // Notification Service — Logs
  // ---------------------------------------------------------------------------

  notifications = {
    list: (params?: {
      page?: number;
      size?: number;
      type?: string;
      channel?: string;
      status?: string;
    }) =>
      request<PagedResponse<NotificationLog>>('/api/notifications', {
        method: 'GET',
        params,
      }),

    send: (data: {
      type: string;
      recipientId: string;
      recipientType: string;
      channel: string;
      subject?: string;
      content: string;
    }) =>
      request<ApiResponse<NotificationLog>>('/api/notifications', {
        method: 'POST',
        body: data,
      }),

    byRecipient: (
      recipientId: string,
      params?: { page?: number; size?: number }
    ) =>
      request<PagedResponse<NotificationLog>>(
        `/api/notifications/recipient/${recipientId}`,
        { method: 'GET', params }
      ),
  };

  // ---------------------------------------------------------------------------
  // Notification Service — Email Templates
  // ---------------------------------------------------------------------------

  notificationEmailTemplates = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<EmailTemplate>>(
        '/api/notifications/email-templates',
        { method: 'GET', params }
      ),

    getById: (id: string) =>
      request<ApiResponse<EmailTemplate>>(
        `/api/notifications/email-templates/${id}`,
        { method: 'GET' }
      ),

    create: (data: EmailTemplateRequest) =>
      request<ApiResponse<EmailTemplate>>('/api/notifications/email-templates', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: EmailTemplateRequest) =>
      request<ApiResponse<EmailTemplate>>(
        `/api/notifications/email-templates/${id}`,
        { method: 'PUT', body: data }
      ),

    delete: (id: string) =>
      request<ApiResponse<void>>(
        `/api/notifications/email-templates/${id}`,
        { method: 'DELETE' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Notification Service — SMS Gateways
  // ---------------------------------------------------------------------------

  smsGateways = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<SmsGateway>>('/api/notifications/sms-gateways', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<SmsGateway>>(
        `/api/notifications/sms-gateways/${id}`,
        { method: 'GET' }
      ),

    create: (data: SmsGatewayRequest) =>
      request<ApiResponse<SmsGateway>>('/api/notifications/sms-gateways', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: SmsGatewayRequest) =>
      request<ApiResponse<SmsGateway>>(
        `/api/notifications/sms-gateways/${id}`,
        { method: 'PUT', body: data }
      ),

    delete: (id: string) =>
      request<ApiResponse<void>>(
        `/api/notifications/sms-gateways/${id}`,
        { method: 'DELETE' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Notification Service — SMTP Configuration
  // ---------------------------------------------------------------------------

  smtp = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<SmtpConfiguration>>('/api/notifications/smtp', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<SmtpConfiguration>>(
        `/api/notifications/smtp/${id}`,
        { method: 'GET' }
      ),

    create: (data: SmtpConfigurationRequest) =>
      request<ApiResponse<SmtpConfiguration>>('/api/notifications/smtp', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: SmtpConfigurationRequest) =>
      request<ApiResponse<SmtpConfiguration>>(
        `/api/notifications/smtp/${id}`,
        { method: 'PUT', body: data }
      ),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/notifications/smtp/${id}`, {
        method: 'DELETE',
      }),
  };

  // ---------------------------------------------------------------------------
  // Payment Gateway Service — Transactions
  // ---------------------------------------------------------------------------

  paymentTransactions = {
    disburse: (data: {
      membreId: string;
      montant: number;
      gateway: string;
      telephone?: string;
      description?: string;
    }) =>
      request<ApiResponse<PaymentTransaction>>(
        '/api/payment-gateways/transactions/disburse',
        { method: 'POST', body: data }
      ),

    collect: (data: {
      membreId: string;
      montant: number;
      gateway: string;
      telephone?: string;
      description?: string;
    }) =>
      request<ApiResponse<PaymentTransaction>>(
        '/api/payment-gateways/transactions/collect',
        { method: 'POST', body: data }
      ),

    getByRef: (reference: string) =>
      request<ApiResponse<PaymentTransaction>>(
        `/api/payment-gateways/transactions/reference/${reference}`,
        { method: 'GET' }
      ),

    list: (params?: {
      page?: number;
      size?: number;
      gateway?: string;
      statut?: string;
      transactionType?: string;
    }) =>
      request<PagedResponse<PaymentTransaction>>(
        '/api/payment-gateways/transactions',
        { method: 'GET', params }
      ),
  };

  // ---------------------------------------------------------------------------
  // Payment Gateway Service — PayDunya Config
  // ---------------------------------------------------------------------------

  paydunya = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<PayDunyaConfig>>(
        '/api/payment-gateways/paydunya',
        { method: 'GET', params }
      ),

    getById: (id: string) =>
      request<ApiResponse<PayDunyaConfig>>(
        `/api/payment-gateways/paydunya/${id}`,
        { method: 'GET' }
      ),

    create: (data: PayDunyaConfigRequest) =>
      request<ApiResponse<PayDunyaConfig>>('/api/payment-gateways/paydunya', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: PayDunyaConfigRequest) =>
      request<ApiResponse<PayDunyaConfig>>(
        `/api/payment-gateways/paydunya/${id}`,
        { method: 'PUT', body: data }
      ),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/payment-gateways/paydunya/${id}`, {
        method: 'DELETE',
      }),

    activate: (id: string) =>
      request<ApiResponse<PayDunyaConfig>>(
        `/api/payment-gateways/paydunya/${id}/activate`,
        { method: 'PUT' }
      ),

    active: () =>
      request<ApiResponse<PayDunyaConfig | null>>(
        '/api/payment-gateways/paydunya/active',
        { method: 'GET' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Payment Gateway Service — PiSpi Config
  // ---------------------------------------------------------------------------

  pispi = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<PiSpiConfig>>('/api/payment-gateways/pispi', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<PiSpiConfig>>(
        `/api/payment-gateways/pispi/${id}`,
        { method: 'GET' }
      ),

    create: (data: PiSpiConfigRequest) =>
      request<ApiResponse<PiSpiConfig>>('/api/payment-gateways/pispi', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: PiSpiConfigRequest) =>
      request<ApiResponse<PiSpiConfig>>(
        `/api/payment-gateways/pispi/${id}`,
        { method: 'PUT', body: data }
      ),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/payment-gateways/pispi/${id}`, {
        method: 'DELETE',
      }),

    activate: (id: string) =>
      request<ApiResponse<PiSpiConfig>>(
        `/api/payment-gateways/pispi/${id}/activate`,
        { method: 'PUT' }
      ),

    active: () =>
      request<ApiResponse<PiSpiConfig | null>>(
        '/api/payment-gateways/pispi/active',
        { method: 'GET' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Payment Gateway Service — Payment Methods
  // ---------------------------------------------------------------------------

  paymentMethods = {
    list: (params?: { page?: number; size?: number }) =>
      request<PagedResponse<PaymentMethod>>('/api/payment-gateways/methods', {
        method: 'GET',
        params,
      }),

    getById: (id: string) =>
      request<ApiResponse<PaymentMethod>>(
        `/api/payment-gateways/methods/${id}`,
        { method: 'GET' }
      ),

    create: (data: PaymentMethodRequest) =>
      request<ApiResponse<PaymentMethod>>('/api/payment-gateways/methods', {
        method: 'POST',
        body: data,
      }),

    update: (id: string, data: PaymentMethodRequest) =>
      request<ApiResponse<PaymentMethod>>(
        `/api/payment-gateways/methods/${id}`,
        { method: 'PUT', body: data }
      ),

    delete: (id: string) =>
      request<ApiResponse<void>>(`/api/payment-gateways/methods/${id}`, {
        method: 'DELETE',
      }),

    byCode: (code: string) =>
      request<ApiResponse<PaymentMethod>>(
        `/api/payment-gateways/methods/code/${code}`,
        { method: 'GET' }
      ),
  };

  // ---------------------------------------------------------------------------
  // Collector Service — Sessions & Collectes
  // ---------------------------------------------------------------------------

  collector = {
    openSession: (data?: { montantOuverture?: number }) =>
      request<ApiResponse<CollecteSession>>('/api/collector/sessions/open', {
        method: 'POST',
        body: data,
      }),

    activeSession: () =>
      request<ApiResponse<CollecteSession | null>>(
        '/api/collector/sessions/active',
        { method: 'GET' }
      ),

    closeSession: (
      sessionId: string,
      data?: { montantFermeture?: number }
    ) =>
      request<ApiResponse<CollecteSession>>(
        `/api/collector/sessions/${sessionId}/close`,
        { method: 'PUT', body: data }
      ),

    sessions: (params?: {
      page?: number;
      size?: number;
      statut?: string;
      startDate?: string;
      endDate?: string;
    }) =>
      request<PagedResponse<CollecteSession>>('/api/collector/sessions', {
        method: 'GET',
        params,
      }),

    collect: (data: CollecteRequest) =>
      request<ApiResponse<Collecte>>('/api/collector/collectes', {
        method: 'POST',
        body: data,
      }),

    confirm: (collecteId: string, data?: { otpCode?: string }) =>
      request<ApiResponse<Collecte>>(
        `/api/collector/collectes/${collecteId}/confirm`,
        { method: 'PUT', body: data }
      ),

    collectes: (params?: {
      sessionId?: string;
      membreId?: string;
      page?: number;
      size?: number;
    }) =>
      request<PagedResponse<Collecte>>('/api/collector/collectes', {
        method: 'GET',
        params,
      }),

    summary: (sessionId: string) =>
      request<ApiResponse<CollecteSessionSummary>>(
        `/api/collector/sessions/${sessionId}/summary`,
        { method: 'GET' }
      ),
  };
}

// =============================================================================
// Singleton Export
// =============================================================================

export const apiClient = new SerenityApiClient();

export default apiClient;
