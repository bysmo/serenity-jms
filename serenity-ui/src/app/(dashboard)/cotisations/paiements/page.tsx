'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Card, CardContent } from '@/components/ui/card';
import type { Paiement } from '@/types';
import { MODE_PAIEMENT_LABELS } from '@/lib/constants';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const formatMoney = (amount: number): string =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

const PAIEMENT_STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  CONFIRME: 'Confirmé',
  ECHEC: 'Échoué',
  ANNULE: 'Annulé',
};

// ---------------------------------------------------------------------------
// Mock Data — with membreNom & cotisationLibelle for sorting
// ---------------------------------------------------------------------------

type PaiementRow = Paiement & { membreNom: string; cotisationLibelle: string };

const initialPaiements: PaiementRow[] = [
  { id: '1', membreId: '1', membreNom: 'Diop Aminata', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'WAVE', statut: 'CONFIRME', datePaiement: '2024-06-15', reference: 'WAV-20240615-001', createdAt: '2024-06-15' },
  { id: '2', membreId: '2', membreNom: 'Ndiaye Fatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ORANGE_MONEY', statut: 'CONFIRME', datePaiement: '2024-06-14', reference: 'OM-20240614-002', createdAt: '2024-06-14' },
  { id: '3', membreId: '3', membreNom: 'Sow Mamadou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 2000, modePaiement: 'ESPECES', statut: 'EN_ATTENTE', datePaiement: '2024-06-13', createdAt: '2024-06-13' },
  { id: '4', membreId: '4', membreNom: 'Ba Ibrahima', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'WAVE', statut: 'ECHEC', datePaiement: '2024-06-12', reference: 'WAV-20240612-004', createdAt: '2024-06-12' },
  { id: '5', membreId: '5', membreNom: 'Diallo Aissatou', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 50000, modePaiement: 'VIREMENT', statut: 'CONFIRME', datePaiement: '2024-06-10', reference: 'VIR-20240610-005', createdAt: '2024-06-10' },
  { id: '6', membreId: '6', membreNom: 'Fall Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'FREE_MONEY', statut: 'ANNULE', datePaiement: '2024-06-08', createdAt: '2024-06-08' },
  { id: '7', membreId: '7', membreNom: 'Sy Mariama', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 3000, modePaiement: 'ORANGE_MONEY', statut: 'EN_ATTENTE', datePaiement: '2024-06-20', reference: 'OM-20240620-007', createdAt: '2024-06-20' },
  { id: '8', membreId: '8', membreNom: 'Gueye Moussa', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 10000, modePaiement: 'WAVE', statut: 'CONFIRME', datePaiement: '2024-06-18', reference: 'WAV-20240618-008', createdAt: '2024-06-18' },
  { id: '9', membreId: '9', membreNom: 'Kane Fatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'CHEQUE', statut: 'EN_ATTENTE', datePaiement: '2024-06-22', reference: 'CHQ-20240622-009', createdAt: '2024-06-22' },
  { id: '10', membreId: '10', membreNom: 'Mbaye Cheikh', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 50000, modePaiement: 'VIREMENT', statut: 'ECHEC', datePaiement: '2024-06-25', reference: 'VIR-20240625-010', createdAt: '2024-06-25' },
  { id: '11', membreId: '11', membreNom: 'Thiam Boubacar', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 5000, modePaiement: 'WAVE', statut: 'CONFIRME', datePaiement: '2024-07-01', reference: 'WAV-20240701-011', createdAt: '2024-07-01' },
  { id: '12', membreId: '12', membreNom: 'Cissé Adama', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ORANGE_MONEY', statut: 'CONFIRME', datePaiement: '2024-07-03', reference: 'OM-20240703-012', createdAt: '2024-07-03' },
  { id: '13', membreId: '13', membreNom: 'Sarr Awa', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 15000, modePaiement: 'VIREMENT', statut: 'CONFIRME', datePaiement: '2024-07-05', reference: 'VIR-20240705-013', createdAt: '2024-07-05' },
  { id: '14', membreId: '14', membreNom: 'Niang Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ESPECES', statut: 'EN_ATTENTE', datePaiement: '2024-07-08', createdAt: '2024-07-08' },
  { id: '15', membreId: '15', membreNom: 'Diop Ibrahima', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 50000, modePaiement: 'CHEQUE', statut: 'CONFIRME', datePaiement: '2024-07-10', reference: 'CHQ-20240710-015', createdAt: '2024-07-10' },
  { id: '16', membreId: '16', membreNom: 'Sow Khady', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 2000, modePaiement: 'FREE_MONEY', statut: 'ECHEC', datePaiement: '2024-07-12', createdAt: '2024-07-12' },
  { id: '17', membreId: '17', membreNom: 'Bâ Mamadou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'WAVE', statut: 'CONFIRME', datePaiement: '2024-07-15', reference: 'WAV-20240715-017', createdAt: '2024-07-15' },
  { id: '18', membreId: '18', membreNom: 'Faye Sokhna', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 20000, modePaiement: 'VIREMENT', statut: 'ANNULE', datePaiement: '2024-07-18', reference: 'VIR-20240718-018', createdAt: '2024-07-18' },
  { id: '19', membreId: '19', membreNom: 'Mendy Ismaïla', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ORANGE_MONEY', statut: 'CONFIRME', datePaiement: '2024-07-20', reference: 'OM-20240720-019', createdAt: '2024-07-20' },
  { id: '20', membreId: '20', membreNom: 'Dieng Fatima', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 3000, modePaiement: 'WAVE', statut: 'EN_ATTENTE', datePaiement: '2024-07-22', reference: 'WAV-20240722-020', createdAt: '2024-07-22' },
  { id: '21', membreId: '21', membreNom: 'Toure Abdoulaye', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 75000, modePaiement: 'VIREMENT', statut: 'CONFIRME', datePaiement: '2024-07-25', reference: 'VIR-20240725-021', createdAt: '2024-07-25' },
  { id: '22', membreId: '22', membreNom: 'Camara Mariatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ESPECES', statut: 'CONFIRME', datePaiement: '2024-07-28', createdAt: '2024-07-28' },
  { id: '23', membreId: '23', membreNom: 'Diallo Mamadou', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 10000, modePaiement: 'WAVE', statut: 'ECHEC', datePaiement: '2024-08-01', reference: 'WAV-20240801-023', createdAt: '2024-08-01' },
  { id: '24', membreId: '24', membreNom: 'Wane Aminata', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 4000, modePaiement: 'ORANGE_MONEY', statut: 'CONFIRME', datePaiement: '2024-08-03', reference: 'OM-20240803-024', createdAt: '2024-08-03' },
  { id: '25', membreId: '25', membreNom: 'Seck Birame', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'CHEQUE', statut: 'EN_ATTENTE', datePaiement: '2024-08-05', reference: 'CHQ-20240805-025', createdAt: '2024-08-05' },
  { id: '26', membreId: '1', membreNom: 'Diop Aminata', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 2500, modePaiement: 'FREE_MONEY', statut: 'CONFIRME', datePaiement: '2024-08-08', reference: 'FM-20240808-026', createdAt: '2024-08-08' },
  { id: '27', membreId: '3', membreNom: 'Sow Mamadou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'WAVE', statut: 'CONFIRME', datePaiement: '2024-08-10', reference: 'WAV-20240810-027', createdAt: '2024-08-10' },
  { id: '28', membreId: '5', membreNom: 'Diallo Aissatou', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montant: 25000, modePaiement: 'VIREMENT', statut: 'CONFIRME', datePaiement: '2024-08-12', reference: 'VIR-20240812-028', createdAt: '2024-08-12' },
  { id: '29', membreId: '7', membreNom: 'Sy Mariama', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ORANGE_MONEY', statut: 'ANNULE', datePaiement: '2024-08-15', reference: 'OM-20240815-029', createdAt: '2024-08-15' },
  { id: '30', membreId: '9', membreNom: 'Kane Fatou', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montant: 50000, modePaiement: 'VIREMENT', statut: 'CONFIRME', datePaiement: '2024-08-18', reference: 'VIR-20240818-030', createdAt: '2024-08-18' },
  { id: '31', membreId: '11', membreNom: 'Thiam Boubacar', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montant: 5000, modePaiement: 'ESPECES', statut: 'EN_ATTENTE', datePaiement: '2024-08-20', createdAt: '2024-08-20' },
  { id: '32', membreId: '13', membreNom: 'Sarr Awa', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montant: 2000, modePaiement: 'WAVE', statut: 'ECHEC', datePaiement: '2024-08-22', reference: 'WAV-20240822-032', createdAt: '2024-08-22' },
];

// ---------------------------------------------------------------------------
// Columns
// ---------------------------------------------------------------------------

const columns: Column<PaiementRow & Record<string, unknown>>[] = [
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
    key: 'modePaiement',
    header: 'Mode',
    sortable: true,
    render: (item) => (
      <span className="text-sm">
        {MODE_PAIEMENT_LABELS[item.modePaiement as string] || String(item.modePaiement)}
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
        label={PAIEMENT_STATUT_LABELS[String(item.statut)]}
      />
    ),
  },
  {
    key: 'datePaiement',
    header: 'Date',
    sortable: true,
    render: (item) => (
      <span className="text-sm">{String(item.datePaiement ?? '')}</span>
    ),
  },
];

// ---------------------------------------------------------------------------
// Page Component
// ---------------------------------------------------------------------------

export default function PaiementsPage() {
  const [paiements, setPaiements] = useState(initialPaiements);
  const [cancelOpen, setCancelOpen] = useState(false);
  const [selectedPaiement, setSelectedPaiement] = useState<(PaiementRow & Record<string, unknown>) | null>(null);
  const [selectedPaiements, setSelectedPaiements] = useState<(PaiementRow & Record<string, unknown>)[]>([]);

  const handleCancel = () => {
    if (selectedPaiement) {
      setPaiements((prev) =>
        prev.map((p) =>
          p.id === selectedPaiement.id ? { ...p, statut: 'ANNULE' as const } : p,
        ),
      );
      setCancelOpen(false);
      setSelectedPaiement(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Paiements"
        description="Historique des paiements de cotisations"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={paiements as (PaiementRow & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['membreNom', 'cotisationLibelle', 'reference']}
            searchPlaceholder="Rechercher un paiement..."
            pageSize={10}
            exportable
            exportFilename="paiements"
            selectable
            onSelectionChange={(items) => setSelectedPaiements(items as (PaiementRow & Record<string, unknown>)[])}
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'EN_ATTENTE' },
                  { label: 'Confirmé', value: 'CONFIRME' },
                  { label: 'Échoué', value: 'ECHEC' },
                  { label: 'Annulé', value: 'ANNULE' },
                ],
              },
              {
                key: 'modePaiement',
                label: 'Mode',
                options: Object.entries(MODE_PAIEMENT_LABELS).map(([value, label]) => ({
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
              }[] = [];
              if (item.statut === 'EN_ATTENTE' || item.statut === 'CONFIRME') {
                actions.push({
                  label: 'Annuler le paiement',
                  onClick: () => {
                    setSelectedPaiement(item);
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

      <ConfirmDialog
        open={cancelOpen}
        onOpenChange={setCancelOpen}
        title="Annuler le paiement"
        description={`Êtes-vous sûr de vouloir annuler le paiement de ${selectedPaiement ? formatMoney(selectedPaiement.montant as number) : ''} ?`}
        confirmLabel="Annuler le paiement"
        variant="destructive"
        onConfirm={handleCancel}
      />
    </div>
  );
}
