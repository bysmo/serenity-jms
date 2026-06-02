'use client';

import { PageHeader } from '@/components/shared/page-header';
import { StatCard } from '@/components/shared/stat-card';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  Users, Wallet, Banknote, CreditCard,
  TrendingUp, Plus, UserPlus
} from 'lucide-react';
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
} from 'recharts';
import { formatCurrency } from '@/lib/constants';
import { StatusBadge } from '@/components/shared/status-badge';

// TODO: Replace with API call
const cotisationsTrend = [
  { mois: 'Jan', montant: 1250000 },
  { mois: 'Fév', montant: 1480000 },
  { mois: 'Mar', montant: 1320000 },
  { mois: 'Avr', montant: 1650000 },
  { mois: 'Mai', montant: 1890000 },
  { mois: 'Jun', montant: 2100000 },
];

// TODO: Replace with API call
const paiementsByMode = [
  { name: 'Wave', value: 4500000 },
  { name: 'Orange Money', value: 3200000 },
  { name: 'Espèces', value: 2800000 },
  { name: 'Free Money', value: 1200000 },
  { name: 'Virement', value: 800000 },
];

// TODO: Replace with API call
const memberGrowth = [
  { mois: 'Jan', membres: 245 },
  { mois: 'Fév', membres: 268 },
  { mois: 'Mar', membres: 290 },
  { mois: 'Avr', membres: 315 },
  { mois: 'Mai', membres: 342 },
  { mois: 'Jun', membres: 378 },
];

// TODO: Replace with API call
const recentActivity = [
  { id: '1', type: 'PAIEMENT', description: 'Cotisation mensuelle - Diop Aminata', montant: 5000, statut: 'CONFIRME', date: '2025-01-15 14:30' },
  { id: '2', type: 'ADHESION', description: 'Nouveau membre - Ndiaye Fatou', montant: 0, statut: 'EN_ATTENTE', date: '2025-01-15 13:15' },
  { id: '3', type: 'EPARGNE', description: 'Versement épargne - Sow Mamadou', montant: 25000, statut: 'CONFIRME', date: '2025-01-15 11:45' },
  { id: '4', type: 'NANO_CREDIT', description: 'Demande nano-crédit - Ba Ibrahima', montant: 50000, statut: 'DEMANDE', date: '2025-01-15 10:20' },
  { id: '5', type: 'REMBOURSEMENT', description: 'Remboursement - Diallo Aissatou', montant: 12000, statut: 'EN_ATTENTE', date: '2025-01-15 09:00' },
];

const PIE_COLORS = ['#10b981', '#f59e0b', '#6366f1', '#ef4444', '#8b5cf6'];

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Tableau de bord"
        description="Vue d'ensemble de la coopérative Serenity-JMS"
        actions={
          <div className="flex gap-2">
            <Button variant="outline" size="sm" className="gap-2">
              <UserPlus className="h-4 w-4" />
              Nouveau membre
            </Button>
            <Button size="sm" className="gap-2">
              <Plus className="h-4 w-4" />
              Action rapide
            </Button>
          </div>
        }
      />

      {/* Stat Cards */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Total Membres"
          value="378"
          description="Membres inscrits"
          icon={<Users className="h-5 w-5" />}
          trend={{ value: 10.5, positive: true }}
        />
        <StatCard
          title="Cotisations Actives"
          value="342"
          description="Engagements actifs"
          icon={<Wallet className="h-5 w-5" />}
          trend={{ value: 5.2, positive: true }}
        />
        <StatCard
          title="Épargne Totale"
          value={formatCurrency(12500000)}
          description="Montant total épargné"
          icon={<Banknote className="h-5 w-5" />}
          trend={{ value: 12.3, positive: true }}
        />
        <StatCard
          title="Nano-Crédits Actifs"
          value="45"
          description="Crédits en cours"
          icon={<CreditCard className="h-5 w-5" />}
          trend={{ value: 2.1, positive: false }}
        />
      </div>

      {/* Charts */}
      <div className="grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Tendance des Cotisations</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={cotisationsTrend}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis dataKey="mois" className="text-xs" />
                <YAxis className="text-xs" tickFormatter={(v: number) => `${(v / 1000000).toFixed(1)}M`} />
                <Tooltip formatter={(value: number) => formatCurrency(value)} />
                <Line type="monotone" dataKey="montant" stroke="#10b981" strokeWidth={2} dot={{ fill: '#10b981' }} name="Montant" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Paiements par Mode</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={paiementsByMode}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }: { name: string; percent: number }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {paiementsByMode.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value: number) => formatCurrency(value)} />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Croissance des Membres</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={memberGrowth}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis dataKey="mois" className="text-xs" />
                <YAxis className="text-xs" />
                <Tooltip />
                <Bar dataKey="membres" fill="#10b981" radius={[4, 4, 0, 0]} name="Membres" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Actions Rapides</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-2 gap-3">
            <Button variant="outline" className="h-auto flex-col gap-2 py-4">
              <UserPlus className="h-5 w-5" />
              <span className="text-xs">Nouveau Membre</span>
            </Button>
            <Button variant="outline" className="h-auto flex-col gap-2 py-4">
              <Wallet className="h-5 w-5" />
              <span className="text-xs">Cotisation</span>
            </Button>
            <Button variant="outline" className="h-auto flex-col gap-2 py-4">
              <Banknote className="h-5 w-5" />
              <span className="text-xs">Épargne</span>
            </Button>
            <Button variant="outline" className="h-auto flex-col gap-2 py-4">
              <CreditCard className="h-5 w-5" />
              <span className="text-xs">Nano-Crédit</span>
            </Button>
            <Button variant="outline" className="h-auto flex-col gap-2 py-4">
              <TrendingUp className="h-5 w-5" />
              <span className="text-xs">Rapport</span>
            </Button>
            <Button variant="outline" className="h-auto flex-col gap-2 py-4">
              <Plus className="h-5 w-5" />
              <span className="text-xs">Autre</span>
            </Button>
          </CardContent>
        </Card>
      </div>

      {/* Recent Activity */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Activité Récente</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {recentActivity.map((activity) => (
              <div key={activity.id} className="flex items-center justify-between rounded-lg border p-3 hover:bg-accent/50 transition-colors">
                <div className="flex items-center gap-3">
                  <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/10">
                    {activity.type === 'PAIEMENT' && <Wallet className="h-4 w-4 text-primary" />}
                    {activity.type === 'ADHESION' && <UserPlus className="h-4 w-4 text-primary" />}
                    {activity.type === 'EPARGNE' && <Banknote className="h-4 w-4 text-primary" />}
                    {activity.type === 'NANO_CREDIT' && <CreditCard className="h-4 w-4 text-primary" />}
                    {activity.type === 'REMBOURSEMENT' && <TrendingUp className="h-4 w-4 text-primary" />}
                  </div>
                  <div>
                    <p className="text-sm font-medium">{activity.description}</p>
                    <p className="text-xs text-muted-foreground">{activity.date}</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  {activity.montant > 0 && (
                    <span className="text-sm font-medium">{formatCurrency(activity.montant)}</span>
                  )}
                  <StatusBadge statut={activity.statut} />
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
