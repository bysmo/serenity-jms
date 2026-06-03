'use client';

import { useAuthStore } from '@/lib/auth-store';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Users,
  Shield,
  CreditCard,
  PiggyBank,
  ArrowRight,
  LayoutDashboard,
} from 'lucide-react';

export default function HomePage() {
  const { user, hasRole } = useAuthStore();
  const router = useRouter();

  const isAdmin = hasRole('ADMIN');
  const isCollecteur = hasRole('COLLECTEUR');

  const firstName = user?.firstName || user?.username || 'Utilisateur';

  const quickActions = [
    ...(isAdmin
      ? [
          {
            title: 'Administration Keycloak',
            description: 'Gérer les realms, clients, rôles et groupes',
            icon: Shield,
            href: '/admin',
            color: 'text-destructive',
            bgColor: 'bg-destructive/10',
          },
        ]
      : []),
    ...(isAdmin || isCollecteur
      ? [
          {
            title: 'Gestion des membres',
            description: 'Consulter et gérer les comptes membres',
            icon: Users,
            href: '/members',
            color: 'text-primary',
            bgColor: 'bg-primary/10',
          },
          {
            title: 'Paiements',
            description: 'Suivi et gestion des paiements',
            icon: CreditCard,
            href: '/payments',
            color: 'text-green-600',
            bgColor: 'bg-green-50',
          },
          {
            title: 'Épargne',
            description: 'Gestion des comptes d\'épargne',
            icon: PiggyBank,
            href: '/savings',
            color: 'text-blue-600',
            bgColor: 'bg-blue-50',
          },
        ]
      : [
          {
            title: 'Mon épargne',
            description: 'Consulter votre compte d\'épargne',
            icon: PiggyBank,
            href: '/savings',
            color: 'text-primary',
            bgColor: 'bg-primary/10',
          },
        ]),
  ];

  return (
    <div className="p-4 md:p-6 space-y-6">
      {/* Welcome Section */}
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <LayoutDashboard className="h-6 w-6 text-primary" />
          <h1 className="text-2xl font-bold">Tableau de bord</h1>
        </div>
        <p className="text-muted-foreground">
          Bienvenue, <span className="font-medium text-foreground">{firstName}</span> !
          Voici un aperçu de votre espace Serenity.
        </p>
      </div>

      {/* Role Badges */}
      <div className="flex gap-2 flex-wrap">
        {user?.roles
          ?.filter((r) => !['default-roles-serenity', 'uma_authorization', 'offline_access'].includes(r))
          .map((role) => (
            <Badge key={role} variant="secondary" className="text-sm">
              {role === 'ADMIN' ? 'Administrateur' : role === 'COLLECTEUR' ? 'Collecteur' : role === 'MEMBRE' ? 'Membre' : role}
            </Badge>
          ))}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {quickActions.map((action) => (
          <Card
            key={action.title}
            className="cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => router.push(action.href)}
          >
            <CardHeader className="flex flex-row items-center gap-4 space-y-0 pb-2">
              <div className={`h-10 w-10 rounded-lg ${action.bgColor} flex items-center justify-center`}>
                <action.icon className={`h-5 w-5 ${action.color}`} />
              </div>
              <div className="flex-1">
                <CardTitle className="text-base">{action.title}</CardTitle>
                <CardDescription className="text-sm">{action.description}</CardDescription>
              </div>
            </CardHeader>
            <CardContent className="pt-0">
              <Button variant="ghost" size="sm" className="text-primary p-0 h-auto">
                Accéder <ArrowRight className="ml-1 h-4 w-4" />
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
