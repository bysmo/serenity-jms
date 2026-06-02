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
import { format } from 'date-fns';
import { fr } from 'date-fns/locale/fr';
import type { KycVerification, KycStatut } from '@/types';

// ── Member name lookup ───────────────────────────────────────────────────
const memberNames: Record<string, string> = {
  '1': 'Diop Aminata',
  '2': 'Ndiaye Fatou',
  '3': 'Sow Mamadou',
  '4': 'Ba Ibrahima',
  '5': 'Diallo Aissatou',
  '6': 'Fall Ousmane',
  '7': 'Sy Mariama',
  '8': 'Gueye Moussa',
  '9': 'Sarr Fatoumata',
  '10': 'Mbaye Cheikh',
  '11': 'Kane Khady',
  '12': 'Thiam Abdoulaye',
  '13': 'Cissé Maimouna',
  '14': 'Toure Seydou',
  '15': 'Konaté Awa',
  '16': 'Camara Ibrahima',
  '17': 'Barry Fatoumata',
  '18': 'Dembélé Oumar',
  '19': 'Traoré Kadiatou',
  '20': 'Keita Modibo',
  '21': 'Sissoko Boubacar',
  '22': 'Coulibaly Aminata',
  '23': 'Sanogo Drissa',
  '24': 'Haidara Oumou',
  '25': 'Maïga Adama',
  '26': 'Sakho Birame',
  '27': 'Wane Ndèye',
  '28': 'Faye Moustapha',
};

// ── KYC niveau labels ────────────────────────────────────────────────────
const KYC_NIVEAU_LABELS: Record<string, string> = {
  NIVEAU_0: 'Niveau 0',
  NIVEAU_1: 'Niveau 1',
  NIVEAU_2: 'Niveau 2',
  NIVEAU_3: 'Niveau 3',
};

// ── Mock data: 24 KYC entries ────────────────────────────────────────────
const mockKyc: KycVerification[] = [
  {
    id: '1',
    membreId: '1',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_3',
    validatedBy: 'Admin Diallo',
    validatedAt: '2024-01-25T10:00:00',
    documents: [],
    createdAt: '2024-01-20T08:00:00',
  },
  {
    id: '2',
    membreId: '2',
    statut: 'EN_COURS',
    niveau: 'NIVEAU_2',
    documents: [],
    createdAt: '2024-03-15T14:00:00',
  },
  {
    id: '3',
    membreId: '3',
    statut: 'REJETÉ',
    niveau: 'NIVEAU_1',
    motifRejet: 'Documents illisibles. Veuillez soumettre des copies claires.',
    rejectedBy: 'Admin Sarr',
    rejectedAt: '2024-04-05T09:00:00',
    documents: [],
    createdAt: '2024-04-01T11:00:00',
  },
  {
    id: '4',
    membreId: '4',
    statut: 'EN_ATTENTE',
    niveau: 'NIVEAU_1',
    documents: [],
    createdAt: '2024-05-10T16:30:00',
  },
  {
    id: '5',
    membreId: '5',
    statut: 'EN_COURS',
    niveau: 'NIVEAU_2',
    documents: [],
    createdAt: '2024-06-20T10:15:00',
  },
  {
    id: '6',
    membreId: '6',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_2',
    validatedBy: 'Admin Ndiaye',
    validatedAt: '2024-02-18T14:00:00',
    documents: [],
    createdAt: '2024-02-10T09:00:00',
  },
  {
    id: '7',
    membreId: '7',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_1',
    validatedBy: 'Admin Diop',
    validatedAt: '2024-08-01T10:00:00',
    documents: [],
    createdAt: '2024-07-22T13:30:00',
  },
  {
    id: '8',
    membreId: '8',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_3',
    validatedBy: 'Admin Fall',
    validatedAt: '2024-09-05T11:00:00',
    documents: [],
    createdAt: '2024-08-30T07:00:00',
  },
  {
    id: '9',
    membreId: '9',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_3',
    validatedBy: 'Admin Diallo',
    validatedAt: '2024-09-20T09:00:00',
    documents: [],
    createdAt: '2024-09-15T10:20:00',
  },
  {
    id: '10',
    membreId: '10',
    statut: 'EN_ATTENTE',
    niveau: 'NIVEAU_0',
    documents: [],
    createdAt: '2024-10-01T15:00:00',
  },
  {
    id: '11',
    membreId: '11',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_3',
    validatedBy: 'Admin Sarr',
    validatedAt: '2024-01-30T08:00:00',
    documents: [],
    createdAt: '2024-01-22T09:00:00',
  },
  {
    id: '12',
    membreId: '12',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_2',
    validatedBy: 'Admin Diop',
    validatedAt: '2024-02-20T10:00:00',
    documents: [],
    createdAt: '2024-02-14T12:30:00',
  },
  {
    id: '13',
    membreId: '13',
    statut: 'REJETÉ',
    niveau: 'NIVEAU_1',
    motifRejet: 'Pièce d\'identité expirée. Veuillez fournir un document en cours de validité.',
    rejectedBy: 'Admin Gueye',
    rejectedAt: '2024-05-15T14:00:00',
    documents: [],
    createdAt: '2024-03-28T16:00:00',
  },
  {
    id: '14',
    membreId: '14',
    statut: 'EN_COURS',
    niveau: 'NIVEAU_2',
    documents: [],
    createdAt: '2024-04-18T08:45:00',
  },
  {
    id: '15',
    membreId: '15',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_2',
    validatedBy: 'Admin Fall',
    validatedAt: '2024-05-20T09:00:00',
    documents: [],
    createdAt: '2024-05-05T14:20:00',
  },
  {
    id: '16',
    membreId: '16',
    statut: 'EN_ATTENTE',
    niveau: 'NIVEAU_0',
    documents: [],
    createdAt: '2024-06-10T10:00:00',
  },
  {
    id: '17',
    membreId: '17',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_3',
    validatedBy: 'Admin Ndiaye',
    validatedAt: '2024-07-10T10:00:00',
    documents: [],
    createdAt: '2024-07-03T11:30:00',
  },
  {
    id: '18',
    membreId: '18',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_2',
    validatedBy: 'Admin Diallo',
    validatedAt: '2024-08-20T11:00:00',
    documents: [],
    createdAt: '2024-08-15T09:45:00',
  },
  {
    id: '19',
    membreId: '19',
    statut: 'REJETÉ',
    niveau: 'NIVEAU_1',
    motifRejet: 'Justificatif de domicile non conforme. Adresse introuvable.',
    rejectedBy: 'Admin Sarr',
    rejectedAt: '2024-10-10T09:00:00',
    documents: [],
    createdAt: '2024-09-02T13:00:00',
  },
  {
    id: '20',
    membreId: '20',
    statut: 'EN_COURS',
    niveau: 'NIVEAU_2',
    documents: [],
    createdAt: '2024-09-20T07:30:00',
  },
  {
    id: '21',
    membreId: '21',
    statut: 'VALIDÉ',
    niveau: 'NIVEAU_3',
    validatedBy: 'Admin Diop',
    validatedAt: '2024-10-15T10:00:00',
    documents: [],
    createdAt: '2024-10-08T15:15:00',
  },
  {
    id: '22',
    membreId: '22',
    statut: 'EN_ATTENTE',
    niveau: 'NIVEAU_0',
    documents: [],
    createdAt: '2024-10-25T10:00:00',
  },
  {
    id: '23',
    membreId: '23',
    statut: 'REJETÉ',
    niveau: 'NIVEAU_1',
    motifRejet: 'Photo de mauvaise qualité sur la pièce d\'identité.',
    rejectedBy: 'Admin Fall',
    rejectedAt: '2024-12-05T14:00:00',
    documents: [],
    createdAt: '2024-11-01T08:30:00',
  },
  {
    id: '24',
    membreId: '24',
    statut: 'EN_COURS',
    niveau: 'NIVEAU_2',
    documents: [],
    createdAt: '2024-11-15T14:00:00',
  },
];

// ── Extended type for display with membreNom ─────────────────────────────
type KycRow = KycVerification & Record<string, unknown> & { membreNom: string };

// ── Build display rows with membreNom ────────────────────────────────────
const buildKycRows = (kycList: KycVerification[]): KycRow[] =>
  kycList.map((k) => ({
    ...k,
    membreNom: memberNames[k.membreId] || k.membreId,
  }));

// ── Columns ──────────────────────────────────────────────────────────────
const columns: Column<KycRow>[] = [
  {
    key: 'membreNom',
    header: 'Membre',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{item.membreNom as string}</span>
    ),
  },
  {
    key: 'niveau',
    header: 'Niveau',
    sortable: true,
    render: (item) => (
      <span className="text-sm">
        {KYC_NIVEAU_LABELS[item.niveau as string] || (item.niveau as string)}
      </span>
    ),
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => (
      <StatusBadge statut={item.statut as string} />
    ),
  },
  {
    key: 'createdAt',
    header: 'Date initiation',
    sortable: true,
    render: (item) => (
      <span className="text-sm text-muted-foreground">
        {format(new Date(item.createdAt as string), 'dd MMM yyyy', { locale: fr })}
      </span>
    ),
  },
  {
    key: 'validatedBy',
    header: 'Validé par',
    render: (item) => (
      <span className="text-sm">
        {(item.validatedBy as string) || '—'}
      </span>
    ),
  },
];

// ── Page Component ───────────────────────────────────────────────────────
export default function KycPage() {
  const [kycList, setKycList] = useState(mockKyc);
  const [validateOpen, setValidateOpen] = useState(false);
  const [rejectOpen, setRejectOpen] = useState(false);
  const [selectedKyc, setSelectedKyc] = useState<KycRow | null>(null);
  const [rejectMotif, setRejectMotif] = useState('');

  const kycRows = buildKycRows(kycList);

  // ── Handlers ──────────────────────────────────────────────────────────
  const handleValidate = () => {
    if (selectedKyc) {
      setKycList((prev) =>
        prev.map((k) =>
          k.id === selectedKyc.id
            ? {
                ...k,
                statut: 'VALIDÉ' as KycStatut,
                validatedBy: 'Admin Connecté',
                validatedAt: new Date().toISOString(),
              }
            : k
        )
      );
      setValidateOpen(false);
      setSelectedKyc(null);
    }
  };

  const handleReject = () => {
    if (selectedKyc) {
      setKycList((prev) =>
        prev.map((k) =>
          k.id === selectedKyc.id
            ? {
                ...k,
                statut: 'REJETÉ' as KycStatut,
                motifRejet: rejectMotif,
                rejectedBy: 'Admin Connecté',
                rejectedAt: new Date().toISOString(),
              }
            : k
        )
      );
      setRejectOpen(false);
      setSelectedKyc(null);
      setRejectMotif('');
    }
  };

  const canValidate = (statut: string) =>
    statut === 'EN_ATTENTE' || statut === 'EN_COURS';

  // ── Render ────────────────────────────────────────────────────────────
  return (
    <div className="space-y-6">
      <PageHeader
        title="Vérifications KYC"
        description="Gestion des vérifications d'identité des membres"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={kycRows}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['membreNom']}
            searchPlaceholder="Rechercher par membre..."
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'EN_ATTENTE' },
                  { label: 'En cours', value: 'EN_COURS' },
                  { label: 'Validé', value: 'VALIDÉ' },
                  { label: 'Rejeté', value: 'REJETÉ' },
                ],
              },
            ]}
            pageSize={10}
            selectable
            onSelectionChange={(selected) => {
              console.log('KYC sélectionnés:', selected.length);
            }}
            exportable
            exportFilename="kyc-verifications"
            actions={(item) => {
              const actions: {
                label: string;
                onClick: () => void;
                variant?: 'default' | 'destructive';
              }[] = [{ label: 'Voir documents', onClick: () => {} }];

              if (canValidate(item.statut as string)) {
                actions.push({
                  label: 'Valider',
                  onClick: () => {
                    setSelectedKyc(item);
                    setValidateOpen(true);
                  },
                });
                actions.push({
                  label: 'Rejeter',
                  onClick: () => {
                    setSelectedKyc(item);
                    setRejectMotif('');
                    setRejectOpen(true);
                  },
                  variant: 'destructive',
                });
              }
              return actions;
            }}
          />
        </CardContent>
      </Card>

      {/* ── Validate Dialog ───────────────────────────────────────────── */}
      <Dialog open={validateOpen} onOpenChange={setValidateOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Valider la vérification KYC</DialogTitle>
          </DialogHeader>
          <p className="text-sm text-muted-foreground">
            Êtes-vous sûr de vouloir valider la vérification KYC de{' '}
            <span className="font-semibold text-foreground">
              {selectedKyc ? memberNames[selectedKyc.membreId as string] : ''}
            </span>{' '}
            (
            {selectedKyc
              ? KYC_NIVEAU_LABELS[selectedKyc.niveau as string]
              : ''}
            ) ?
          </p>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setValidateOpen(false)}
            >
              Annuler
            </Button>
            <Button onClick={handleValidate} className="gap-2">
              <CheckCircle className="h-4 w-4" />
              Valider
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ── Reject Dialog ─────────────────────────────────────────────── */}
      <Dialog open={rejectOpen} onOpenChange={setRejectOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Rejeter la vérification KYC</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Rejeter la vérification de{' '}
              <span className="font-semibold text-foreground">
                {selectedKyc
                  ? memberNames[selectedKyc.membreId as string]
                  : ''}
              </span>
            </p>
            <div className="space-y-2">
              <Label htmlFor="reject-motif">Motif du rejet</Label>
              <Textarea
                id="reject-motif"
                value={rejectMotif}
                onChange={(e) => setRejectMotif(e.target.value)}
                placeholder="Expliquez la raison du rejet..."
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setRejectOpen(false)}
            >
              Annuler
            </Button>
            <Button
              variant="destructive"
              onClick={handleReject}
              className="gap-2"
            >
              <XCircle className="h-4 w-4" />
              Rejeter
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
