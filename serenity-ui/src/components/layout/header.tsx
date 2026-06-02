'use client';

import { usePathname } from 'next/navigation';
import { useTheme } from 'next-themes';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb';
import {
  Search,
  Sun,
  Moon,
  Bell,
  User,
  Settings,
  LogOut,
} from 'lucide-react';

const pathNameMap: Record<string, string> = {
  '/': 'Tableau de bord',
  '/members': 'Membres',
  '/members/segments': 'Segments',
  '/members/kyc': 'Vérifications KYC',
  '/members/parrainage': 'Parrainage',
  '/cotisations': 'Cotisations',
  '/cotisations/engagements': 'Engagements',
  '/cotisations/paiements': 'Paiements',
  '/cotisations/adhesions': 'Adhésions',
  '/cotisations/versement-demandes': 'Demandes de versement',
  '/cotisations/remboursements': 'Remboursements',
  '/epargne': "Plans d'épargne",
  '/epargne/souscriptions': 'Souscriptions',
  '/nano-credits': 'Nano-Crédit',
  '/nano-credits/paliers': 'Paliers',
  '/payments': 'Transactions',
  '/payments/methods': 'Méthodes de paiement',
  '/payments/paydunya': 'Configuration PayDunya',
  '/payments/pispi': 'Configuration Pi-SPI',
  '/notifications': 'Journal',
  '/notifications/templates': 'Modèles email',
  '/notifications/sms-gateways': 'Passerelles SMS',
  '/notifications/smtp': 'Configuration SMTP',
  '/admin/settings': 'Paramètres',
  '/admin/tags': 'Tags',
  '/admin/announcements': 'Annonces',
  '/admin/audit-logs': "Journal d'audit",
  '/admin/auto-numbering': 'Numérotation auto',
  '/collector': 'Sessions collecteur',
};

function buildBreadcrumbs(pathname: string) {
  if (pathname === '/') {
    return [{ label: 'Tableau de bord', href: '/', isLast: true }];
  }

  const segments = pathname.split('/').filter(Boolean);
  const breadcrumbs: { label: string; href: string; isLast: boolean }[] = [];
  let currentPath = '';

  segments.forEach((segment, index) => {
    currentPath += '/' + segment;
    const isLast = index === segments.length - 1;
    const label = pathNameMap[currentPath] || segment.charAt(0).toUpperCase() + segment.slice(1).replace(/-/g, ' ');
    breadcrumbs.push({ label, href: currentPath, isLast });
  });

  return breadcrumbs;
}

export function Header() {
  const pathname = usePathname();
  const { setTheme, theme } = useTheme();
  const breadcrumbs = buildBreadcrumbs(pathname);

  return (
    <header className="flex h-14 shrink-0 items-center gap-4 border-b bg-background px-6">
      {/* Breadcrumbs */}
      <Breadcrumb className="flex-1">
        <BreadcrumbList>
          {breadcrumbs.map((crumb, index) => (
            <span key={crumb.href} className="contents">
              {index > 0 && <BreadcrumbSeparator />}
              <BreadcrumbItem>
                {crumb.isLast ? (
                  <BreadcrumbPage>{crumb.label}</BreadcrumbPage>
                ) : (
                  <BreadcrumbLink href={crumb.href}>{crumb.label}</BreadcrumbLink>
                )}
              </BreadcrumbItem>
            </span>
          ))}
        </BreadcrumbList>
      </Breadcrumb>

      {/* Search */}
      <div className="hidden md:flex items-center relative">
        <Search className="absolute left-2.5 size-4 text-muted-foreground" />
        <Input
          type="search"
          placeholder="Rechercher..."
          className="h-8 w-[200px] pl-8 lg:w-[260px]"
        />
      </div>

      <Separator orientation="vertical" className="h-6" />

      {/* Theme toggle */}
      <Button
        variant="ghost"
        size="icon"
        className="size-8"
        onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
        aria-label="Basculer le thème"
      >
        <Sun className="size-4 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
        <Moon className="absolute size-4 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
      </Button>

      {/* Notifications */}
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" size="icon" className="relative size-8">
            <Bell className="size-4" />
            <span className="absolute -right-0.5 -top-0.5 flex size-4 items-center justify-center rounded-full bg-destructive text-[10px] font-bold text-white">
              3
            </span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-72">
          <DropdownMenuLabel>Notifications</DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuItem className="flex flex-col items-start gap-1 py-2">
            <span className="text-sm font-medium">Nouveau membre inscrit</span>
            <span className="text-xs text-muted-foreground">Il y a 5 minutes</span>
          </DropdownMenuItem>
          <DropdownMenuItem className="flex flex-col items-start gap-1 py-2">
            <span className="text-sm font-medium">Cotisation reçue</span>
            <span className="text-xs text-muted-foreground">Il y a 30 minutes</span>
          </DropdownMenuItem>
          <DropdownMenuItem className="flex flex-col items-start gap-1 py-2">
            <span className="text-sm font-medium">Demande de versement en attente</span>
            <span className="text-xs text-muted-foreground">Il y a 2 heures</span>
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          <DropdownMenuItem className="justify-center text-primary font-medium">
            Voir toutes les notifications
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      {/* User dropdown */}
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="relative h-8 gap-2 px-2">
            <Avatar className="size-7">
              <AvatarFallback className="bg-primary text-primary-foreground text-xs">
                AD
              </AvatarFallback>
            </Avatar>
            <span className="hidden text-sm font-medium sm:inline-block">Admin</span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end" className="w-56">
          <DropdownMenuLabel className="font-normal">
            <div className="flex flex-col gap-1">
              <p className="text-sm font-medium">Admin</p>
              <p className="text-xs text-muted-foreground">admin@serenity.jms</p>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem>
              <User className="mr-2 size-4" />
              Profil
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Settings className="mr-2 size-4" />
              Paramètres
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem variant="destructive">
            <LogOut className="mr-2 size-4" />
            Déconnexion
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </header>
  );
}
