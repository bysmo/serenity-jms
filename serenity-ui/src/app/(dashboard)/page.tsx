'use client';

import { PageHeader } from '@/components/shared/page-header';
import { StatCard } from '@/components/shared/stat-card';
import { StatusBadge } from '@/components/shared/status-badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Users,
  Wallet,
  Banknote,
  CreditCard,
  TrendingUp,
  UserPlus,
  PiggyBank,
  HandCoins,
} from 'lucide-react';
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale/fr';

// ── Currency helper ──────────────────────────────────────────────────────
const fmtCurrency = (amount: number) =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

// ── Mock data: Cotisations mensuelles (6 derniers mois) ──────────────────
const cotisationsMensuelles = [
  { mois: 'Janv.', montant: 3_200_000 },
  { mois: 'Fév.', montant: 3_850_000 },
  { mois: 'Mars', montant: 3_500_000 },
  { mois: 'Avr.', montant: 4_120_000 },
  { mois: 'Mai', montant: 4_680_000 },
  { mois: 'Juin', montant: 5_100_000 },
];

// ── Mock data: Répartition des paiements par mode ────────────────────────
const paiementsParMode = [
  { name: 'Wave', value: 4_500_000 },
  { name: 'Orange Money', value: 3_200_000 },
  { name: 'Espèces', value: 2_800_000 },
  { name: 'Free Money', value: 1_200_000 },
  { name: 'Virement', value: 800_000 },
];

const PIE_COLORS = ['#10b981', '#f59e0b', '#6366f1', '#ef4444', '#8b5cf6'];

// ── Mock data: Croissance des membres ────────────────────────────────────
const croissanceMembres = [
  { mois: 'Janv.', membres: 4_120 },
  { mois: 'Fév.', membres: 4_350 },
  { mois: 'Mars', membres: 4_580 },
  { mois: 'Avr.', membres: 4_800 },
  { mois: 'Mai', membres: 5_010 },
  { mois: 'Juin', membres: 5_234 },
];

// ── Mock data: Activité récente ──────────────────────────────────────────
const activiteRecente = [
  {
    id: '1',
    type: 'PAIEMENT',
    description: 'Cotisation mensuelle - Diop Aminata',
    montant: 5_000,
    statut: 'CONFIRME',
    date: '2025-06-01T14:30:00',
  },
  {
    id: '2',
    type: 'ADHESION',
    description: 'Nouvelle adhésion - Ndiaye Fatou',
    montant: 10_000,
    statut: 'EN_ATTENTE',
    date: '2025-06-01T13:15:00',
  },
  {
    id: '3',
    type: 'EPARGNE',
    description: 'Versement épargne - Sow Mamadou',
    montant: 25_000,
    statut: 'CONFIRME',
    date: '2025-06-01T11:45:00',
  },
  {
    id: '4',
    type: 'NANO_CREDIT',
    description: 'Demande nano-crédit - Ba Ibrahima',
    montant: 50_000,
    statut: 'DEMANDE_EN_ATTENTE',
    date: '2025-06-01T10:20:00',
  },
  {
    id: '5',
    type: 'REMBOURSEMENT',
    description: 'Remboursement partiel - Diallo Aissatou',
    montant: 12_000,
    statut: 'CONFIRME',
    date: '2025-06-01T09:00:00',
  },
];

const TYPE_ICONS: Record<string, React.ElementType> = {
  PAIEMENT: Wallet,
  ADHESION: UserPlus,
  EPARGNE: PiggyBank,
  NANO_CREDIT: CreditCard,
  REMBOURSEMENT: TrendingUp,
};

// ── Component ────────────────────────────────────────────────────────────
export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Tableau de bord"
        description="Vue d'ensemble de la coopérative Serenity-JMS"
      />

      {/* ── Stat Cards ────────────────────────────────────────────────── */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Membres Actifs"
          value="5 234"
          description="Membres inscrits actifs"
          icon={<Users className="h-5 w-5" />}
          trend={{ value: 12.5, positive: true }}
        />
        <StatCard
          title="Cotisations Actives"
          value="128"
          description="Engagements en cours"
          icon={<Wallet className="h-5 w-5" />}
          trend={{ value: 8.3, positive: true }}
        />
        <StatCard
          title="Épargne Totale"
          value="45,2M FCFA"
          description="Montant total épargné"
          icon={<Banknote className="h-5 w-5" />}
          trend={{ value: 15.2, positive: true }}
        />
        <StatCard
          title="Nano-Crédits"
          value="342"
          description="Crédits en cours"
          icon={<CreditCard className="h-5 w-5" />}
          trend={{ value: 3.1, positive: false }}
        />
      </div>

      {/* ── Charts (2×2 grid) ─────────────────────────────────────────── */}
      <div className="grid gap-4 lg:grid-cols-2">
        {/* Line chart – Cotisations mensuelles */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">
              Cotisations Mensuelles
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={cotisationsMensuelles}>
                <CartesianGrid
                  strokeDasharray="3 3"
                  className="stroke-muted"
                />
                <XAxis dataKey="mois" className="text-xs" />
                <YAxis
                  className="text-xs"
                  tickFormatter={(v: number) =>
                    `${(v / 1_000_000).toFixed(1)}M`
                  }
                />
                <Tooltip
                  formatter={(value: number) => fmtCurrency(value)}
                />
                <Line
                  type="monotone"
                  dataKey="montant"
                  stroke="#10b981"
                  strokeWidth={2}
                  dot={{ fill: '#10b981', r: 4 }}
                  activeDot={{ r: 6 }}
                  name="Montant"
                />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Pie chart – Répartition par mode de paiement */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">
              Répartition des Paiements par Mode
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={paiementsParMode}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({
                    name,
                    percent,
                  }: {
                    name: string;
                    percent: number;
                  }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {paiementsParMode.map((_, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={PIE_COLORS[index % PIE_COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip formatter={(value: number) => fmtCurrency(value)} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Bar chart – Croissance des membres */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">
              Croissance des Membres
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={croissanceMembres}>
                <CartesianGrid
                  strokeDasharray="3 3"
                  className="stroke-muted"
                />
                <XAxis dataKey="mois" className="text-xs" />
                <YAxis className="text-xs" />
                <Tooltip />
                <Bar
                  dataKey="membres"
                  fill="#10b981"
                  radius={[4, 4, 0, 0]}
                  name="Membres"
                />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* Quick actions */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Actions Rapides</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-2 gap-4">
            <Button
              variant="outline"
              className="h-auto flex-col gap-3 py-6 hover:bg-primary/5 hover:border-primary/30 transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300">
                <UserPlus className="h-5 w-5" />
              </div>
              <span className="text-sm font-medium">Nouveau Membre</span>
            </Button>
            <Button
              variant="outline"
              className="h-auto flex-col gap-3 py-6 hover:bg-primary/5 hover:border-primary/30 transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-amber-100 text-amber-700 dark:bg-amber-900 dark:text-amber-300">
                <Wallet className="h-5 w-5" />
              </div>
              <span className="text-sm font-medium">Créer Cotisation</span>
            </Button>
            <Button
              variant="outline"
              className="h-auto flex-col gap-3 py-6 hover:bg-primary/5 hover:border-primary/30 transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-violet-100 text-violet-700 dark:bg-violet-900 dark:text-violet-300">
                <PiggyBank className="h-5 w-5" />
              </div>
              <span className="text-sm font-medium">Souscrire Épargne</span>
            </Button>
            <Button
              variant="outline"
              className="h-auto flex-col gap-3 py-6 hover:bg-primary/5 hover:border-primary/30 transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-sky-100 text-sky-700 dark:bg-sky-900 dark:text-sky-300">
                <HandCoins className="h-5 w-5" />
              </div>
              <span className="text-sm font-medium">
                Demande Nano-Crédit
              </span>
            </Button>
          </CardContent>
        </Card>
      </div>

      {/* ── Recent Activity Table ─────────────────────────────────────── */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Activité Récente</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[40px]" />
                  <TableHead>Description</TableHead>
                  <TableHead className="text-right">Montant</TableHead>
                  <TableHead>Statut</TableHead>
                  <TableHead className="text-right">Date</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {activiteRecente.map((activity) => {
                  const Icon = TYPE_ICONS[activity.type] ?? TrendingUp;
                  return (
                    <TableRow key={activity.id}>
                      <TableCell>
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/10">
                          <Icon className="h-4 w-4 text-primary" />
                        </div>
                      </TableCell>
                      <TableCell className="font-medium">
                        {activity.description}
                      </TableCell>
                      <TableCell className="text-right font-medium">
                        {activity.montant > 0
                          ? fmtCurrency(activity.montant)
                          : '—'}
                      </TableCell>
                      <TableCell>
                        <StatusBadge statut={activity.statut} />
                      </TableCell>
                      <TableCell className="text-right text-muted-foreground">
                        {format(new Date(activity.date), 'dd MMM yyyy HH:mm', { locale: fr })}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
