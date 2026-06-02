'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/layout/sidebar';
import { Header } from '@/components/layout/header';
import { useToast } from '@/hooks/use-toast';
import { StatCard } from '@/components/shared/stat-card';
import { PageHeader } from '@/components/shared/page-header';
import { StatusBadge } from '@/components/shared/status-badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Users,
  Repeat,
  CreditCard,
  TrendingUp,
  ArrowUpRight,
  Plus,
} from 'lucide-react';

const recentActivity = [
  { id: 1, member: 'Amadou Diallo', action: 'Nouvelle adhésion', status: 'active', date: 'Il y a 5 min' },
  { id: 2, member: 'Fatou Sow', action: 'Cotisation mensuelle', status: 'paid', date: 'Il y a 12 min' },
  { id: 3, member: 'Ibrahima Ndiaye', action: 'Demande de nano-crédit', status: 'pending', date: 'Il y a 25 min' },
  { id: 4, member: 'Mariama Ba', action: 'Vérification KYC', status: 'processing', date: 'Il y a 1h' },
  { id: 5, member: 'Ousmane Sy', action: 'Remboursement échoué', status: 'failed', date: 'Il y a 2h' },
];

export default function DashboardPage() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const { toast } = useToast();

  return (
    <div className="flex h-screen overflow-hidden bg-background">
      <Sidebar collapsed={sidebarCollapsed} onToggle={() => setSidebarCollapsed(!sidebarCollapsed)} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6 space-y-6">
          <PageHeader
            title="Tableau de bord"
            description="Vue d'ensemble de votre plateforme mutualiste"
            actions={
              <Button>
                <Plus className="mr-2 size-4" />
                Nouveau membre
              </Button>
            }
          />

          {/* Stats grid */}
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <StatCard
              title="Membres actifs"
              value="2,421"
              icon={<Users className="size-5" />}
              trend={{ value: 12.5, positive: true }}
              description="vs mois dernier"
            />
            <StatCard
              title="Cotisations"
              value="₣ 4.2M"
              icon={<Repeat className="size-5" />}
              trend={{ value: 8.2, positive: true }}
              description="ce mois"
            />
            <StatCard
              title="Paiements reçus"
              value="₣ 1.8M"
              icon={<CreditCard className="size-5" />}
              trend={{ value: -3.1, positive: false }}
              description="vs mois dernier"
            />
            <StatCard
              title="Épargne totale"
              value="₣ 12.6M"
              icon={<TrendingUp className="size-5" />}
              trend={{ value: 5.4, positive: true }}
              description="vs mois dernier"
            />
          </div>

          {/* Recent activity and Quick actions */}
          <div className="grid gap-6 lg:grid-cols-3">
            {/* Recent activity */}
            <Card className="lg:col-span-2">
              <CardHeader>
                <CardTitle className="text-lg">Activité récente</CardTitle>
                <CardDescription>Dernières actions sur la plateforme</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {recentActivity.map((activity) => (
                    <div
                      key={activity.id}
                      className="flex items-center justify-between gap-4 rounded-lg border p-3 transition-colors hover:bg-muted/50"
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <div className="flex size-9 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary text-xs font-semibold">
                          {activity.member.split(' ').map(n => n[0]).join('')}
                        </div>
                        <div className="min-w-0">
                          <p className="text-sm font-medium truncate">{activity.member}</p>
                          <p className="text-xs text-muted-foreground">{activity.action}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-3 shrink-0">
                        <StatusBadge statut={activity.status} />
                        <span className="text-xs text-muted-foreground hidden sm:inline">{activity.date}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* Quick stats */}
            <div className="space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Actions rapides</CardTitle>
                  <CardDescription>Raccourcis fréquents</CardDescription>
                </CardHeader>
                <CardContent className="space-y-2">
                  <Button variant="outline" className="w-full justify-start gap-2">
                    <Users className="size-4" />
                    Ajouter un membre
                  </Button>
                  <Button variant="outline" className="w-full justify-start gap-2">
                    <CreditCard className="size-4" />
                    Enregistrer un paiement
                  </Button>
                  <Button variant="outline" className="w-full justify-start gap-2">
                    <ArrowUpRight className="size-4" />
                    Nouveau nano-crédit
                  </Button>
                  <Button variant="outline" className="w-full justify-start gap-2">
                    <Repeat className="size-4" />
                    Valider une cotisation
                  </Button>
                  <Button variant="outline" className="w-full justify-start gap-2">
                    <TrendingUp className="size-4" />
                    Voir les rapports
                  </Button>
                </CardContent>
              </Card>

              {/* Demo notifications */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Test des Notifications</CardTitle>
                  <CardDescription>Déclencher des alertes en haut à droite</CardDescription>
                </CardHeader>
                <CardContent className="grid grid-cols-2 gap-2">
                  <Button
                    variant="outline"
                    className="justify-start gap-2 border-emerald-500/20 text-emerald-600 hover:bg-emerald-500/10 dark:text-emerald-400 dark:hover:bg-emerald-500/20"
                    onClick={() => toast({
                      variant: "success",
                      title: "Succès",
                      description: "Le membre a été ajouté avec succès."
                    })}
                  >
                    Succès
                  </Button>
                  <Button
                    variant="outline"
                    className="justify-start gap-2 border-amber-500/20 text-amber-600 hover:bg-amber-500/10 dark:text-amber-400 dark:hover:bg-amber-500/20"
                    onClick={() => toast({
                      variant: "warning",
                      title: "Validation",
                      description: "Attention, veuillez vérifier le document KYC."
                    })}
                  >
                    Validation
                  </Button>
                  <Button
                    variant="outline"
                    className="justify-start gap-2 border-destructive/20 text-destructive hover:bg-destructive/10 dark:hover:bg-destructive/20"
                    onClick={() => toast({
                      variant: "destructive",
                      title: "Erreur",
                      description: "Échec de l'enregistrement du paiement."
                    })}
                  >
                    Erreur
                  </Button>
                  <Button
                    variant="outline"
                    className="justify-start gap-2 border-blue-500/20 text-blue-600 hover:bg-blue-500/10 dark:text-blue-400 dark:hover:bg-blue-500/20"
                    onClick={() => toast({
                      variant: "info",
                      title: "Information",
                      description: "Une mise à jour système est prévue à 22h."
                    })}
                  >
                    Info
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
