'use client';

import { useState } from 'react';
import { apiClient } from '@/lib/api-client';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { useToast } from '@/hooks/use-toast';
import Link from 'next/link';

export default function ForgotPasswordPage() {
  const { toast } = useToast();
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await apiClient.auth.forgotPassword(email);
      setSent(true);
      toast({
        variant: 'success',
        title: 'E-mail envoyé',
        description: 'Si un compte existe avec cette adresse, vous recevrez un e-mail de réinitialisation.',
      });
    } catch (err: any) {
      console.error('Forgot password error', err);
      const msg = err?.message || 'Une erreur est survenue. Veuillez réessayer.';
      setError(msg);
      toast({
        variant: 'destructive',
        title: 'Erreur',
        description: msg,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="border-0 shadow-none lg:border lg:shadow-sm">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold">Mot de passe oublié</CardTitle>
        <CardDescription>
          {sent
            ? 'Vérifiez votre boîte de réception pour le lien de réinitialisation.'
            : 'Entrez votre adresse e-mail pour recevoir un lien de réinitialisation.'}
        </CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
          {error && (
            <div className="rounded-md bg-destructive/15 p-3 text-sm text-destructive font-medium border border-destructive/25">
              {error}
            </div>
          )}

          {sent ? (
            <div className="rounded-md bg-primary/10 p-4 text-sm text-primary font-medium border border-primary/20">
              Un e-mail de réinitialisation a été envoyé à <strong>{email}</strong>.
              Suivez les instructions dans l&apos;e-mail pour réinitialiser votre mot de passe.
            </div>
          ) : (
            <div className="space-y-2">
              <Label htmlFor="email">Adresse e-mail</Label>
              <Input
                id="email"
                type="email"
                placeholder="jean.dupont@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoComplete="email"
                disabled={loading}
              />
            </div>
          )}
        </CardContent>
        <CardFooter className="flex flex-col gap-4 mt-4">
          {!sent && (
            <Button type="submit" className="w-full" disabled={loading || !email}>
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="size-4 animate-spin rounded-full border-2 border-primary-foreground border-t-transparent" />
                  Envoi en cours...
                </span>
              ) : (
                'Envoyer le lien de réinitialisation'
              )}
            </Button>
          )}
          <p className="text-center text-sm text-muted-foreground">
            <Link href="/login" className="text-primary hover:underline font-medium">
              Retour à la connexion
            </Link>
          </p>
        </CardFooter>
      </form>
    </Card>
  );
}
