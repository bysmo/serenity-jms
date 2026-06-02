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
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { CheckCircle, XCircle } from 'lucide-react';
import type { Adhesion, AdhesionStatut } from '@/types';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const ADHESION_STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  ACCEPTEE: 'Acceptée',
  REFUSEE: 'Refusée',
};

// ---------------------------------------------------------------------------
// Mock Data — with membreNom & cotisationLibelle for sorting
// ---------------------------------------------------------------------------

type AdhesionRow = Adhesion & { membreNom: string; cotisationLibelle: string };

const adminNames: Record<string, string> = {
  admin1: 'Admin Diallo',
  admin2: 'Admin Sarr',
  admin3: 'Admin Ndiaye',
};

const initialAdhesions: AdhesionRow[] = [
  { id: '1', membreId: '1', membreNom: 'Diop Aminata', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'ACCEPTEE', traitePar: 'admin1', dateTraitement: '2024-01-20', createdAt: '2024-01-15' },
  { id: '2', membreId: '3', membreNom: 'Sow Mamadou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', statut: 'EN_ATTENTE', createdAt: '2024-06-10' },
  { id: '3', membreId: '5', membreNom: 'Diallo Aissatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'EN_ATTENTE', createdAt: '2024-06-12' },
  { id: '4', membreId: '7', membreNom: 'Sy Mariama', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', statut: 'REFUSEE', traitePar: 'admin2', dateTraitement: '2024-05-22', motifRefus: 'Non éligible — pièces justificatives insuffisantes', createdAt: '2024-05-20' },
  { id: '5', membreId: '2', membreNom: 'Ndiaye Fatou', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', statut: 'ACCEPTEE', traitePar: 'admin1', dateTraitement: '2024-04-02', createdAt: '2024-04-01' },
  { id: '6', membreId: '4', membreNom: 'Ba Ibrahima', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', statut: 'EN_ATTENTE', createdAt: '2024-06-18' },
  { id: '7', membreId: '6', membreNom: 'Fall Ousmane', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', statut: 'EN_ATTENTE', createdAt: '2024-06-20' },
  { id: '8', membreId: '8', membreNom: 'Gueye Moussa', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'ACCEPTEE', traitePar: 'admin1', dateTraitement: '2024-02-15', createdAt: '2024-02-10' },
  { id: '9', membreId: '9', membreNom: 'Kane Fatou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', statut: 'REFUSEE', traitePar: 'admin2', dateTraitement: '2024-03-12', motifRefus: 'Cotisation réservée aux membres actifs depuis plus de 6 mois', createdAt: '2024-03-10' },
  { id: '10', membreId: '10', membreNom: 'Mbaye Cheikh', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', statut: 'ACCEPTEE', traitePar: 'admin3', dateTraitement: '2024-05-01', createdAt: '2024-04-28' },
  { id: '11', membreId: '11', membreNom: 'Thiam Boubacar', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'ACCEPTEE', traitePar: 'admin1', dateTraitement: '2024-06-05', createdAt: '2024-06-01' },
  { id: '12', membreId: '12', membreNom: 'Cissé Adama', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', statut: 'EN_ATTENTE', createdAt: '2024-07-02' },
  { id: '13', membreId: '13', membreNom: 'Sarr Awa', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', statut: 'ACCEPTEE', traitePar: 'admin2', dateTraitement: '2024-07-08', createdAt: '2024-07-05' },
  { id: '14', membreId: '14', membreNom: 'Niang Ousmane', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'EN_ATTENTE', createdAt: '2024-07-10' },
  { id: '15', membreId: '15', membreNom: 'Diop Ibrahima', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', statut: 'REFUSEE', traitePar: 'admin1', dateTraitement: '2024-07-18', motifRefus: 'Dossier incomplet — justificatif de revenus manquant', createdAt: '2024-07-15' },
  { id: '16', membreId: '16', membreNom: 'Sow Khady', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'ACCEPTEE', traitePar: 'admin3', dateTraitement: '2024-07-22', createdAt: '2024-07-20' },
  { id: '17', membreId: '17', membreNom: 'Bâ Mamadou', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', statut: 'EN_ATTENTE', createdAt: '2024-08-01' },
  { id: '18', membreId: '18', membreNom: 'Faye Sokhna', cotisationId: '3', cotisationLibelle: 'Cotisation Annuelle Extraordinaire', statut: 'ACCEPTEE', traitePar: 'admin2', dateTraitement: '2024-08-05', createdAt: '2024-08-02' },
  { id: '19', membreId: '19', membreNom: 'Mendy Ismaïla', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'EN_ATTENTE', createdAt: '2024-08-08' },
  { id: '20', membreId: '20', membreNom: 'Dieng Fatima', cotisationId: '4', cotisationLibelle: 'Épargne Prévoyance', statut: 'ACCEPTEE', traitePar: 'admin1', dateTraitement: '2024-08-12', createdAt: '2024-08-10' },
  { id: '21', membreId: '21', membreNom: 'Toure Abdoulaye', cotisationId: '2', cotisationLibelle: 'Fonds de Solidarité', statut: 'REFUSEE', traitePar: 'admin3', dateTraitement: '2024-08-18', motifRefus: 'Suspicion de compte duplicata — vérification en cours', createdAt: '2024-08-15' },
  { id: '22', membreId: '22', membreNom: 'Camara Mariatou', cotisationId: '1', cotisationLibelle: 'Cotisation Mensuelle Ordinaire', statut: 'EN_ATTENTE', createdAt: '2024-08-20' },
];

// ---------------------------------------------------------------------------
// Columns
// ---------------------------------------------------------------------------

const columns: Column<AdhesionRow & Record<string, unknown>>[] = [
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
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => (
      <StatusBadge
        statut={String(item.statut)}
        label={ADHESION_STATUT_LABELS[String(item.statut)]}
      />
    ),
  },
  {
    key: 'createdAt',
    header: 'Date demande',
    sortable: true,
    render: (item) => <span className="text-sm">{String(item.createdAt ?? '')}</span>,
  },
  {
    key: 'traitePar',
    header: 'Traité par',
    render: (item) =>
      item.traitePar ? (
        <span className="text-sm">
          {adminNames[String(item.traitePar)] || String(item.traitePar)}
        </span>
      ) : (
        <span className="text-sm text-muted-foreground">—</span>
      ),
  },
];

// ---------------------------------------------------------------------------
// Page Component
// ---------------------------------------------------------------------------

export default function AdhesionsPage() {
  const [adhesions, setAdhesions] = useState(initialAdhesions);
  const [rejectOpen, setRejectOpen] = useState(false);
  const [acceptOpen, setAcceptOpen] = useState(false);
  const [selectedAdhesion, setSelectedAdhesion] = useState<(AdhesionRow & Record<string, unknown>) | null>(null);
  const [rejectMotif, setRejectMotif] = useState('');

  const openAcceptDialog = (item: AdhesionRow & Record<string, unknown>) => {
    setSelectedAdhesion(item);
    setAcceptOpen(true);
  };

  const openRejectDialog = (item: AdhesionRow & Record<string, unknown>) => {
    setSelectedAdhesion(item);
    setRejectMotif('');
    setRejectOpen(true);
  };

  const handleAccept = () => {
    if (selectedAdhesion) {
      setAdhesions((prev) =>
        prev.map((a) =>
          a.id === selectedAdhesion.id
            ? { ...a, statut: 'ACCEPTEE' as AdhesionStatut, traitePar: 'admin1', dateTraitement: new Date().toISOString().slice(0, 10) }
            : a,
        ),
      );
      setAcceptOpen(false);
      setSelectedAdhesion(null);
    }
  };

  const handleReject = () => {
    if (selectedAdhesion) {
      setAdhesions((prev) =>
        prev.map((a) =>
          a.id === selectedAdhesion.id
            ? { ...a, statut: 'REFUSEE' as AdhesionStatut, motifRefus: rejectMotif, traitePar: 'admin1', dateTraitement: new Date().toISOString().slice(0, 10) }
            : a,
        ),
      );
      setRejectOpen(false);
      setSelectedAdhesion(null);
      setRejectMotif('');
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Adhésions"
        description="Gestion des demandes d'adhésion aux cotisations"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={adhesions as (AdhesionRow & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['membreNom', 'cotisationLibelle']}
            searchPlaceholder="Rechercher par membre ou cotisation..."
            pageSize={10}
            exportable
            exportFilename="adhesions"
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'EN_ATTENTE' },
                  { label: 'Acceptée', value: 'ACCEPTEE' },
                  { label: 'Refusée', value: 'REFUSEE' },
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
                  label: 'Accepter',
                  onClick: () => openAcceptDialog(item),
                });
                actions.push({
                  label: 'Refuser',
                  onClick: () => openRejectDialog(item),
                  variant: 'destructive',
                });
              }
              return actions;
            }}
          />
        </CardContent>
      </Card>

      {/* Accept Confirmation Dialog */}
      <Dialog open={acceptOpen} onOpenChange={setAcceptOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Accepter l&apos;adhésion</DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            <p className="text-sm text-muted-foreground">
              Accepter la demande d&apos;adhésion de{' '}
              <span className="font-medium">
                {selectedAdhesion ? String(selectedAdhesion.membreNom) : ''}
              </span>{' '}
              à la cotisation{' '}
              <span className="font-medium">
                {selectedAdhesion
                  ? String(selectedAdhesion.cotisationLibelle)
                  : ''}
              </span>{' '}
              ?
            </p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAcceptOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleAccept} className="gap-2">
              <CheckCircle className="h-4 w-4" />
              Accepter
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Reject Dialog */}
      <Dialog open={rejectOpen} onOpenChange={setRejectOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Refuser l&apos;adhésion</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Refuser la demande d&apos;adhésion de{' '}
              <span className="font-medium">
                {selectedAdhesion ? String(selectedAdhesion.membreNom) : ''}
              </span>
            </p>
            <div className="space-y-2">
              <Label>Motif du refus</Label>
              <Textarea
                value={rejectMotif}
                onChange={(e) => setRejectMotif(e.target.value)}
                placeholder="Expliquez la raison du refus..."
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setRejectOpen(false)}>
              Annuler
            </Button>
            <Button variant="destructive" onClick={handleReject} className="gap-2">
              <XCircle className="h-4 w-4" />
              Refuser
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
