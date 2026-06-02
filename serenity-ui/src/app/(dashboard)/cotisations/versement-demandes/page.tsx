'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Card, CardContent } from '@/components/ui/card';
import type { VersementDemande, VersementDemandeStatut } from '@/types';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const formatMoney = (amount: number): string =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

const VERSEMENT_STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  TRAITEE: 'Traitée',
  REJETEE: 'Rejetée',
};

// ---------------------------------------------------------------------------
// Mock Data — with membreNom & cotisationLibelle for sorting
// ---------------------------------------------------------------------------

type VersementRow = VersementDemande & { membreNom: string; cotisationLibelle: string };

const initialDemandes: VersementRow[] = [
  { id: '1', membreId: '1', membreNom: 'Diop Aminata', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'TRAITEE', traitePar: 'admin1', dateTraitement: '2024-06-02', createdAt: '2024-06-01' },
  { id: '2', membreId: '2', membreNom: 'Ndiaye Fatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'EN_ATTENTE', createdAt: '2024-06-10' },
  { id: '3', membreId: '3', membreNom: 'Sow Mamadou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantDemande: 10000, statut: 'EN_ATTENTE', createdAt: '2024-06-12' },
  { id: '4', membreId: '4', membreNom: 'Ba Ibrahima', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'REJETEE', traitePar: 'admin2', dateTraitement: '2024-05-16', motifRejet: 'Montant insuffisant sur le compte cotisation', createdAt: '2024-05-15' },
  { id: '5', membreId: '6', membreNom: 'Fall Ousmane', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantDemande: 3000, statut: 'TRAITEE', traitePar: 'admin1', dateTraitement: '2024-05-22', createdAt: '2024-05-20' },
  { id: '6', membreId: '5', membreNom: 'Diallo Aissatou', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantDemande: 15000, statut: 'EN_ATTENTE', createdAt: '2024-06-22' },
  { id: '7', membreId: '7', membreNom: 'Sy Mariama', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'TRAITEE', traitePar: 'admin1', dateTraitement: '2024-07-01', createdAt: '2024-06-28' },
  { id: '8', membreId: '8', membreNom: 'Gueye Moussa', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantDemande: 25000, statut: 'EN_ATTENTE', createdAt: '2024-07-03' },
  { id: '9', membreId: '9', membreNom: 'Kane Fatou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantDemande: 8000, statut: 'REJETEE', traitePar: 'admin2', dateTraitement: '2024-07-08', motifRejet: 'Demande hors délai — période de versement clôturée', createdAt: '2024-07-05' },
  { id: '10', membreId: '10', membreNom: 'Mbaye Cheikh', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantDemande: 20000, statut: 'TRAITEE', traitePar: 'admin3', dateTraitement: '2024-07-12', createdAt: '2024-07-10' },
  { id: '11', membreId: '11', membreNom: 'Thiam Boubacar', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'EN_ATTENTE', createdAt: '2024-07-15' },
  { id: '12', membreId: '12', membreNom: 'Cissé Adama', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantDemande: 4000, statut: 'TRAITEE', traitePar: 'admin1', dateTraitement: '2024-07-20', createdAt: '2024-07-18' },
  { id: '13', membreId: '13', membreNom: 'Sarr Awa', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantDemande: 50000, statut: 'EN_ATTENTE', createdAt: '2024-07-22' },
  { id: '14', membreId: '14', membreNom: 'Niang Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 10000, statut: 'REJETEE', traitePar: 'admin1', dateTraitement: '2024-07-28', motifRejet: 'Montant demandé supérieur au solde disponible', createdAt: '2024-07-25' },
  { id: '15', membreId: '15', membreNom: 'Diop Ibrahima', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantDemande: 30000, statut: 'TRAITEE', traitePar: 'admin2', dateTraitement: '2024-08-01', createdAt: '2024-07-30' },
  { id: '16', membreId: '16', membreNom: 'Sow Khady', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantDemande: 6000, statut: 'EN_ATTENTE', createdAt: '2024-08-03' },
  { id: '17', membreId: '17', membreNom: 'Bâ Mamadou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'TRAITEE', traitePar: 'admin3', dateTraitement: '2024-08-06', createdAt: '2024-08-04' },
  { id: '18', membreId: '18', membreNom: 'Faye Sokhna', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantDemande: 40000, statut: 'EN_ATTENTE', createdAt: '2024-08-08' },
  { id: '19', membreId: '19', membreNom: 'Mendy Ismaïla', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantDemande: 12000, statut: 'REJETEE', traitePar: 'admin2', dateTraitement: '2024-08-12', motifRejet: 'Compte suspendu — régularisation en cours', createdAt: '2024-08-10' },
  { id: '20', membreId: '20', membreNom: 'Dieng Fatima', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantDemande: 5000, statut: 'EN_ATTENTE', createdAt: '2024-08-14' },
  { id: '21', membreId: '21', membreNom: 'Toure Abdoulaye', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantDemande: 7500, statut: 'TRAITEE', traitePar: 'admin1', dateTraitement: '2024-08-18', createdAt: '2024-08-16' },
];

// ---------------------------------------------------------------------------
// Columns
// ---------------------------------------------------------------------------

const columns: Column<VersementRow & Record<string, unknown>>[] = [
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
    key: 'montantDemande',
    header: 'Montant Demandé',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatMoney(item.montantDemande as number)}</span>
    ),
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => (
      <StatusBadge
        statut={String(item.statut)}
        label={VERSEMENT_STATUT_LABELS[String(item.statut)]}
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

export default function VersementDemandesPage() {
  const [demandes, setDemandes] = useState(initialDemandes);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedDemande, setSelectedDemande] = useState<(VersementRow & Record<string, unknown>) | null>(null);
  const [actionType, setActionType] = useState<'process' | 'reject'>('process');

  const handleProcess = () => {
    if (selectedDemande) {
      setDemandes((prev) =>
        prev.map((d) =>
          d.id === selectedDemande.id
            ? {
                ...d,
                statut: (actionType === 'process' ? 'TRAITEE' : 'REJETEE') as VersementDemandeStatut,
                traitePar: 'admin1',
                dateTraitement: new Date().toISOString().slice(0, 10),
              }
            : d,
        ),
      );
      setConfirmOpen(false);
      setSelectedDemande(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Demandes de Versement"
        description="Gestion des demandes de versement de cotisations"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={demandes as (VersementRow & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['membreNom', 'cotisationLibelle']}
            searchPlaceholder="Rechercher par membre ou cotisation..."
            pageSize={10}
            exportable
            exportFilename="versement-demandes"
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'EN_ATTENTE' },
                  { label: 'Traitée', value: 'TRAITEE' },
                  { label: 'Rejetée', value: 'REJETEE' },
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
                  label: 'Traiter',
                  onClick: () => {
                    setSelectedDemande(item);
                    setActionType('process');
                    setConfirmOpen(true);
                  },
                });
                actions.push({
                  label: 'Rejeter',
                  onClick: () => {
                    setSelectedDemande(item);
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
        title={actionType === 'process' ? 'Traiter la demande' : 'Rejeter la demande'}
        description={
          actionType === 'process'
            ? `Êtes-vous sûr de vouloir traiter la demande de versement de ${selectedDemande ? formatMoney(selectedDemande.montantDemande as number) : ''} ?`
            : `Êtes-vous sûr de vouloir rejeter la demande de versement de ${selectedDemande ? formatMoney(selectedDemande.montantDemande as number) : ''} ?`
        }
        confirmLabel={actionType === 'process' ? 'Traiter' : 'Rejeter'}
        variant={actionType === 'reject' ? 'destructive' : 'default'}
        onConfirm={handleProcess}
      />
    </div>
  );
}
