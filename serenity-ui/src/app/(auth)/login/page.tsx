'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/auth-store';
import { apiClient } from '@/lib/api-client';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Checkbox } from '@/components/ui/checkbox';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';

export default function LoginPage() {
  const router = useRouter();
  const { login, updateUser } = useAuthStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      // 1. Call identity-service /api/v1/auth/login through the API Gateway
      const response = await apiClient.auth.login({
        username: email,
        password,
        clientId: 'serenity-ui'
      });
      
      if (response && response.data) {
        // Parse token and populate basic auth store state
        login(response.data.accessToken, response.data.refreshToken, {
          id: '',
          email: email,
          firstName: '',
          lastName: '',
          roles: []
        });

        // 2. Fetch authenticated user details from /api/v1/auth/me to get the actual Keycloak roles
        try {
          const userResponse = await apiClient.auth.me();
          if (userResponse && userResponse.data) {
            updateUser({
              id: userResponse.data.id || '',
              email: userResponse.data.email || email,
              firstName: userResponse.data.firstName || '',
              lastName: userResponse.data.lastName || '',
              roles: userResponse.data.roles || []
            });
          }
        } catch (meErr) {
          console.error('Failed to fetch user profile details', meErr);
        }

        // 3. Redirect to dashboard
        router.push('/');
      } else {
        throw new Error('Identifiants de connexion invalides.');
      }
    } catch (err: any) {
      console.error('Authentication error', err);
      setError(
        err?.message || 'Nom d\'utilisateur ou mot de passe incorrect.'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Card className="border-0 shadow-none lg:border lg:shadow-sm">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold">Connexion</CardTitle>
        <CardDescription>
          Entrez vos identifiants pour accéder à votre espace
        </CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
          {error && (
            <div className="rounded-md bg-destructive/15 p-3 text-sm text-destructive font-medium border border-destructive/25">
              {error}
            </div>
          )}
          <div className="space-y-2">
            <Label htmlFor="email">Email ou nom d&apos;utilisateur</Label>
            <Input
              id="email"
              type="text"
              placeholder="admin@serenity.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <Label htmlFor="password">Mot de passe</Label>
              <Link
                href="/forgot-password"
                className="text-xs text-primary hover:underline"
              >
                Mot de passe oublié ?
              </Link>
            </div>
            <Input
              id="password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="flex items-center gap-2">
            <Checkbox id="remember" />
            <Label htmlFor="remember" className="text-sm font-normal cursor-pointer">
              Se souvenir de moi
            </Label>
          </div>
        </CardContent>
        <CardFooter className="flex flex-col gap-4">
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? (
              <span className="flex items-center gap-2">
                <span className="size-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                Connexion en cours...
              </span>
            ) : (
              'Se connecter'
            )}
          </Button>
          <p className="text-center text-sm text-muted-foreground">
            Pas encore de compte ?{' '}
            <Link href="/register" className="text-primary hover:underline font-medium">
              Créer un compte
            </Link>
          </p>
        </CardFooter>
      </form>
    </Card>
  );
}
