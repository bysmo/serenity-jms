'use client';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Users, CreditCard, ClipboardCheck } from 'lucide-react';

export default function CollectorPage() {
  return (
    <div className="p-4 md:p-6 space-y-6">
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <ClipboardCheck className="h-6 w-6 text-primary" />
          <h1 className="text-2xl font-bold">Espace Collecteur</h1>
        </div>
        <p className="text-muted-foreground">
          Gérez vos collectes, paiements et le suivi des membres.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center gap-4 space-y-0">
            <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
              <Users className="h-5 w-5 text-primary" />
            </div>
            <div>
              <CardTitle className="text-base">Mes membres</CardTitle>
              <CardDescription>Consulter la liste de vos membres assignés</CardDescription>
            </div>
          </CardHeader>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center gap-4 space-y-0">
            <div className="h-10 w-10 rounded-lg bg-green-50 flex items-center justify-center">
              <CreditCard className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <CardTitle className="text-base">Collectes du jour</CardTitle>
              <CardDescription>Enregistrer de nouveaux paiements</CardDescription>
            </div>
          </CardHeader>
        </Card>
      </div>
    </div>
  );
}
