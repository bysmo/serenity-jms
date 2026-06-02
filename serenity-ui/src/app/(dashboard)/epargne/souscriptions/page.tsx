'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Card, CardContent } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import type { EpargneSouscription, EpargneEcheance } from '@/types';
import { formatCurrency } from '@/lib/constants';

// Extended type with display fields for sorting
type SouscriptionRow = EpargneSouscription & {
  membreNom: string;
  planNom: string;
} & Record<string, unknown>;

// TODO: Replace with API call
const initialSouscriptions: SouscriptionRow[] = [
  { id: '1', membreId: '1', planId: '1', membreNom: 'Diop Aminata', planNom: 'Épargne Court Terme', montant: 50000, statut: 'ACTIVE', dateSouscription: '2024-01-15', dateFin: '2024-07-15' },
  { id: '2', membreId: '2', planId: '2', membreNom: 'Ndiaye Fatou', planNom: 'Épargne Moyen Terme', montant: 200000, statut: 'ACTIVE', dateSouscription: '2024-02-10', dateFin: '2025-02-10' },
  { id: '3', membreId: '3', planId: '1', membreNom: 'Sow Mamadou', planNom: 'Épargne Court Terme', montant: 30000, statut: 'ANNULEE', dateSouscription: '2024-03-05' },
  { id: '4', membreId: '4', planId: '3', membreNom: 'Ba Ibrahima', planNom: 'Épargne Long Terme', montant: 500000, statut: 'ACTIVE', dateSouscription: '2024-04-20', dateFin: '2026-04-20' },
  { id: '5', membreId: '6', planId: '2', membreNom: 'Fall Ousmane', planNom: 'Épargne Moyen Terme', montant: 150000, statut: 'TERMINEE', dateSouscription: '2023-06-01', dateFin: '2024-06-01' },
  { id: '6', membreId: '8', planId: '1', membreNom: 'Gueye Moussa', planNom: 'Épargne Court Terme', montant: 75000, statut: 'ACTIVE', dateSouscription: '2024-05-12', dateFin: '2024-11-12' },
  { id: '7', membreId: '5', planId: '5', membreNom: 'Diallo Aissatou', planNom: 'Épargne Hebdomadaire', montant: 60000, statut: 'ACTIVE', dateSouscription: '2024-06-01', dateFin: '2025-03-01' },
  { id: '8', membreId: '7', planId: '2', membreNom: 'Sy Mariama', planNom: 'Épargne Moyen Terme', montant: 100000, statut: 'TERMINEE', dateSouscription: '2023-01-15', dateFin: '2024-01-15' },
  { id: '9', membreId: '9', planId: '6', membreNom: 'Sarr Abdoulaye', planNom: 'Épargne Éducation', montant: 120000, statut: 'ACTIVE', dateSouscription: '2024-06-20', dateFin: '2025-04-20' },
  { id: '10', membreId: '10', planId: '7', membreNom: 'Mbaye Khady', planNom: 'Épargne Santé', montant: 80000, statut: 'ACTIVE', dateSouscription: '2024-07-01', dateFin: '2025-07-01' },
  { id: '11', membreId: '11', planId: '8', membreNom: 'Thiam Ousmane', planNom: 'Épargne Mariage', montant: 300000, statut: 'ACTIVE', dateSouscription: '2024-07-15', dateFin: '2026-01-15' },
  { id: '12', membreId: '12', planId: '1', membreNom: 'Kane Fatima', planNom: 'Épargne Court Terme', montant: 40000, statut: 'ANNULEE', dateSouscription: '2024-03-22' },
  { id: '13', membreId: '13', planId: '9', membreNom: 'Cisse Youssouf', planNom: 'Épargne Immobilier', montant: 1000000, statut: 'ACTIVE', dateSouscription: '2024-08-01', dateFin: '2027-08-01' },
  { id: '14', membreId: '14', planId: '2', membreNom: 'Diouf Aminata', planNom: 'Épargne Moyen Terme', montant: 180000, statut: 'TERMINEE', dateSouscription: '2023-03-10', dateFin: '2024-03-10' },
  { id: '15', membreId: '15', planId: '10', membreNom: 'Niang Cheikh', planNom: 'Épargne Urgence', montant: 50000, statut: 'TERMINEE', dateSouscription: '2024-01-05', dateFin: '2024-04-05' },
  { id: '16', membreId: '16', planId: '3', membreNom: 'Samb Rokhaya', planNom: 'Épargne Long Terme', montant: 750000, statut: 'ACTIVE', dateSouscription: '2024-08-20', dateFin: '2026-08-20' },
  { id: '17', membreId: '17', planId: '5', membreNom: 'Seck Boubacar', planNom: 'Épargne Hebdomadaire', montant: 45000, statut: 'ACTIVE', dateSouscription: '2024-09-01', dateFin: '2025-06-01' },
  { id: '18', membreId: '18', planId: '2', membreNom: 'Faye Awa', planNom: 'Épargne Moyen Terme', montant: 250000, statut: 'ACTIVE', dateSouscription: '2024-09-10', dateFin: '2025-09-10' },
  { id: '19', membreId: '19', planId: '6', membreNom: 'Wade Moustapha', planNom: 'Épargne Éducation', montant: 95000, statut: 'ANNULEE', dateSouscription: '2024-04-15' },
  { id: '20', membreId: '20', planId: '1', membreNom: 'Lo Mamadou', planNom: 'Épargne Court Terme', montant: 25000, statut: 'TERMINEE', dateSouscription: '2023-08-20', dateFin: '2024-02-20' },
  { id: '21', membreId: '21', planId: '7', membreNom: 'Balde Mariama', planNom: 'Épargne Santé', montant: 65000, statut: 'ACTIVE', dateSouscription: '2024-10-01', dateFin: '2025-10-01' },
  { id: '22', membreId: '22', planId: '11', membreNom: 'Diao Abdoul', planNom: 'Épargne Retraite', montant: 500000, statut: 'ACTIVE', dateSouscription: '2024-10-15', dateFin: '2029-10-15' },
  { id: '23', membreId: '23', planId: '3', membreNom: 'Toure Kadiatou', planNom: 'Épargne Long Terme', montant: 600000, statut: 'ACTIVE', dateSouscription: '2024-11-01', dateFin: '2026-11-01' },
  { id: '24', membreId: '24', planId: '2', membreNom: 'Camara Ibrahim', planNom: 'Épargne Moyen Terme', montant: 170000, statut: 'TERMINEE', dateSouscription: '2023-05-20', dateFin: '2024-05-20' },
  { id: '25', membreId: '25', planId: '5', membreNom: 'Bah Aissatou', planNom: 'Épargne Hebdomadaire', montant: 35000, statut: 'ANNULEE', dateSouscription: '2024-05-30' },
  { id: '26', membreId: '26', planId: '8', membreNom: 'Dembelé Sekou', planNom: 'Épargne Mariage', montant: 400000, statut: 'ACTIVE', dateSouscription: '2024-11-15', dateFin: '2026-05-15' },
  { id: '27', membreId: '27', planId: '1', membreNom: 'Konaté Fatoumata', planNom: 'Épargne Court Terme', montant: 20000, statut: 'ACTIVE', dateSouscription: '2024-12-01', dateFin: '2024-12-01' },
  { id: '28', membreId: '28', planId: '9', membreNom: 'Traore Modibo', planNom: 'Épargne Immobilier', montant: 2000000, statut: 'ACTIVE', dateSouscription: '2024-12-10', dateFin: '2027-12-10' },
];

// Mock echeances per souscription
const mockEcheances: Record<string, EpargneEcheance[]> = {
  '1': [
    { id: 'e1-1', souscriptionId: '1', numeroEcheance: 1, montant: 8333, dateEcheance: '2024-02-15', statut: 'PAYEE', datePaiement: '2024-02-15', montantPaye: 8333 },
    { id: 'e1-2', souscriptionId: '1', numeroEcheance: 2, montant: 8333, dateEcheance: '2024-03-15', statut: 'PAYEE', datePaiement: '2024-03-16', montantPaye: 8333 },
    { id: 'e1-3', souscriptionId: '1', numeroEcheance: 3, montant: 8334, dateEcheance: '2024-04-15', statut: 'EN_ATTENTE' },
    { id: 'e1-4', souscriptionId: '1', numeroEcheance: 4, montant: 8333, dateEcheance: '2024-05-15', statut: 'EN_ATTENTE' },
    { id: 'e1-5', souscriptionId: '1', numeroEcheance: 5, montant: 8334, dateEcheance: '2024-06-15', statut: 'EN_ATTENTE' },
    { id: 'e1-6', souscriptionId: '1', numeroEcheance: 6, montant: 8333, dateEcheance: '2024-07-15', statut: 'EN_ATTENTE' },
  ],
  '2': [
    { id: 'e2-1', souscriptionId: '2', numeroEcheance: 1, montant: 16667, dateEcheance: '2024-03-10', statut: 'PAYEE', datePaiement: '2024-03-10', montantPaye: 16667 },
    { id: 'e2-2', souscriptionId: '2', numeroEcheance: 2, montant: 16667, dateEcheance: '2024-04-10', statut: 'PAYEE', datePaiement: '2024-04-11', montantPaye: 16667 },
    { id: 'e2-3', souscriptionId: '2', numeroEcheance: 3, montant: 16666, dateEcheance: '2024-05-10', statut: 'EN_RETARD' },
    { id: 'e2-4', souscriptionId: '2', numeroEcheance: 4, montant: 16667, dateEcheance: '2024-06-10', statut: 'EN_ATTENTE' },
  ],
  '3': [
    { id: 'e3-1', souscriptionId: '3', numeroEcheance: 1, montant: 5000, dateEcheance: '2024-04-05', statut: 'ANNULEE' },
  ],
  '4': [
    { id: 'e4-1', souscriptionId: '4', numeroEcheance: 1, montant: 62500, dateEcheance: '2024-07-20', statut: 'PAYEE', datePaiement: '2024-07-20', montantPaye: 62500 },
    { id: 'e4-2', souscriptionId: '4', numeroEcheance: 2, montant: 62500, dateEcheance: '2024-10-20', statut: 'EN_ATTENTE' },
    { id: 'e4-3', souscriptionId: '4', numeroEcheance: 3, montant: 62500, dateEcheance: '2025-01-20', statut: 'EN_ATTENTE' },
  ],
  '5': [
    { id: 'e5-1', souscriptionId: '5', numeroEcheance: 1, montant: 12500, dateEcheance: '2023-07-01', statut: 'PAYEE', datePaiement: '2023-07-01', montantPaye: 12500 },
    { id: 'e5-2', souscriptionId: '5', numeroEcheance: 2, montant: 12500, dateEcheance: '2023-08-01', statut: 'PAYEE', datePaiement: '2023-08-02', montantPaye: 12500 },
    { id: 'e5-3', souscriptionId: '5', numeroEcheance: 3, montant: 12500, dateEcheance: '2023-09-01', statut: 'PAYEE', datePaiement: '2023-09-01', montantPaye: 12500 },
    { id: 'e5-4', souscriptionId: '5', numeroEcheance: 4, montant: 12500, dateEcheance: '2023-10-01', statut: 'PAYEE', datePaiement: '2023-10-01', montantPaye: 12500 },
  ],
  '6': [
    { id: 'e6-1', souscriptionId: '6', numeroEcheance: 1, montant: 12500, dateEcheance: '2024-06-12', statut: 'PAYEE', datePaiement: '2024-06-12', montantPaye: 12500 },
    { id: 'e6-2', souscriptionId: '6', numeroEcheance: 2, montant: 12500, dateEcheance: '2024-07-12', statut: 'EN_ATTENTE' },
    { id: 'e6-3', souscriptionId: '6', numeroEcheance: 3, montant: 12500, dateEcheance: '2024-08-12', statut: 'EN_ATTENTE' },
  ],
  '7': [
    { id: 'e7-1', souscriptionId: '7', numeroEcheance: 1, montant: 1500, dateEcheance: '2024-06-08', statut: 'PAYEE', datePaiement: '2024-06-08', montantPaye: 1500 },
    { id: 'e7-2', souscriptionId: '7', numeroEcheance: 2, montant: 1500, dateEcheance: '2024-06-15', statut: 'PAYEE', datePaiement: '2024-06-15', montantPaye: 1500 },
    { id: 'e7-3', souscriptionId: '7', numeroEcheance: 3, montant: 1500, dateEcheance: '2024-06-22', statut: 'EN_ATTENTE' },
  ],
  '8': [
    { id: 'e8-1', souscriptionId: '8', numeroEcheance: 1, montant: 8334, dateEcheance: '2023-02-15', statut: 'PAYEE', datePaiement: '2023-02-15', montantPaye: 8334 },
    { id: 'e8-2', souscriptionId: '8', numeroEcheance: 2, montant: 8333, dateEcheance: '2023-03-15', statut: 'PAYEE', datePaiement: '2023-03-15', montantPaye: 8333 },
  ],
};

const columns: Column<SouscriptionRow>[] = [
  {
    key: 'membreNom',
    header: 'Membre',
    sortable: true,
    render: (item) => <span className="font-medium">{item.membreNom as string}</span>,
  },
  {
    key: 'planNom',
    header: 'Plan',
    sortable: true,
    render: (item) => <span>{item.planNom as string}</span>,
  },
  {
    key: 'montant',
    header: 'Montant',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatCurrency(item.montant as number)}</span>
    ),
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => <StatusBadge statut={item.statut as string} />,
  },
  {
    key: 'dateSouscription',
    header: 'Date souscription',
    sortable: true,
  },
  {
    key: 'dateFin',
    header: 'Date fin',
    render: (item) => <span>{(item.dateFin as string) || '—'}</span>,
  },
];

const STATUT_LABELS: Record<string, string> = {
  ACTIVE: 'Active',
  TERMINEE: 'Terminée',
  ANNULEE: 'Annulée',
};

export default function SouscriptionsPage() {
  const [souscriptions, setSouscriptions] = useState(initialSouscriptions);
  const [cancelOpen, setCancelOpen] = useState(false);
  const [echeancesOpen, setEcheancesOpen] = useState(false);
  const [selectedSouscription, setSelectedSouscription] = useState<SouscriptionRow | null>(null);

  const handleCancel = () => {
    // TODO: Replace with API call
    if (selectedSouscription) {
      setSouscriptions(
        souscriptions.map((s) =>
          s.id === selectedSouscription.id ? { ...s, statut: 'ANNULEE' as const } : s
        )
      );
      setCancelOpen(false);
      setSelectedSouscription(null);
    }
  };

  const openEcheances = (item: SouscriptionRow) => {
    setSelectedSouscription(item);
    setEcheancesOpen(true);
  };

  const echeances = selectedSouscription
    ? mockEcheances[selectedSouscription.id as string] || []
    : [];

  return (
    <div className="space-y-6">
      <PageHeader
        title="Souscriptions Épargne"
        description="Suivi des souscriptions aux plans d'épargne"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={souscriptions as SouscriptionRow[]}
            columns={columns}
            keyExtractor={(item) => item.id as string}
            searchKeys={['membreNom', 'planNom']}
            searchPlaceholder="Rechercher par membre ou plan..."
            pageSize={10}
            selectable={true}
            exportable={true}
            exportFilename="epargne-souscriptions"
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: Object.entries(STATUT_LABELS).map(([value, label]) => ({
                  label,
                  value,
                })),
              },
            ]}
            actions={(item) => {
              const actions: {
                label: string;
                onClick: () => void;
                variant?: 'default' | 'destructive';
              }[] = [
                { label: 'Voir échéances', onClick: () => openEcheances(item) },
              ];
              if (item.statut === 'ACTIVE') {
                actions.push({
                  label: 'Annuler',
                  onClick: () => {
                    setSelectedSouscription(item);
                    setCancelOpen(true);
                  },
                  variant: 'destructive',
                });
              }
              return actions;
            }}
          />
        </CardContent>
      </Card>

      {/* Echeances Dialog */}
      <Dialog open={echeancesOpen} onOpenChange={setEcheancesOpen}>
        <DialogContent className="max-w-3xl">
          <DialogHeader>
            <DialogTitle>
              Échéances — {selectedSouscription?.membreNom} —{' '}
              {selectedSouscription?.planNom}
            </DialogTitle>
          </DialogHeader>
          <div className="max-h-96 overflow-y-auto">
            {echeances.length > 0 ? (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[60px]">N°</TableHead>
                    <TableHead>Montant</TableHead>
                    <TableHead>Date échéance</TableHead>
                    <TableHead>Statut</TableHead>
                    <TableHead>Montant payé</TableHead>
                    <TableHead>Date paiement</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {echeances.map((ech) => (
                    <TableRow key={ech.id}>
                      <TableCell className="font-medium">{ech.numeroEcheance}</TableCell>
                      <TableCell>{formatCurrency(ech.montant)}</TableCell>
                      <TableCell>{ech.dateEcheance}</TableCell>
                      <TableCell>
                        <StatusBadge statut={ech.statut} />
                      </TableCell>
                      <TableCell>
                        {ech.montantPaye != null ? formatCurrency(ech.montantPaye) : '—'}
                      </TableCell>
                      <TableCell>{ech.datePaiement || '—'}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <p className="py-8 text-center text-muted-foreground">Aucune échéance trouvée</p>
            )}
          </div>
        </DialogContent>
      </Dialog>

      {/* Cancel Confirm Dialog */}
      <ConfirmDialog
        open={cancelOpen}
        onOpenChange={setCancelOpen}
        title="Annuler la souscription"
        description={`Êtes-vous sûr de vouloir annuler la souscription de ${selectedSouscription?.membreNom} au plan ${selectedSouscription?.planNom} ?`}
        confirmLabel="Annuler la souscription"
        variant="destructive"
        onConfirm={handleCancel}
      />
    </div>
  );
}
