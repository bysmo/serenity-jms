'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Card, CardContent } from '@/components/ui/card';
import type { Remboursement, RemboursementStatut } from '@/types';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const formatMoney = (amount: number): string =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

const REMBOURSEMENT_STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  APPROUVE: 'Approuvé',
  REJETE: 'Rejeté',
  PAYE: 'Payé',
};

// ---------------------------------------------------------------------------
// Mock Data — with membreNom & cotisationLibelle for sorting
// ---------------------------------------------------------------------------

type RemboursementRow = Remboursement & { membreNom: string; cotisationLibelle: string };

const initialRemboursements: RemboursementRow[] = [
  { id: '1', membreId: '1', membreNom: 'Diop Aminata', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, motif: 'Erreur de paiement — double prélèvement', statut: 'EN_ATTENTE', createdAt: '2024-06-01' },
  { id: '2', membreId: '2', membreNom: 'Ndiaye Fatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 10000, motif: 'Double paiement détecté', statut: 'APPROUVE', traitePar: 'admin1', dateTraitement: '2024-05-22', createdAt: '2024-05-20' },
  { id: '3', membreId: '3', membreNom: 'Sow Mamadou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 2000, motif: 'Retrait volontaire de la cotisation', statut: 'REJETE', traitePar: 'admin2', dateTraitement: '2024-05-16', commentaire: 'Le retrait volontaire ne donne pas lieu à remboursement selon les statuts.', createdAt: '2024-05-15' },
  { id: '4', membreId: '4', membreNom: 'Ba Ibrahima', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, motif: 'Cotisation indue — membre déjà à jour', statut: 'PAYE', traitePar: 'admin1', dateTraitement: '2024-04-12', createdAt: '2024-04-10' },
  { id: '5', membreId: '6', membreNom: 'Fall Ousmane', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 3000, motif: 'Montant incorrect prélevé', statut: 'EN_ATTENTE', createdAt: '2024-06-05' },
  { id: '6', membreId: '5', membreNom: 'Diallo Aissatou', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 15000, motif: 'Annulation de cotisation après confirmation', statut: 'APPROUVE', traitePar: 'admin1', dateTraitement: '2024-06-18', createdAt: '2024-06-15' },
  { id: '7', membreId: '7', membreNom: 'Sy Mariama', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 20000, motif: 'Résiliation du plan d\'épargne — remboursement partiel', statut: 'EN_ATTENTE', createdAt: '2024-06-20' },
  { id: '8', membreId: '8', membreNom: 'Gueye Moussa', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, motif: 'Prélèvement sur compte inactif', statut: 'REJETE', traitePar: 'admin2', dateTraitement: '2024-07-02', commentaire: 'Le compte était actif au moment du prélèvement.', createdAt: '2024-06-28' },
  { id: '9', membreId: '9', membreNom: 'Kane Fatou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 4000, motif: 'Erreur de montant — prélèvement excessif', statut: 'PAYE', traitePar: 'admin1', dateTraitement: '2024-07-08', createdAt: '2024-07-05' },
  { id: '10', membreId: '10', membreNom: 'Mbaye Cheikh', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 50000, motif: 'Non-conformité — cotisation non approuvée par l\'AG', statut: 'APPROUVE', traitePar: 'admin3', dateTraitement: '2024-07-15', createdAt: '2024-07-12' },
  { id: '11', membreId: '11', membreNom: 'Thiam Boubacar', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, motif: 'Triple prélèvement détecté sur le même mois', statut: 'EN_ATTENTE', createdAt: '2024-07-18' },
  { id: '12', membreId: '12', membreNom: 'Cissé Adama', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 8000, motif: 'Retrait anticipé — cas de force majeure', statut: 'EN_ATTENTE', createdAt: '2024-07-22' },
  { id: '13', membreId: '13', membreNom: 'Sarr Awa', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 2500, motif: 'Cotisation prélevée après résignation', statut: 'PAYE', traitePar: 'admin2', dateTraitement: '2024-08-02', createdAt: '2024-07-30' },
  { id: '14', membreId: '14', membreNom: 'Niang Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 10000, motif: 'Paiement en doublon — erreur système', statut: 'APPROUVE', traitePar: 'admin1', dateTraitement: '2024-08-05', createdAt: '2024-08-01' },
  { id: '15', membreId: '15', membreNom: 'Diop Ibrahima', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 25000, motif: 'Désistement — projet non réalisé', statut: 'REJETE', traitePar: 'admin3', dateTraitement: '2024-08-10', commentaire: 'Les cotisations extraordinaires ne sont pas remboursables sauf décision du conseil.', createdAt: '2024-08-08' },
  { id: '16', membreId: '16', membreNom: 'Sow Khady', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 6000, motif: 'Départ volontaire de la coopérative', statut: 'EN_ATTENTE', createdAt: '2024-08-12' },
  { id: '17', membreId: '17', membreNom: 'Bâ Mamadou', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 35000, motif: 'Clôture de compte — remboursement intégral', statut: 'APPROUVE', traitePar: 'admin1', dateTraitement: '2024-08-18', createdAt: '2024-08-15' },
];

// ---------------------------------------------------------------------------
// Columns
// ---------------------------------------------------------------------------

const columns: Column<RemboursementRow & Record<string, unknown>>[] = [
  {
    key: 'membreNom',
    header: 'Membre',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{String(item.membreNom)}</span>
    ),
  },
  {
    key: 'cotisationLibelle',
    header: 'Cotisation',
    render: (item) => (
      <span>{String(item.cotisationLibelle)}</span>
    ),
  },
  {
    key: 'montant',
    header: 'Montant',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatMoney(item.montant as number)}</span>
    ),
  },
  {
    key: 'motif',
    header: 'Motif',
    render: (item) => (
      <span className="text-sm max-w-[200px] truncate block">
        {String(item.motif ?? '—')}
      </span>
    ),
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => (
      <StatusBadge
        statut={String(item.statut)}
        label={REMBOURSEMENT_STATUT_LABELS[String(item.statut)]}
      />
    ),
  },
  {
    key: 'createdAt',
    header: 'Date',
    sortable: true,
    render: (item) => <span className="text-sm">{String(item.createdAt ?? '')}</span>,
  },
];

// ---------------------------------------------------------------------------
// Page Component
// ---------------------------------------------------------------------------

export default function RemboursementsPage() {
  const [remboursements, setRemboursements] = useState(initialRemboursements);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedRemboursement, setSelectedRemboursement] = useState<(RemboursementRow & Record<string, unknown>) | null>(null);
  const [actionType, setActionType] = useState<'approve' | 'reject'>('approve');

  const handleAction = () => {
    if (selectedRemboursement) {
      setRemboursements((prev) =>
        prev.map((r) =>
          r.id === selectedRemboursement.id
            ? {
                ...r,
                statut: (actionType === 'approve' ? 'APPROUVE' : 'REJETE') as RemboursementStatut,
                traitePar: 'admin1',
                dateTraitement: new Date().toISOString().slice(0, 10),
              }
            : r,
        ),
      );
      setConfirmOpen(false);
      setSelectedRemboursement(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Remboursements"
        description="Gestion des demandes de remboursement"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={remboursements as (RemboursementRow & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['membreNom', 'motif']}
            searchPlaceholder="Rechercher..."
            pageSize={10}
            exportable
            exportFilename="remboursements"
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'EN_ATTENTE' },
                  { label: 'Approuvé', value: 'APPROUVE' },
                  { label: 'Rejeté', value: 'REJETE' },
                  { label: 'Payé', value: 'PAYE' },
                ],
              },
            ]}
            actions={(item) => {
              const actions: {
                label: string;
                onClick: () => void;
                variant?: 'default' | 'destructive';
              }[] = [];
              if (item.statut === 'EN_ATTENTE') {
                actions.push({
                  label: 'Approuver',
                  onClick: () => {
                    setSelectedRemboursement(item);
                    setActionType('approve');
                    setConfirmOpen(true);
                  },
                });
                actions.push({
                  label: 'Rejeter',
                  onClick: () => {
                    setSelectedRemboursement(item);
                    setActionType('reject');
                    setConfirmOpen(true);
                  },
                  variant: 'destructive',
                });
              }
              return actions;
            }}
          />
        </CardContent>
      </Card>

      <ConfirmDialog
        open={confirmOpen}
        onOpenChange={setConfirmOpen}
        title={
          actionType === 'approve'
            ? 'Approuver le remboursement'
            : 'Rejeter le remboursement'
        }
        description={
          actionType === 'approve'
            ? `Êtes-vous sûr de vouloir approuver le remboursement de ${selectedRemboursement ? formatMoney(selectedRemboursement.montant as number) : ''} ?`
            : `Êtes-vous sûr de vouloir rejeter le remboursement de ${selectedRemboursement ? formatMoney(selectedRemboursement.montant as number) : ''} ?`
        }
        confirmLabel={actionType === 'approve' ? 'Approuver' : 'Rejeter'}
        variant={actionType === 'reject' ? 'destructive' : 'default'}
        onConfirm={handleAction}
      />
    </div>
  );
}
