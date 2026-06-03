'use client';

import { useAuthStore } from '@/lib/auth-store';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { User, Mail, Shield, Calendar } from 'lucide-react';

export default function ProfilePage() {
  const { user } = useAuthStore();

  if (!user) return null;

  const initials = [
    user.firstName?.[0] || '',
    user.lastName?.[0] || '',
  ]
    .join('')
    .toUpperCase() || user.username?.[0]?.toUpperCase() || 'U';

  const displayName = user.firstName && user.lastName
    ? `${user.firstName} ${user.lastName}`
    : user.username || 'Utilisateur';

  const roleLabels: Record<string, string> = {
    ADMIN: 'Administrateur',
    COLLECTEUR: 'Collecteur',
    MEMBRE: 'Membre',
  };

  const displayRoles = user.roles
    .filter((r) => !['default-roles-serenity', 'uma_authorization', 'offline_access'].includes(r));

  return (
    <div className="p-4 md:p-6 space-y-6 max-w-2xl">
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <User className="h-6 w-6 text-primary" />
          <h1 className="text-2xl font-bold">Mon profil</h1>
        </div>
        <p className="text-muted-foreground">
          Informations de votre compte Serenity.
        </p>
      </div>

      <Card>
        <CardHeader className="flex flex-row items-center gap-6">
          <Avatar className="h-20 w-20">
            <AvatarFallback className="bg-primary text-primary-foreground text-2xl">
              {initials}
            </AvatarFallback>
          </Avatar>
          <div className="space-y-1">
            <CardTitle className="text-xl">{displayName}</CardTitle>
            <CardDescription>{user.username}</CardDescription>
            <div className="flex gap-1 flex-wrap pt-1">
              {displayRoles.map((role) => (
                <Badge key={role} variant="secondary" className="text-xs">
                  {roleLabels[role] || role}
                </Badge>
              ))}
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <Separator />

          <div className="grid gap-4">
            <div className="flex items-center gap-3">
              <Mail className="h-4 w-4 text-muted-foreground" />
              <div>
                <p className="text-sm text-muted-foreground">Adresse e-mail</p>
                <p className="font-medium">{user.email || 'Non renseigné'}</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <User className="h-4 w-4 text-muted-foreground" />
              <div>
                <p className="text-sm text-muted-foreground">Prénom</p>
                <p className="font-medium">{user.firstName || 'Non renseigné'}</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <User className="h-4 w-4 text-muted-foreground" />
              <div>
                <p className="text-sm text-muted-foreground">Nom</p>
                <p className="font-medium">{user.lastName || 'Non renseigné'}</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <Shield className="h-4 w-4 text-muted-foreground" />
              <div>
                <p className="text-sm text-muted-foreground">Identifiant</p>
                <p className="font-medium font-mono text-sm">{user.id || 'N/A'}</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
