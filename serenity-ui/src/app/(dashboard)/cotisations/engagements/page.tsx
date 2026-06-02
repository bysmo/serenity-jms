'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Card, CardContent } from '@/components/ui/card';
import type { Engagement, EngagementStatut } from '@/types';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const formatMoney = (amount: number): string =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

const ENGAGEMENT_STATUT_LABELS: Record<string, string> = {
  EN_COURS: 'En cours',
  TERMINE: 'Terminé',
  ANNULE: 'Annulé',
  SUSPENDU: 'Suspendu',
};

// ---------------------------------------------------------------------------
// Mock Data — with membreNom & cotisationLibelle for sorting
// ---------------------------------------------------------------------------

type EngagementRow = Engagement & { membreNom: string; cotisationLibelle: string };

const initialEngagements: EngagementRow[] = [
  { id: '1', membreId: '1', membreNom: 'Diop Aminata', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 45000, periodicite: 'MENSUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'EN_COURS', createdAt: '2024-01-15' },
  { id: '2', membreId: '2', membreNom: 'Ndiaye Fatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 60000, periodicite: 'MENSUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'TERMINE', createdAt: '2024-02-20' },
  { id: '3', membreId: '3', membreNom: 'Sow Mamadou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantEngage: 24000, montantPaye: 8000, periodicite: 'MENSUELLE', periodeDebut: '2024-03-01', periodeFin: '2025-02-28', statut: 'SUSPENDU', createdAt: '2024-03-10' },
  { id: '4', membreId: '4', membreNom: 'Ba Ibrahima', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 30000, periodicite: 'MENSUELLE', periodeDebut: '2024-04-01', periodeFin: '2025-03-31', statut: 'EN_COURS', createdAt: '2024-04-05' },
  { id: '5', membreId: '5', membreNom: 'Diallo Aissatou', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantEngage: 50000, montantPaye: 0, periodicite: 'ANNUELLE', periodeDebut: '2024-05-01', periodeFin: '2024-12-31', statut: 'ANNULE', createdAt: '2024-05-12' },
  { id: '6', membreId: '6', membreNom: 'Fall Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 55000, periodicite: 'MENSUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'EN_COURS', createdAt: '2024-06-18' },
  { id: '7', membreId: '7', membreNom: 'Sy Mariama', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantEngage: 120000, montantPaye: 40000, periodicite: 'MENSUELLE', periodeDebut: '2024-02-01', periodeFin: '2025-01-31', statut: 'EN_COURS', createdAt: '2024-02-10' },
  { id: '8', membreId: '8', membreNom: 'Gueye Moussa', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantEngage: 24000, montantPaye: 24000, periodicite: 'MENSUELLE', periodeDebut: '2023-06-01', periodeFin: '2024-05-31', statut: 'TERMINE', createdAt: '2023-06-01' },
  { id: '9', membreId: '9', membreNom: 'Kane Fatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 20000, periodicite: 'MENSUELLE', periodeDebut: '2024-03-01', periodeFin: '2025-02-28', statut: 'EN_COURS', createdAt: '2024-03-05' },
  { id: '10', membreId: '10', membreNom: 'Mbaye Cheikh', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantEngage: 50000, montantPaye: 50000, periodicite: 'ANNUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'TERMINE', createdAt: '2024-01-08' },
  { id: '11', membreId: '11', membreNom: 'Thiam Boubacar', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantEngage: 24000, montantPaye: 18000, periodicite: 'MENSUELLE', periodeDebut: '2024-04-01', periodeFin: '2025-03-31', statut: 'EN_COURS', createdAt: '2024-04-12' },
  { id: '12', membreId: '12', membreNom: 'Cissé Adama', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 60000, periodicite: 'MENSUELLE', periodeDebut: '2023-07-01', periodeFin: '2024-06-30', statut: 'TERMINE', createdAt: '2023-07-01' },
  { id: '13', membreId: '13', membreNom: 'Sarr Awa', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantEngage: 120000, montantPaye: 90000, periodicite: 'MENSUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'EN_COURS', createdAt: '2024-01-02' },
  { id: '14', membreId: '14', membreNom: 'Niang Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 10000, periodicite: 'MENSUELLE', periodeDebut: '2024-06-01', periodeFin: '2025-05-31', statut: 'SUSPENDU', createdAt: '2024-06-01' },
  { id: '15', membreId: '15', membreNom: 'Diop Ibrahima', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantEngage: 50000, montantPaye: 25000, periodicite: 'ANNUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'EN_COURS', createdAt: '2024-01-10' },
  { id: '16', membreId: '16', membreNom: 'Sow Khady', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantEngage: 24000, montantPaye: 0, periodicite: 'MENSUELLE', periodeDebut: '2024-07-01', periodeFin: '2025-06-30', statut: 'ANNULE', createdAt: '2024-07-01' },
  { id: '17', membreId: '17', membreNom: 'Bâ Mamadou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 35000, periodicite: 'MENSUELLE', periodeDebut: '2024-02-01', periodeFin: '2025-01-31', statut: 'EN_COURS', createdAt: '2024-02-05' },
  { id: '18', membreId: '18', membreNom: 'Faye Sokhna', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantEngage: 120000, montantPaye: 120000, periodicite: 'MENSUELLE', periodeDebut: '2023-01-01', periodeFin: '2023-12-31', statut: 'TERMINE', createdAt: '2023-01-01' },
  { id: '19', membreId: '19', membreNom: 'Mendy Ismaïla', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 5000, periodicite: 'MENSUELLE', periodeDebut: '2024-08-01', periodeFin: '2025-07-31', statut: 'EN_COURS', createdAt: '2024-08-01' },
  { id: '20', membreId: '20', membreNom: 'Dieng Fatima', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantEngage: 24000, montantPaye: 20000, periodicite: 'MENSUELLE', periodeDebut: '2024-05-01', periodeFin: '2025-04-30', statut: 'EN_COURS', createdAt: '2024-05-03' },
  { id: '21', membreId: '21', membreNom: 'Toure Abdoulaye', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantEngage: 100000, montantPaye: 100000, periodicite: 'ANNUELLE', periodeDebut: '2023-01-01', periodeFin: '2023-12-31', statut: 'TERMINE', createdAt: '2023-01-15' },
  { id: '22', membreId: '22', membreNom: 'Camara Mariatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 40000, periodicite: 'MENSUELLE', periodeDebut: '2024-03-01', periodeFin: '2025-02-28', statut: 'EN_COURS', createdAt: '2024-03-08' },
  { id: '23', membreId: '23', membreNom: 'Diallo Mamadou', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', montantEngage: 180000, montantPaye: 45000, periodicite: 'MENSUELLE', periodeDebut: '2024-04-01', periodeFin: '2025-03-31', statut: 'SUSPENDU', createdAt: '2024-04-01' },
  { id: '24', membreId: '24', membreNom: 'Wane Aminata', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', montantEngage: 36000, montantPaye: 36000, periodicite: 'MENSUELLE', periodeDebut: '2023-04-01', periodeFin: '2024-03-31', statut: 'TERMINE', createdAt: '2023-04-01' },
  { id: '25', membreId: '25', membreNom: 'Seck Birame', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 50000, periodicite: 'MENSUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'EN_COURS', createdAt: '2024-01-05' },
  { id: '26', membreId: '26', membreNom: 'Ly Oumar', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', montantEngage: 75000, montantPaye: 0, periodicite: 'ANNUELLE', periodeDebut: '2024-06-01', periodeFin: '2024-12-31', statut: 'ANNULE', createdAt: '2024-06-10' },
  { id: '27', membreId: '27', membreNom: 'Goudiaby Awa', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', montantEngage: 60000, montantPaye: 60000, periodicite: 'MENSUELLE', periodeDebut: '2024-01-01', periodeFin: '2024-12-31', statut: 'TERMINE', createdAt: '2024-01-12' },
];

// ---------------------------------------------------------------------------
// Columns
// ---------------------------------------------------------------------------

const columns: Column<EngagementRow & Record<string, unknown>>[] = [
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
    sortable: true,
    render: (item) => (
      <span>{String(item.cotisationLibelle)}</span>
    ),
  },
  {
    key: 'montantEngage',
    header: 'Montant Engagé',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatMoney(item.montantEngage as number)}</span>
    ),
  },
  {
    key: 'montantPaye',
    header: 'Montant Payé',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatMoney(item.montantPaye as number)}</span>
    ),
  },
  {
    key: 'periodicite',
    header: 'Période',
    render: (item) => (
      <span className="text-sm">
        {String(item.periodeDebut ?? '')} → {String(item.periodeFin ?? '')}
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
        label={ENGAGEMENT_STATUT_LABELS[String(item.statut)]}
      />
    ),
  },
];

// ---------------------------------------------------------------------------
// Page Component
// ---------------------------------------------------------------------------

export default function EngagementsPage() {
  const [engagements, setEngagements] = useState(initialEngagements);
  const [updateOpen, setUpdateOpen] = useState(false);
  const [selectedEngagement, setSelectedEngagement] = useState<(EngagementRow & Record<string, unknown>) | null>(null);
  const [newStatut, setNewStatut] = useState<string>('');

  const handleUpdateStatut = () => {
    if (selectedEngagement && newStatut) {
      setEngagements((prev) =>
        prev.map((e) =>
          e.id === selectedEngagement.id
            ? { ...e, statut: newStatut as EngagementStatut }
            : e,
        ),
      );
      setUpdateOpen(false);
      setSelectedEngagement(null);
      setNewStatut('');
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Engagements"
        description="Suivi des engagements de cotisation des membres"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={engagements as (EngagementRow & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['membreNom', 'cotisationLibelle']}
            searchPlaceholder="Rechercher par membre ou cotisation..."
            pageSize={10}
            exportable
            exportFilename="engagements"
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En cours', value: 'EN_COURS' },
                  { label: 'Terminé', value: 'TERMINE' },
                  { label: 'Annulé', value: 'ANNULE' },
                  { label: 'Suspendu', value: 'SUSPENDU' },
                ],
              },
            ]}
            actions={(item) => [
              {
                label: 'Modifier le statut',
                onClick: () => {
                  setSelectedEngagement(item);
                  setNewStatut(String(item.statut));
                  setUpdateOpen(true);
                },
              },
            ]}
          />
        </CardContent>
      </Card>

      {/* Update Statut Dialog */}
      <Dialog open={updateOpen} onOpenChange={setUpdateOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Modifier le statut de l&apos;engagement</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Engagement de{' '}
              <span className="font-medium">
                {selectedEngagement ? String(selectedEngagement.membreNom) : ''}
              </span>{' '}
              pour{' '}
              <span className="font-medium">
                {selectedEngagement
                  ? String(selectedEngagement.cotisationLibelle)
                  : ''}
              </span>
            </p>
            <Select value={newStatut} onValueChange={setNewStatut}>
              <SelectTrigger>
                <SelectValue placeholder="Sélectionner un statut" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="EN_COURS">En cours</SelectItem>
                <SelectItem value="TERMINE">Terminé</SelectItem>
                <SelectItem value="ANNULE">Annulé</SelectItem>
                <SelectItem value="SUSPENDU">Suspendu</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setUpdateOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleUpdateStatut} disabled={!newStatut}>
              Enregistrer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
