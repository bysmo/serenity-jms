'use client';

import { ShieldCheck } from 'lucide-react';

export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen">
      {/* Left panel - branding */}
      <div className="hidden lg:flex lg:w-1/2 relative flex-col items-center justify-center bg-primary p-12 text-primary-foreground">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_30%_40%,rgba(255,255,255,0.1),transparent_60%)]" />
        <div className="relative z-10 flex max-w-md flex-col items-center text-center">
          <ShieldCheck className="size-16 mb-6" />
          <h1 className="text-4xl font-bold tracking-tight">Serenity</h1>
          <p className="mt-4 text-lg text-primary-foreground/80">
            Plateforme de gestion mutualiste intelligente. Gérez vos membres,
            cotisations, épargne et nano-crédits en toute simplicité.
          </p>
          <div className="mt-8 flex gap-8 text-sm text-primary-foreground/60">
            <div className="flex flex-col items-center gap-1">
              <span className="text-3xl font-bold text-primary-foreground">2.4K+</span>
              <span>Membres actifs</span>
            </div>
            <div className="flex flex-col items-center gap-1">
              <span className="text-3xl font-bold text-primary-foreground">98%</span>
              <span>Satisfaction</span>
            </div>
            <div className="flex flex-col items-center gap-1">
              <span className="text-3xl font-bold text-primary-foreground">24/7</span>
              <span>Disponibilité</span>
            </div>
          </div>
        </div>
      </div>

      {/* Right panel - auth form */}
      <div className="flex w-full items-center justify-center p-6 lg:w-1/2">
        <div className="w-full max-w-md">
          {/* Mobile branding */}
          <div className="mb-8 flex items-center justify-center gap-2 lg:hidden">
            <ShieldCheck className="size-8 text-primary" />
            <span className="text-2xl font-bold">Serenity</span>
          </div>
          {children}
        </div>
      </div>
    </div>
  );
}
