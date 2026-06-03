'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/auth-store';
import { Sidebar } from '@/components/layout/sidebar';
import { Header } from '@/components/layout/header';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const router = useRouter();
  const { isAuthenticated, token, logout } = useAuthStore();
  const [isClient, setIsClient] = useState(false);
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);

  useEffect(() => {
    setIsClient(true);
  }, []);

  useEffect(() => {
    if (!isClient) return;

    const verifyAuth = async () => {
      if (!isAuthenticated || !token) {
        router.push('/login');
        setIsCheckingAuth(false);
        return;
      }

      // Check if token is expired
      let expired = false;
      try {
        const parts = token.split('.');
        if (parts.length >= 2) {
          const payload = JSON.parse(atob(parts[1]));
          if (payload.exp && Date.now() >= payload.exp * 1000) {
            expired = true;
          }
        } else {
          expired = true;
        }
      } catch {
        expired = true;
      }

      if (expired) {
        // Try to validate/refresh token by calling me()
        try {
          const { apiClient } = await import('@/lib/api-client');
          await apiClient.auth.me();
          setIsCheckingAuth(false);
        } catch (err) {
          console.error("Auth validation failed, logging out", err);
          logout();
          router.push('/login');
          setIsCheckingAuth(false);
        }
      } else {
        setIsCheckingAuth(false);
      }
    };

    verifyAuth();
  }, [isClient, isAuthenticated, token, router, logout]);

  if (!isClient || isCheckingAuth) {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-background">
        <div className="size-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="flex h-screen overflow-hidden bg-background">
      <Sidebar collapsed={sidebarCollapsed} onToggle={() => setSidebarCollapsed(!sidebarCollapsed)} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-4 lg:p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
