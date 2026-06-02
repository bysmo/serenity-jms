'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Card, CardContent } from '@/components/ui/card';
import { UserPlus } from 'lucide-react';
import type { Membre, MembreStatut } from '@/types';

// ── Segment lookup ───────────────────────────────────────────────────────
const SEGMENTS: Record<string, string> = {
  '1': 'Premium',
  '2': 'Standard',
  '3': 'Basique',
  '4': 'Junior',
  '5': 'Senior',
};

// ── Mock data: 28 members ────────────────────────────────────────────────
const mockMembers: Membre[] = [
  {
    id: '1',
    numero: 'MEM-001',
    nom: 'Diop',
    prenom: 'Aminata',
    email: 'aminata.diop@email.com',
    telephone: '+221 77 123 45 67',
    statut: 'ACTIF',
    segmentId: '1',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-01-15T10:00:00',
    updatedAt: '2024-01-15T10:00:00',
  },
  {
    id: '2',
    numero: 'MEM-002',
    nom: 'Ndiaye',
    prenom: 'Fatou',
    email: 'fatou.ndiaye@email.com',
    telephone: '+221 78 234 56 78',
    statut: 'ACTIF',
    segmentId: '2',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-02-20T08:30:00',
    updatedAt: '2024-02-20T08:30:00',
  },
  {
    id: '3',
    numero: 'MEM-003',
    nom: 'Sow',
    prenom: 'Mamadou',
    email: 'mamadou.sow@email.com',
    telephone: '+221 76 345 67 89',
    statut: 'SUSPENDU',
    segmentId: '1',
    pays: 'SN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: true,
    telephoneVerifie: false,
    kycNiveau: 'NIVEAU_1',
    createdAt: '2024-03-10T14:00:00',
    updatedAt: '2024-05-10T14:00:00',
  },
  {
    id: '4',
    numero: 'MEM-004',
    nom: 'Ba',
    prenom: 'Ibrahima',
    email: 'ibrahima.ba@email.com',
    telephone: '+221 77 456 78 90',
    statut: 'ACTIF',
    segmentId: '2',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-04-05T09:15:00',
    updatedAt: '2024-04-05T09:15:00',
  },
  {
    id: '5',
    numero: 'MEM-005',
    nom: 'Diallo',
    prenom: 'Aissatou',
    email: 'aissatou.diallo@email.com',
    telephone: '+221 78 567 89 01',
    statut: 'EN_ATTENTE',
    segmentId: '3',
    pays: 'SN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: false,
    telephoneVerifie: false,
    kycNiveau: 'NIVEAU_0',
    createdAt: '2024-05-12T11:00:00',
    updatedAt: '2024-05-12T11:00:00',
  },
  {
    id: '6',
    numero: 'MEM-006',
    nom: 'Fall',
    prenom: 'Ousmane',
    email: 'ousmane.fall@email.com',
    telephone: '+221 76 678 90 12',
    statut: 'ACTIF',
    segmentId: '4',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-06-18T16:45:00',
    updatedAt: '2024-06-18T16:45:00',
  },
  {
    id: '7',
    numero: 'MEM-007',
    nom: 'Sy',
    prenom: 'Mariama',
    email: 'mariama.sy@email.com',
    telephone: '+221 77 789 01 23',
    statut: 'RADIÉ',
    segmentId: '2',
    pays: 'SN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_1',
    createdAt: '2024-07-22T13:30:00',
    updatedAt: '2024-09-22T13:30:00',
  },
  {
    id: '8',
    numero: 'MEM-008',
    nom: 'Gueye',
    prenom: 'Moussa',
    email: 'moussa.gueye@email.com',
    telephone: '+221 78 890 12 34',
    statut: 'ACTIF',
    segmentId: '5',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-08-30T07:00:00',
    updatedAt: '2024-08-30T07:00:00',
  },
  {
    id: '9',
    numero: 'MEM-009',
    nom: 'Sarr',
    prenom: 'Fatoumata',
    email: 'fatoumata.sarr@email.com',
    telephone: '+221 76 901 23 45',
    statut: 'ACTIF',
    segmentId: '1',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-09-15T10:20:00',
    updatedAt: '2024-09-15T10:20:00',
  },
  {
    id: '10',
    numero: 'MEM-010',
    nom: 'Mbaye',
    prenom: 'Cheikh',
    email: 'cheikh.mbaye@email.com',
    telephone: '+221 77 012 34 56',
    statut: 'EN_ATTENTE',
    segmentId: '3',
    pays: 'SN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: false,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_0',
    createdAt: '2024-10-01T15:00:00',
    updatedAt: '2024-10-01T15:00:00',
  },
  {
    id: '11',
    numero: 'MEM-011',
    nom: 'Kane',
    prenom: 'Khady',
    email: 'khady.kane@email.com',
    telephone: '+221 78 111 22 33',
    statut: 'ACTIF',
    segmentId: '1',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-01-22T09:00:00',
    updatedAt: '2024-01-22T09:00:00',
  },
  {
    id: '12',
    numero: 'MEM-012',
    nom: 'Thiam',
    prenom: 'Abdoulaye',
    email: 'abdoulaye.thiam@email.com',
    telephone: '+221 76 222 33 44',
    statut: 'ACTIF',
    segmentId: '2',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-02-14T12:30:00',
    updatedAt: '2024-02-14T12:30:00',
  },
  {
    id: '13',
    numero: 'MEM-013',
    nom: 'Cissé',
    prenom: 'Maimouna',
    email: 'maimouna.cisse@email.com',
    telephone: '+221 77 333 44 55',
    statut: 'SUSPENDU',
    segmentId: '3',
    pays: 'SN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: true,
    telephoneVerifie: false,
    kycNiveau: 'NIVEAU_1',
    createdAt: '2024-03-28T16:00:00',
    updatedAt: '2024-06-28T16:00:00',
  },
  {
    id: '14',
    numero: 'MEM-014',
    nom: 'Toure',
    prenom: 'Seydou',
    email: 'seydou.toure@email.com',
    telephone: '+221 78 444 55 66',
    statut: 'ACTIF',
    segmentId: '4',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-04-18T08:45:00',
    updatedAt: '2024-04-18T08:45:00',
  },
  {
    id: '15',
    numero: 'MEM-015',
    nom: 'Konaté',
    prenom: 'Awa',
    email: 'awa.konate@email.com',
    telephone: '+223 70 555 66 77',
    statut: 'ACTIF',
    segmentId: '2',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-05-05T14:20:00',
    updatedAt: '2024-05-05T14:20:00',
  },
  {
    id: '16',
    numero: 'MEM-016',
    nom: 'Camara',
    prenom: 'Ibrahima',
    email: 'ibrahima.camara@email.com',
    telephone: '+224 60 666 77 88',
    statut: 'EN_ATTENTE',
    segmentId: '3',
    pays: 'GN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: false,
    telephoneVerifie: false,
    kycNiveau: 'NIVEAU_0',
    createdAt: '2024-06-10T10:00:00',
    updatedAt: '2024-06-10T10:00:00',
  },
  {
    id: '17',
    numero: 'MEM-017',
    nom: 'Barry',
    prenom: 'Fatoumata',
    email: 'fatoumata.barry@email.com',
    telephone: '+224 61 777 88 99',
    statut: 'ACTIF',
    segmentId: '1',
    pays: 'GN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-07-03T11:30:00',
    updatedAt: '2024-07-03T11:30:00',
  },
  {
    id: '18',
    numero: 'MEM-018',
    nom: 'Dembélé',
    prenom: 'Oumar',
    email: 'oumar.dembele@email.com',
    telephone: '+223 71 888 99 00',
    statut: 'ACTIF',
    segmentId: '5',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-08-15T09:45:00',
    updatedAt: '2024-08-15T09:45:00',
  },
  {
    id: '19',
    numero: 'MEM-019',
    nom: 'Traoré',
    prenom: 'Kadiatou',
    email: 'kadiatou.traore@email.com',
    telephone: '+223 72 999 00 11',
    statut: 'RADIÉ',
    segmentId: '4',
    pays: 'ML',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_1',
    createdAt: '2024-09-02T13:00:00',
    updatedAt: '2024-11-02T13:00:00',
  },
  {
    id: '20',
    numero: 'MEM-020',
    nom: 'Keita',
    prenom: 'Modibo',
    email: 'modibo.keita@email.com',
    telephone: '+223 73 000 11 22',
    statut: 'ACTIF',
    segmentId: '2',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-09-20T07:30:00',
    updatedAt: '2024-09-20T07:30:00',
  },
  {
    id: '21',
    numero: 'MEM-021',
    nom: 'Sissoko',
    prenom: 'Boubacar',
    email: 'boubacar.sissoko@email.com',
    telephone: '+223 74 111 22 33',
    statut: 'ACTIF',
    segmentId: '1',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-10-08T15:15:00',
    updatedAt: '2024-10-08T15:15:00',
  },
  {
    id: '22',
    numero: 'MEM-022',
    nom: 'Coulibaly',
    prenom: 'Aminata',
    email: 'aminata.coulibaly@email.com',
    telephone: '+223 75 222 33 44',
    statut: 'EN_ATTENTE',
    segmentId: '4',
    pays: 'ML',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: false,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_0',
    createdAt: '2024-10-25T10:00:00',
    updatedAt: '2024-10-25T10:00:00',
  },
  {
    id: '23',
    numero: 'MEM-023',
    nom: 'Sanogo',
    prenom: 'Drissa',
    email: 'drissa.sanogo@email.com',
    telephone: '+223 76 333 44 55',
    statut: 'SUSPENDU',
    segmentId: '3',
    pays: 'ML',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: true,
    telephoneVerifie: false,
    kycNiveau: 'NIVEAU_1',
    createdAt: '2024-11-01T08:30:00',
    updatedAt: '2024-12-01T08:30:00',
  },
  {
    id: '24',
    numero: 'MEM-024',
    nom: 'Haidara',
    prenom: 'Oumou',
    email: 'oumou.haidara@email.com',
    telephone: '+223 77 444 55 66',
    statut: 'ACTIF',
    segmentId: '5',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-11-15T14:00:00',
    updatedAt: '2024-11-15T14:00:00',
  },
  {
    id: '25',
    numero: 'MEM-025',
    nom: 'Maïga',
    prenom: 'Adama',
    email: 'adama.maiga@email.com',
    telephone: '+223 78 555 66 77',
    statut: 'ACTIF',
    segmentId: '2',
    pays: 'ML',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-12-01T09:00:00',
    updatedAt: '2024-12-01T09:00:00',
  },
  {
    id: '26',
    numero: 'MEM-026',
    nom: 'Sakho',
    prenom: 'Birame',
    email: 'birame.sakho@email.com',
    telephone: '+221 76 666 77 88',
    statut: 'ACTIF',
    segmentId: '1',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_3',
    createdAt: '2024-12-10T11:30:00',
    updatedAt: '2024-12-10T11:30:00',
  },
  {
    id: '27',
    numero: 'MEM-027',
    nom: 'Wane',
    prenom: 'Ndèye',
    email: 'ndeye.wane@email.com',
    telephone: '+221 77 777 88 99',
    statut: 'EN_ATTENTE',
    segmentId: '3',
    pays: 'SN',
    pinEnabled: false,
    nanoCreditEligible: false,
    parrainageActif: false,
    emailVerifie: false,
    telephoneVerifie: false,
    kycNiveau: 'NIVEAU_0',
    createdAt: '2024-12-15T16:00:00',
    updatedAt: '2024-12-15T16:00:00',
  },
  {
    id: '28',
    numero: 'MEM-028',
    nom: 'Faye',
    prenom: 'Moustapha',
    email: 'moustapha.faye@email.com',
    telephone: '+221 78 888 99 00',
    statut: 'ACTIF',
    segmentId: '4',
    pays: 'SN',
    pinEnabled: true,
    nanoCreditEligible: true,
    parrainageActif: true,
    emailVerifie: true,
    telephoneVerifie: true,
    kycNiveau: 'NIVEAU_2',
    createdAt: '2024-12-20T08:00:00',
    updatedAt: '2024-12-20T08:00:00',
  },
];

// ── Statut labels for display ────────────────────────────────────────────
const STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  ACTIF: 'Actif',
  SUSPENDU: 'Suspendu',
  RADIÉ: 'Radié',
};

// ── Columns definition ───────────────────────────────────────────────────
const columns: Column<Membre & Record<string, unknown>>[] = [
  {
    key: 'numero',
    header: 'Numéro',
    sortable: true,
    render: (item) => (
      <span className="font-mono text-sm">{item.numero as string}</span>
    ),
  },
  {
    key: 'nom',
    header: 'Nom Complet',
    sortable: true,
    render: (item) => (
      <span className="font-medium">
        {item.prenom as string} {item.nom as string}
      </span>
    ),
  },
  {
    key: 'email',
    header: 'Email',
    sortable: true,
  },
  {
    key: 'telephone',
    header: 'Téléphone',
    sortable: true,
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => (
      <StatusBadge
        statut={item.statut as string}
        label={STATUT_LABELS[item.statut as string] || (item.statut as string)}
      />
    ),
  },
  {
    key: 'segmentId',
    header: 'Segment',
    render: (item) => (
      <span className="text-sm">
        {SEGMENTS[item.segmentId as string] || '-'}
      </span>
    ),
  },
];

// ── Form default ─────────────────────────────────────────────────────────
interface MemberForm {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  segmentId: string;
}

const EMPTY_FORM: MemberForm = {
  nom: '',
  prenom: '',
  email: '',
  telephone: '',
  segmentId: '2',
};

// ── Page Component ───────────────────────────────────────────────────────
export default function MembersPage() {
  const [members, setMembers] = useState(mockMembers);
  const [createOpen, setCreateOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<
    (Membre & Record<string, unknown>) | null
  >(null);
  const [formData, setFormData] = useState<MemberForm>({ ...EMPTY_FORM });

  // ── Handlers ─────────────────────────────────────────────────────────
  const handleCreate = () => {
    const newMember: Membre = {
      id: String(Date.now()),
      numero: `MEM-${String(members.length + 1).padStart(3, '0')}`,
      nom: formData.nom,
      prenom: formData.prenom,
      email: formData.email,
      telephone: formData.telephone,
      statut: 'EN_ATTENTE' as MembreStatut,
      segmentId: formData.segmentId,
      pays: 'SN',
      pinEnabled: false,
      nanoCreditEligible: false,
      parrainageActif: false,
      emailVerifie: false,
      telephoneVerifie: false,
      kycNiveau: 'NIVEAU_0',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    setMembers((prev) => [...prev, newMember]);
    setCreateOpen(false);
    setFormData({ ...EMPTY_FORM });
  };

  const handleDelete = () => {
    if (selectedMember) {
      setMembers((prev) => prev.filter((m) => m.id !== selectedMember.id));
      setDeleteOpen(false);
      setSelectedMember(null);
    }
  };

  // ── Render ───────────────────────────────────────────────────────────
  return (
    <div className="space-y-6">
      <PageHeader
        title="Gestion des Membres"
        description="Administration des membres de la coopérative"
        actions={
          <Button
            size="sm"
            className="gap-2"
            onClick={() => {
              setFormData({ ...EMPTY_FORM });
              setCreateOpen(true);
            }}
          >
            <UserPlus className="h-4 w-4" />
            Nouveau Membre
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={members as (Membre & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['nom', 'prenom', 'email', 'numero']}
            searchPlaceholder="Rechercher un membre..."
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'EN_ATTENTE' },
                  { label: 'Actif', value: 'ACTIF' },
                  { label: 'Suspendu', value: 'SUSPENDU' },
                  { label: 'Radié', value: 'RADIÉ' },
                ],
              },
              {
                key: 'segmentId',
                label: 'Segment',
                options: Object.entries(SEGMENTS).map(([value, label]) => ({
                  label,
                  value,
                })),
              },
            ]}
            actions={(item) => [
              { label: 'Voir', onClick: () => {} },
              { label: 'Modifier', onClick: () => {} },
              {
                label: 'KYC',
                onClick: () => {},
              },
              {
                label: 'Supprimer',
                onClick: () => {
                  setSelectedMember(item);
                  setDeleteOpen(true);
                },
                variant: 'destructive',
              },
            ]}
            pageSize={10}
            selectable
            onSelectionChange={(selected) => {
              console.log('Membres sélectionnés:', selected.length);
            }}
            exportable
            exportFilename="membres"
          />
        </CardContent>
      </Card>

      {/* ── Create Member Dialog ──────────────────────────────────────── */}
      <Dialog open={createOpen} onOpenChange={setCreateOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Nouveau Membre</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="prenom">Prénom</Label>
                <Input
                  id="prenom"
                  placeholder="Prénom"
                  value={formData.prenom}
                  onChange={(e) =>
                    setFormData({ ...formData, prenom: e.target.value })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="nom">Nom</Label>
                <Input
                  id="nom"
                  placeholder="Nom"
                  value={formData.nom}
                  onChange={(e) =>
                    setFormData({ ...formData, nom: e.target.value })
                  }
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="email@exemple.com"
                value={formData.email}
                onChange={(e) =>
                  setFormData({ ...formData, email: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="telephone">Téléphone</Label>
              <Input
                id="telephone"
                placeholder="+221 77 000 00 00"
                value={formData.telephone}
                onChange={(e) =>
                  setFormData({ ...formData, telephone: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="segment">Segment</Label>
              <Select
                value={formData.segmentId}
                onValueChange={(v) =>
                  setFormData({ ...formData, segmentId: v })
                }
              >
                <SelectTrigger id="segment">
                  <SelectValue placeholder="Sélectionner un segment" />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(SEGMENTS).map(([value, label]) => (
                    <SelectItem key={value} value={value}>
                      {label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCreateOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleCreate}>Créer le membre</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ── Delete Confirm Dialog ─────────────────────────────────────── */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer le membre"
        description={`Êtes-vous sûr de vouloir supprimer le membre ${selectedMember?.prenom} ${selectedMember?.nom} ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
