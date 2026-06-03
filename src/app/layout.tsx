'use client';

import { useEffect, useSyncExternalStore } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import { Geist, Geist_Mono } from 'next/font/google';
import { useAuthStore } from '@/lib/auth-store';
import { Header } from '@/components/header';
import { AccessDenied } from '@/components/access-denied';
import { Toaster } from 'sonner';

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

// Public paths that don't require authentication
const PUBLIC_PATHS = ['/login', '/register', '/forgot-password'];

// Role-based path access mapping
const ROLE_PATH_MAP: Record<string, string> = {
  '/admin': 'ADMIN',
  '/collector': 'COLLECTEUR',
};

// Hydration-safe mounted check using useSyncExternalStore
const emptySubscribe = () => () => {};
function useMounted() {
  return useSyncExternalStore(
    emptySubscribe,
    () => true,
    () => false
  );
}

function LayoutShell({ children }: { children: React.ReactNode }) {
  return (
    <html lang="fr" suppressHydrationWarning>
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased bg-background text-foreground`}
      >
        {children}
        <Toaster position="top-right" richColors closeButton />
      </body>
    </html>
  );
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const pathname = usePathname();
  const router = useRouter();
  const { user, isAuthenticated, hasRole, logout } = useAuthStore();
  const mounted = useMounted();

  useEffect(() => {
    const isPublicPath = PUBLIC_PATHS.some(
      (path) => pathname === path || pathname.startsWith(path + '/')
    );

    if (isPublicPath) {
      if (isAuthenticated && (pathname === '/login' || pathname === '/register')) {
        router.push('/');
      }
      return;
    }

    if (!isAuthenticated || !user?.accessToken) {
      router.push('/login');
      return;
    }

    // Check token expiration
    if (user.expiresAt && Date.now() >= user.expiresAt) {
      import('@/lib/api-client').then(({ apiClient }) => {
        apiClient.auth.me().catch(() => {
          logout();
          router.push('/login');
        });
      });
    }
  }, [pathname, isAuthenticated, user, router, logout]);

  // Prevent flash of content during hydration
  if (!mounted) {
    return (
      <LayoutShell>
        <div className="min-h-screen flex items-center justify-center">
          <div className="animate-pulse text-muted-foreground">Chargement...</div>
        </div>
      </LayoutShell>
    );
  }

  // Check if path is public
  const isPublicPath = PUBLIC_PATHS.some(
    (path) => pathname === path || pathname.startsWith(path + '/')
  );

  // For public paths, render without header
  if (isPublicPath) {
    return (
      <LayoutShell>
        {children}
      </LayoutShell>
    );
  }

  // Not authenticated - show redirect message
  if (!isAuthenticated) {
    return (
      <LayoutShell>
        <div className="min-h-screen flex items-center justify-center">
          <div className="animate-pulse text-muted-foreground">Redirection...</div>
        </div>
      </LayoutShell>
    );
  }

  // Role-based authorization check
  let requiredRole: string | null = null;
  for (const [pathPrefix, role] of Object.entries(ROLE_PATH_MAP)) {
    if (pathname.startsWith(pathPrefix)) {
      requiredRole = role;
      break;
    }
  }

  if (requiredRole && !hasRole(requiredRole)) {
    return (
      <LayoutShell>
        <Header />
        <AccessDenied requiredRole={requiredRole} />
      </LayoutShell>
    );
  }

  // Authenticated with proper role - render full layout
  return (
    <LayoutShell>
      <div className="min-h-screen flex flex-col">
        <Header />
        <main className="flex-1">{children}</main>
      </div>
    </LayoutShell>
  );
}
