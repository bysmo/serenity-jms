// =============================================================================
// Serenity-JMS Auth Store (Zustand + Persist)
// =============================================================================

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

interface AuthState {
  token: string | null;
  refreshToken: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (token: string, refreshToken: string, user: AuthUser) => void;
  logout: () => void;
  updateUser: (user: AuthUser) => void;
  setToken: (token: string) => void;
  setRefreshToken: (refreshToken: string) => void;
  hasRole: (role: string) => boolean;
  hasAnyRole: (...roles: string[]) => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      refreshToken: null,
      user: null,
      isAuthenticated: false,

      login: (token, refreshToken, user) =>
        set({ token, refreshToken, user, isAuthenticated: true }),

      logout: () =>
        set({
          token: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
        }),

      updateUser: (user) => set({ user }),

      setToken: (token) => set({ token }),

      setRefreshToken: (refreshToken) => set({ refreshToken }),

      hasRole: (role: string): boolean => {
        const { user } = get();
        return user?.roles?.includes(role) ?? false;
      },

      hasAnyRole: (...roles: string[]): boolean => {
        const { user } = get();
        if (!user?.roles) return false;
        return roles.some((role) => user.roles.includes(role));
      },
    }),
    {
      name: 'serenity-auth',
    }
  )
);
