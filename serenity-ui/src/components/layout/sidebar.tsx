'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {
  ShieldCheck,
  LayoutDashboard,
  Users,
  Layers,
  Shield,
  Gift,
  Repeat,
  Handshake,
  CreditCard,
  UserPlus,
  ArrowDownCircle,
  RotateCcw,
  PiggyBank,
  FileText,
  Banknote,
  BarChart3,
  ArrowLeftRight,
  Wallet,
  Settings,
  Bell,
  Mail,
  Smartphone,
  Server,
  Tag,
  Megaphone,
  ScrollText,
  Hash,
  ClipboardList,
  ChevronLeft,
  ChevronDown,
  LogOut,
} from 'lucide-react';

interface NavItem {
  title: string;
  href: string;
  icon: React.ReactNode;
}

interface NavSection {
  title: string;
  icon: React.ReactNode;
  items: NavItem[];
}

const navigation: NavSection[] = [
  {
    title: 'Membres',
    icon: <Users className="size-4" />,
    items: [
      { title: 'Liste des membres', href: '/members', icon: <Users className="size-4" /> },
      { title: 'Segments', href: '/members/segments', icon: <Layers className="size-4" /> },
      { title: 'Vérifications KYC', href: '/members/kyc', icon: <Shield className="size-4" /> },
      { title: 'Parrainage', href: '/members/parrainage', icon: <Gift className="size-4" /> },
    ],
  },
  {
    title: 'Cotisations',
    icon: <Repeat className="size-4" />,
    items: [
      { title: 'Cotisations', href: '/cotisations', icon: <Repeat className="size-4" /> },
      { title: 'Engagements', href: '/cotisations/engagements', icon: <Handshake className="size-4" /> },
      { title: 'Paiements', href: '/cotisations/paiements', icon: <CreditCard className="size-4" /> },
      { title: 'Adhésions', href: '/cotisations/adhesions', icon: <UserPlus className="size-4" /> },
      { title: 'Demandes de versement', href: '/cotisations/versement-demandes', icon: <ArrowDownCircle className="size-4" /> },
      { title: 'Remboursements', href: '/cotisations/remboursements', icon: <RotateCcw className="size-4" /> },
    ],
  },
  {
    title: 'Épargne',
    icon: <PiggyBank className="size-4" />,
    items: [
      { title: "Plans d'épargne", href: '/epargne', icon: <PiggyBank className="size-4" /> },
      { title: 'Souscriptions', href: '/epargne/souscriptions', icon: <FileText className="size-4" /> },
    ],
  },
  {
    title: 'Nano-Crédit',
    icon: <Banknote className="size-4" />,
    items: [
      { title: 'Nano-crédits', href: '/nano-credits', icon: <Banknote className="size-4" /> },
      { title: 'Paliers', href: '/nano-credits/paliers', icon: <BarChart3 className="size-4" /> },
    ],
  },
  {
    title: 'Paiements',
    icon: <ArrowLeftRight className="size-4" />,
    items: [
      { title: 'Transactions', href: '/payments', icon: <ArrowLeftRight className="size-4" /> },
      { title: 'Méthodes de paiement', href: '/payments/methods', icon: <Wallet className="size-4" /> },
      { title: 'Configuration PayDunya', href: '/payments/paydunya', icon: <Settings className="size-4" /> },
      { title: 'Configuration Pi-SPI', href: '/payments/pispi', icon: <Settings className="size-4" /> },
    ],
  },
  {
    title: 'Notifications',
    icon: <Bell className="size-4" />,
    items: [
      { title: 'Journal', href: '/notifications', icon: <Bell className="size-4" /> },
      { title: 'Modèles email', href: '/notifications/templates', icon: <Mail className="size-4" /> },
      { title: 'Passerelles SMS', href: '/notifications/sms-gateways', icon: <Smartphone className="size-4" /> },
      { title: 'Configuration SMTP', href: '/notifications/smtp', icon: <Server className="size-4" /> },
    ],
  },
  {
    title: 'Administration',
    icon: <Settings className="size-4" />,
    items: [
      { title: 'Paramètres', href: '/admin/settings', icon: <Settings className="size-4" /> },
      { title: 'Tags', href: '/admin/tags', icon: <Tag className="size-4" /> },
      { title: 'Annonces', href: '/admin/announcements', icon: <Megaphone className="size-4" /> },
      { title: "Journal d'audit", href: '/admin/audit-logs', icon: <ScrollText className="size-4" /> },
      { title: 'Numérotation auto', href: '/admin/auto-numbering', icon: <Hash className="size-4" /> },
    ],
  },
  {
    title: 'Collecteur',
    icon: <ClipboardList className="size-4" />,
    items: [
      { title: 'Sessions', href: '/collector', icon: <ClipboardList className="size-4" /> },
    ],
  },
];

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
}

export function Sidebar({ collapsed, onToggle }: SidebarProps) {
  const pathname = usePathname();
  const [openSections, setOpenSections] = useState<Record<string, boolean>>(() => {
    const initial: Record<string, boolean> = {};
    navigation.forEach((section) => {
      const isInSection = section.items.some(
        (item) => pathname === item.href || pathname.startsWith(item.href + '/')
      );
      if (isInSection) {
        initial[section.title] = true;
      }
    });
    return initial;
  });

  const toggleSection = (title: string) => {
    setOpenSections((prev) => ({ ...prev, [title]: !prev[title] }));
  };

  const isActive = (href: string) => {
    return pathname === href || pathname.startsWith(href + '/');
  };

  return (
    <TooltipProvider delayDuration={0}>
      <aside
        className={cn(
          'relative flex flex-col border-r border-sidebar-border bg-sidebar text-sidebar-foreground transition-all duration-300 ease-in-out',
          collapsed ? 'w-[68px]' : 'w-[260px]'
        )}
      >
        {/* Brand */}
        <div className="flex h-14 items-center border-b border-sidebar-border px-4">
          <Link href="/" className="flex items-center gap-2 overflow-hidden">
            <ShieldCheck className="size-7 shrink-0 text-primary" />
            {!collapsed && (
              <span className="text-lg font-bold tracking-tight whitespace-nowrap">
                Serenity
              </span>
            )}
          </Link>
        </div>

        {/* Toggle button */}
        <Button
          variant="outline"
          size="icon"
          className="absolute -right-3 top-16 z-10 size-6 rounded-full border bg-background shadow-sm hover:bg-accent"
          onClick={onToggle}
          aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          <ChevronLeft
            className={cn(
              'size-3 transition-transform duration-300',
              collapsed && 'rotate-180'
            )}
          />
        </Button>

        {/* Navigation */}
        <ScrollArea className="flex-1 py-2">
          <nav className="flex flex-col gap-1 px-2">
            {/* Dashboard - standalone link */}
            <Tooltip>
              <TooltipTrigger asChild>
                <Link
                  href="/"
                  className={cn(
                    'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground',
                    isActive('/') && !pathname.startsWith('/members') && !pathname.startsWith('/cotisations') && !pathname.startsWith('/epargne') && !pathname.startsWith('/nano-credits') && !pathname.startsWith('/payments') && !pathname.startsWith('/notifications') && !pathname.startsWith('/admin') && !pathname.startsWith('/collector')
                      ? 'bg-sidebar-accent text-sidebar-primary font-semibold'
                      : 'text-sidebar-foreground/70'
                  )}
                >
                  <LayoutDashboard className="size-4 shrink-0" />
                  {!collapsed && <span>Tableau de bord</span>}
                </Link>
              </TooltipTrigger>
              {collapsed && (
                <TooltipContent side="right">Tableau de bord</TooltipContent>
              )}
            </Tooltip>

            <Separator className="my-1 bg-sidebar-border" />

            {/* Sections */}
            {navigation.map((section) => {
              const isSectionActive = section.items.some((item) => isActive(item.href));

              if (collapsed) {
                return (
                  <div key={section.title} className="flex flex-col gap-1">
                    {section.items.map((item) => (
                      <Tooltip key={item.href}>
                        <TooltipTrigger asChild>
                          <Link
                            href={item.href}
                            className={cn(
                              'flex items-center gap-3 rounded-md px-3 py-2 text-sm transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground justify-center',
                              isActive(item.href)
                                ? 'bg-sidebar-accent text-sidebar-primary font-semibold'
                                : 'text-sidebar-foreground/70'
                            )}
                          >
                            {item.icon}
                          </Link>
                        </TooltipTrigger>
                        <TooltipContent side="right">{item.title}</TooltipContent>
                      </Tooltip>
                    ))}
                    <Separator className="my-1 bg-sidebar-border" />
                  </div>
                );
              }

              return (
                <Collapsible
                  key={section.title}
                  open={openSections[section.title]}
                  onOpenChange={() => toggleSection(section.title)}
                >
                  <CollapsibleTrigger asChild>
                    <button
                      className={cn(
                        'flex w-full items-center gap-2 rounded-md px-3 py-2 text-xs font-semibold uppercase tracking-wider transition-colors hover:bg-sidebar-accent/50',
                        isSectionActive
                          ? 'text-sidebar-primary'
                          : 'text-sidebar-foreground/50'
                      )}
                    >
                      <span className="shrink-0">{section.icon}</span>
                      <span className="flex-1 text-left">{section.title}</span>
                      <ChevronDown
                        className={cn(
                          'size-3.5 shrink-0 transition-transform duration-200',
                          openSections[section.title] && 'rotate-180'
                        )}
                      />
                    </button>
                  </CollapsibleTrigger>
                  <CollapsibleContent>
                    <div className="flex flex-col gap-0.5 pl-3">
                      {section.items.map((item) => (
                        <Link
                          key={item.href}
                          href={item.href}
                          className={cn(
                            'flex items-center gap-2.5 rounded-md px-3 py-1.5 text-sm transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground',
                            isActive(item.href)
                              ? 'bg-sidebar-accent text-sidebar-primary font-medium'
                              : 'text-sidebar-foreground/70'
                          )}
                        >
                          <span className="shrink-0">{item.icon}</span>
                          <span className="truncate">{item.title}</span>
                        </Link>
                      ))}
                    </div>
                  </CollapsibleContent>
                </Collapsible>
              );
            })}
          </nav>
        </ScrollArea>

        {/* User section */}
        <Separator className="bg-sidebar-border" />
        <div className={cn('flex items-center gap-3 p-3', collapsed && 'justify-center')}>
          <Avatar className="size-8 shrink-0">
            <AvatarFallback className="bg-primary text-primary-foreground text-xs">
              AD
            </AvatarFallback>
          </Avatar>
          {!collapsed && (
            <div className="flex flex-1 items-center justify-between overflow-hidden">
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-medium">Admin</p>
                <p className="truncate text-xs text-sidebar-foreground/50">
                  admin@serenity.jms
                </p>
              </div>
              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="size-8 shrink-0 text-sidebar-foreground/50 hover:text-destructive"
                  >
                    <LogOut className="size-4" />
                  </Button>
                </TooltipTrigger>
                <TooltipContent side="right">Déconnexion</TooltipContent>
              </Tooltip>
            </div>
          )}
          {collapsed && (
            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="size-8 text-sidebar-foreground/50 hover:text-destructive"
                >
                  <LogOut className="size-4" />
                </Button>
              </TooltipTrigger>
              <TooltipContent side="right">Déconnexion</TooltipContent>
            </Tooltip>
          )}
        </div>
      </aside>
    </TooltipProvider>
  );
}
