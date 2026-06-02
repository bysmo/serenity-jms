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
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Card, CardContent } from '@/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Plus, Pencil, Trash2, PowerOff } from 'lucide-react';
import type { Cotisation, CotisationType, Frequence, TypeMontant, Visibilite } from '@/types';
import { COTISATION_TYPE_LABELS, FREQUENCE_LABELS } from '@/lib/constants';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const formatMoney = (amount: number): string =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

const VISIBILITE_LABELS: Record<string, string> = {
  PUBLIQUE: 'Publique',
  PRIVEE: 'Privée',
  GROUPE: 'Groupe',
};

const TYPE_MONTANT_LABELS: Record<string, string> = {
  FIXE: 'Fixe',
  LIBRE: 'Libre',
  MINIMUM: 'Minimum',
};

// ---------------------------------------------------------------------------
// Mock Data
// ---------------------------------------------------------------------------

const initialCotisations: Cotisation[] = [
  {
    id: '1',
    libelle: 'Cotisation Mensuelle Ordinaire',
    description: 'Cotisation mensuelle obligatoire pour tous les membres',
    type: 'ORDINAIRE',
    frequence: 'MENSUELLE',
    typeMontant: 'FIXE',
    montant: 5000,
    visibilite: 'PUBLIQUE',
    actif: true,
    dateDebut: '2024-01-01',
    dateFin: '2024-12-31',
    createdAt: '2024-01-01',
  },
  {
    id: '2',
    libelle: 'Fonds de Solidarité',
    description: 'Contribution volontaire au fonds de solidarité',
    type: 'SOCIALE',
    frequence: 'MENSUELLE',
    typeMontant: 'LIBRE',
    montant: 2000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'solidarité',
    createdAt: '2024-01-15',
  },
  {
    id: '3',
    libelle: 'Cotisation Annuelle Extraordinaire',
    description: 'Cotisation exceptionnelle pour projets spéciaux',
    type: 'EXTRAORDINAIRE',
    frequence: 'ANNUELLE',
    typeMontant: 'FIXE',
    montant: 50000,
    visibilite: 'PUBLIQUE',
    actif: true,
    dateDebut: '2024-06-01',
    dateFin: '2024-06-30',
    createdAt: '2024-02-01',
  },
  {
    id: '4',
    libelle: 'Épargne Prévoyance',
    description: "Plan d'épargne prévoyance pour les membres",
    type: 'SPECIALE',
    frequence: 'MENSUELLE',
    typeMontant: 'MINIMUM',
    montant: 10000,
    visibilite: 'PRIVEE',
    actif: false,
    tag: 'épargne',
    createdAt: '2024-03-01',
  },
  {
    id: '5',
    libelle: 'Cotisation Hebdomadaire Marchés',
    description: 'Cotisation hebdomadaire pour groupes locaux',
    type: 'ORDINAIRE',
    frequence: 'HEBDOMADAIRE',
    typeMontant: 'FIXE',
    montant: 1500,
    visibilite: 'GROUPE',
    actif: true,
    tag: 'marché',
    createdAt: '2024-04-01',
  },
  {
    id: '6',
    libelle: "Fonds d'Assurance Collective",
    description: 'Assurance collective pour les membres actifs',
    type: 'SOCIALE',
    frequence: 'TRIMESTRIELLE',
    typeMontant: 'FIXE',
    montant: 15000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'assurance',
    createdAt: '2024-04-15',
  },
  {
    id: '7',
    libelle: 'Contribution Spéciale Formation',
    description: 'Contribution pour le programme de formation',
    type: 'SPECIALE',
    frequence: 'UNIQUE',
    typeMontant: 'FIXE',
    montant: 25000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'formation',
    createdAt: '2024-05-01',
  },
  {
    id: '8',
    libelle: 'Cotisation Journalière Marchands',
    description: 'Cotisation quotidienne pour les marchands ambulants',
    type: 'ORDINAIRE',
    frequence: 'JOURNALIERE',
    typeMontant: 'LIBRE',
    montant: 500,
    visibilite: 'GROUPE',
    actif: false,
    tag: 'marché',
    createdAt: '2024-05-15',
  },
  {
    id: '9',
    libelle: 'Fonds de Scolarité',
    description: 'Aide à la scolarisation des enfants des membres',
    type: 'SOCIALE',
    frequence: 'ANNUELLE',
    typeMontant: 'FIXE',
    montant: 30000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'éducation',
    createdAt: '2024-01-20',
  },
  {
    id: '10',
    libelle: 'Cotisation Trimestrielle Développement',
    description: 'Fonds de développement de la coopérative',
    type: 'EXTRAORDINAIRE',
    frequence: 'TRIMESTRIELLE',
    typeMontant: 'FIXE',
    montant: 20000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'développement',
    createdAt: '2024-02-10',
  },
  {
    id: '11',
    libelle: 'Épargne Logement',
    description: "Programme d'épargne pour l'accès au logement",
    type: 'SPECIALE',
    frequence: 'MENSUELLE',
    typeMontant: 'MINIMUM',
    montant: 20000,
    visibilite: 'PRIVEE',
    actif: true,
    tag: 'logement',
    createdAt: '2024-03-05',
  },
  {
    id: '12',
    libelle: 'Cotisation Santé Communautaire',
    description: 'Mutuelle de santé communautaire',
    type: 'SOCIALE',
    frequence: 'MENSUELLE',
    typeMontant: 'FIXE',
    montant: 3000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'santé',
    createdAt: '2024-03-20',
  },
  {
    id: '13',
    libelle: 'Contribution Infrastructures',
    description: 'Fonds pour les projets infrastructurels',
    type: 'EXTRAORDINAIRE',
    frequence: 'ANNUELLE',
    typeMontant: 'LIBRE',
    montant: 75000,
    visibilite: 'PUBLIQUE',
    actif: false,
    tag: 'infrastructure',
    createdAt: '2024-04-08',
  },
  {
    id: '14',
    libelle: 'Cotisation Hebdomadaire Pêcheurs',
    description: 'Cotisation dédiée aux groupements de pêcheurs',
    type: 'ORDINAIRE',
    frequence: 'HEBDOMADAIRE',
    typeMontant: 'FIXE',
    montant: 2000,
    visibilite: 'GROUPE',
    actif: true,
    tag: 'pêche',
    createdAt: '2024-04-22',
  },
  {
    id: '15',
    libelle: 'Fonds d\'Urgence Humanitaire',
    description: 'Réserve financière pour les situations d\'urgence',
    type: 'SOCIALE',
    frequence: 'TRIMESTRIELLE',
    typeMontant: 'LIBRE',
    montant: 10000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'urgence',
    createdAt: '2024-05-05',
  },
  {
    id: '16',
    libelle: 'Cotisation Mensuelle Agriculture',
    description: 'Appui aux activités agricoles des membres',
    type: 'ORDINAIRE',
    frequence: 'MENSUELLE',
    typeMontant: 'MINIMUM',
    montant: 4000,
    visibilite: 'GROUPE',
    actif: true,
    tag: 'agriculture',
    createdAt: '2024-05-18',
  },
  {
    id: '17',
    libelle: 'Programme Micro-Projets',
    description: 'Financement de micro-projets générateurs de revenus',
    type: 'SPECIALE',
    frequence: 'TRIMESTRIELLE',
    typeMontant: 'FIXE',
    montant: 35000,
    visibilite: 'PRIVEE',
    actif: true,
    tag: 'entrepreneuriat',
    createdAt: '2024-06-01',
  },
  {
    id: '18',
    libelle: 'Cotisation Annuelle Retraite',
    description: 'Préparation de la retraite pour les membres seniors',
    type: 'ORDINAIRE',
    frequence: 'ANNUELLE',
    typeMontant: 'MINIMUM',
    montant: 100000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'retraite',
    createdAt: '2024-06-10',
  },
  {
    id: '19',
    libelle: 'Fonds Funérailles',
    description: 'Contribution pour l\'assistance funéraire',
    type: 'SOCIALE',
    frequence: 'MENSUELLE',
    typeMontant: 'FIXE',
    montant: 1500,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'funérailles',
    createdAt: '2024-06-20',
  },
  {
    id: '20',
    libelle: 'Cotisation Unique Adhésion',
    description: 'Frais uniques d\'adhésion à la coopérative',
    type: 'ORDINAIRE',
    frequence: 'UNIQUE',
    typeMontant: 'FIXE',
    montant: 10000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'adhésion',
    createdAt: '2024-07-01',
  },
  {
    id: '21',
    libelle: 'Épargne Femme Entrepreneur',
    description: 'Programme d\'épargne dédié aux femmes entrepreneurs',
    type: 'SPECIALE',
    frequence: 'HEBDOMADAIRE',
    typeMontant: 'LIBRE',
    montant: 2500,
    visibilite: 'GROUPE',
    actif: true,
    tag: 'genre',
    createdAt: '2024-07-10',
  },
  {
    id: '22',
    libelle: 'Cotisation Trimestrielle Jeunes',
    description: 'Cotisation encourageant la participation des jeunes',
    type: 'ORDINAIRE',
    frequence: 'TRIMESTRIELLE',
    typeMontant: 'MINIMUM',
    montant: 5000,
    visibilite: 'PUBLIQUE',
    actif: false,
    tag: 'jeunesse',
    createdAt: '2024-07-15',
  },
  {
    id: '23',
    libelle: 'Fonds Numérique & Innovation',
    description: 'Fonds pour la digitalisation et l\'innovation',
    type: 'EXTRAORDINAIRE',
    frequence: 'ANNUELLE',
    typeMontant: 'LIBRE',
    montant: 40000,
    visibilite: 'PRIVEE',
    actif: true,
    tag: 'numérique',
    createdAt: '2024-08-01',
  },
  {
    id: '24',
    libelle: 'Cotisation Mensuelle Transport',
    description: 'Aide aux transports pour les membres éloignés',
    type: 'SOCIALE',
    frequence: 'MENSUELLE',
    typeMontant: 'FIXE',
    montant: 2500,
    visibilite: 'GROUPE',
    actif: true,
    tag: 'transport',
    createdAt: '2024-08-10',
  },
  {
    id: '25',
    libelle: 'Contribution Environnement',
    description: 'Fonds pour les projets environnementaux durables',
    type: 'SPECIALE',
    frequence: 'UNIQUE',
    typeMontant: 'LIBRE',
    montant: 15000,
    visibilite: 'PUBLIQUE',
    actif: true,
    tag: 'environnement',
    createdAt: '2024-08-20',
  },
  {
    id: '26',
    libelle: 'Cotisation Journalière Artisans',
    description: 'Cotisation quotidienne pour les artisans du secteur informel',
    type: 'ORDINAIRE',
    frequence: 'JOURNALIERE',
    typeMontant: 'FIXE',
    montant: 300,
    visibilite: 'GROUPE',
    actif: false,
    tag: 'artisanat',
    createdAt: '2024-09-01',
  },
];

// ---------------------------------------------------------------------------
// Columns
// ---------------------------------------------------------------------------

const columns: Column<Cotisation & Record<string, unknown>>[] = [
  {
    key: 'libelle',
    header: 'Libellé',
    sortable: true,
    render: (item) => (
      <div>
        <span className="font-medium">{item.libelle}</span>
        {item.tag && (
          <span className="ml-2 text-xs text-muted-foreground">#{String(item.tag)}</span>
        )}
      </div>
    ),
  },
  {
    key: 'type',
    header: 'Type',
    sortable: true,
    render: (item) => (
      <span className="text-sm">
        {COTISATION_TYPE_LABELS[item.type as string] || String(item.type)}
      </span>
    ),
  },
  {
    key: 'frequence',
    header: 'Fréquence',
    sortable: true,
    render: (item) => (
      <span className="text-sm">
        {FREQUENCE_LABELS[item.frequence as string] || String(item.frequence)}
      </span>
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
    key: 'visibilite',
    header: 'Visibilité',
    render: (item) => (
      <span className="text-sm">
        {VISIBILITE_LABELS[item.visibilite as string] || String(item.visibilite)}
      </span>
    ),
  },
  {
    key: 'actif',
    header: 'Actif',
    sortable: true,
    render: (item) => (
      <StatusBadge statut={item.actif ? 'ACTIF' : 'INACTIF'} label={item.actif ? 'Oui' : 'Non'} />
    ),
  },
];

// ---------------------------------------------------------------------------
// Form default
// ---------------------------------------------------------------------------

interface CotisationFormData {
  libelle: string;
  description: string;
  type: CotisationType;
  frequence: Frequence;
  typeMontant: TypeMontant;
  montant: number;
  visibilite: Visibilite;
  tag: string;
  actif: boolean;
  dateDebut: string;
  dateFin: string;
}

const defaultFormData: CotisationFormData = {
  libelle: '',
  description: '',
  type: 'ORDINAIRE',
  frequence: 'MENSUELLE',
  typeMontant: 'FIXE',
  montant: 0,
  visibilite: 'PUBLIQUE',
  tag: '',
  actif: true,
  dateDebut: '',
  dateFin: '',
};

// ---------------------------------------------------------------------------
// Page Component
// ---------------------------------------------------------------------------

export default function CotisationsPage() {
  const [cotisations, setCotisations] = useState(initialCotisations);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [deactivateOpen, setDeactivateOpen] = useState(false);
  const [editingCotisation, setEditingCotisation] = useState<(Cotisation & Record<string, unknown>) | null>(null);
  const [selectedCotisation, setSelectedCotisation] = useState<(Cotisation & Record<string, unknown>) | null>(null);
  const [formData, setFormData] = useState<CotisationFormData>({ ...defaultFormData });

  // ---- handlers ----

  const openCreate = () => {
    setEditingCotisation(null);
    setFormData({ ...defaultFormData });
    setDialogOpen(true);
  };

  const openEdit = (item: Cotisation & Record<string, unknown>) => {
    setEditingCotisation(item);
    setFormData({
      libelle: String(item.libelle ?? ''),
      description: String(item.description ?? ''),
      type: item.type as CotisationType,
      frequence: item.frequence as Frequence,
      typeMontant: item.typeMontant as TypeMontant,
      montant: item.montant as number,
      visibilite: item.visibilite as Visibilite,
      tag: String(item.tag ?? ''),
      actif: item.actif as boolean,
      dateDebut: String(item.dateDebut ?? ''),
      dateFin: String(item.dateFin ?? ''),
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editingCotisation) {
      setCotisations((prev) =>
        prev.map((c) =>
          c.id === editingCotisation.id
            ? { ...c, ...formData, montant: Number(formData.montant) }
            : c,
        ),
      );
    } else {
      const newCotisation: Cotisation = {
        id: String(Date.now()),
        ...formData,
        montant: Number(formData.montant),
        createdAt: new Date().toISOString(),
      };
      setCotisations((prev) => [...prev, newCotisation]);
    }
    setDialogOpen(false);
  };

  const handleDelete = () => {
    if (selectedCotisation) {
      setCotisations((prev) => prev.filter((c) => c.id !== selectedCotisation.id));
      setDeleteOpen(false);
      setSelectedCotisation(null);
    }
  };

  const handleDeactivate = () => {
    if (selectedCotisation) {
      setCotisations((prev) =>
        prev.map((c) =>
          c.id === selectedCotisation.id ? { ...c, actif: !c.actif } : c,
        ),
      );
      setDeactivateOpen(false);
      setSelectedCotisation(null);
    }
  };

  // ---- render ----

  return (
    <div className="space-y-6">
      <PageHeader
        title="Gestion des Cotisations"
        description="Créez et gérez les cotisations de la coopérative"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouvelle Cotisation
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={cotisations as (Cotisation & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['libelle', 'description']}
            searchPlaceholder="Rechercher une cotisation..."
            pageSize={10}
            exportable
            exportFilename="cotisations"
            filters={[
              {
                key: 'type',
                label: 'Type',
                options: Object.entries(COTISATION_TYPE_LABELS)
                  .filter(([k]) => ['ORDINAIRE', 'EXTRAORDINAIRE', 'SOCIALE', 'SPECIALE'].includes(k))
                  .map(([value, label]) => ({ label, value })),
              },
              {
                key: 'frequence',
                label: 'Fréquence',
                options: Object.entries(FREQUENCE_LABELS).map(([value, label]) => ({
                  label,
                  value,
                })),
              },
              {
                key: 'actif',
                label: 'Actif',
                options: [
                  { label: 'Oui', value: 'true' },
                  { label: 'Non', value: 'false' },
                ],
              },
            ]}
            actions={(item) => [
              {
                label: 'Modifier',
                onClick: () => openEdit(item),
              },
              {
                label: item.actif ? 'Désactiver' : 'Activer',
                onClick: () => {
                  setSelectedCotisation(item);
                  setDeactivateOpen(true);
                },
              },
              {
                label: 'Supprimer',
                onClick: () => {
                  setSelectedCotisation(item);
                  setDeleteOpen(true);
                },
                variant: 'destructive' as const,
              },
            ]}
          />
        </CardContent>
      </Card>

      {/* Create / Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-lg max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {editingCotisation ? 'Modifier la cotisation' : 'Nouvelle cotisation'}
            </DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            {/* Libellé */}
            <div className="space-y-2">
              <Label htmlFor="libelle">Libellé</Label>
              <Input
                id="libelle"
                value={formData.libelle}
                onChange={(e) => setFormData({ ...formData, libelle: e.target.value })}
                placeholder="Nom de la cotisation"
              />
            </div>

            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Description détaillée..."
                rows={3}
              />
            </div>

            {/* Type + Fréquence */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Type</Label>
                <Select
                  value={formData.type}
                  onValueChange={(v) => setFormData({ ...formData, type: v as CotisationType })}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ORDINAIRE">Ordinaire</SelectItem>
                    <SelectItem value="EXTRAORDINAIRE">Extraordinaire</SelectItem>
                    <SelectItem value="SOCIALE">Sociale</SelectItem>
                    <SelectItem value="SPECIALE">Spéciale</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Fréquence</Label>
                <Select
                  value={formData.frequence}
                  onValueChange={(v) => setFormData({ ...formData, frequence: v as Frequence })}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="JOURNALIERE">Journalière</SelectItem>
                    <SelectItem value="HEBDOMADAIRE">Hebdomadaire</SelectItem>
                    <SelectItem value="MENSUELLE">Mensuelle</SelectItem>
                    <SelectItem value="TRIMESTRIELLE">Trimestrielle</SelectItem>
                    <SelectItem value="ANNUELLE">Annuelle</SelectItem>
                    <SelectItem value="UNIQUE">Unique</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Type Montant + Montant */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Type de montant</Label>
                <Select
                  value={formData.typeMontant}
                  onValueChange={(v) => setFormData({ ...formData, typeMontant: v as TypeMontant })}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="FIXE">Fixe</SelectItem>
                    <SelectItem value="LIBRE">Libre</SelectItem>
                    <SelectItem value="MINIMUM">Minimum</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="montant">Montant (FCFA)</Label>
                <Input
                  id="montant"
                  type="number"
                  min={0}
                  value={formData.montant || ''}
                  onChange={(e) => setFormData({ ...formData, montant: Number(e.target.value) })}
                  placeholder="0"
                />
              </div>
            </div>

            {/* Visibilité + Tag */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Visibilité</Label>
                <Select
                  value={formData.visibilite}
                  onValueChange={(v) => setFormData({ ...formData, visibilite: v as Visibilite })}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="PUBLIQUE">Publique</SelectItem>
                    <SelectItem value="PRIVEE">Privée</SelectItem>
                    <SelectItem value="GROUPE">Groupe</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="tag">Tag</Label>
                <Input
                  id="tag"
                  value={formData.tag}
                  onChange={(e) => setFormData({ ...formData, tag: e.target.value })}
                  placeholder="Étiquette optionnelle"
                />
              </div>
            </div>

            {/* Dates */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="dateDebut">Date début</Label>
                <Input
                  id="dateDebut"
                  type="date"
                  value={formData.dateDebut}
                  onChange={(e) => setFormData({ ...formData, dateDebut: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="dateFin">Date fin</Label>
                <Input
                  id="dateFin"
                  type="date"
                  value={formData.dateFin}
                  onChange={(e) => setFormData({ ...formData, dateFin: e.target.value })}
                />
              </div>
            </div>

            {/* Actif toggle */}
            <div className="flex items-center justify-between rounded-lg border p-3">
              <div>
                <Label className="text-sm font-medium">Cotisation active</Label>
                <p className="text-xs text-muted-foreground">
                  Les membres peuvent adhérer si la cotisation est active
                </p>
              </div>
              <Switch
                checked={formData.actif}
                onCheckedChange={(v) => setFormData({ ...formData, actif: v })}
              />
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleSave} disabled={!formData.libelle.trim()}>
              {editingCotisation ? 'Modifier' : 'Créer'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer la cotisation"
        description={`Êtes-vous sûr de vouloir supprimer la cotisation "${selectedCotisation?.libelle}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />

      {/* Deactivate Confirmation */}
      <ConfirmDialog
        open={deactivateOpen}
        onOpenChange={setDeactivateOpen}
        title={selectedCotisation?.actif ? 'Désactiver la cotisation' : 'Activer la cotisation'}
        description={
          selectedCotisation?.actif
            ? `Voulez-vous désactiver la cotisation "${selectedCotisation?.libelle}" ? Les membres ne pourront plus y adhérer.`
            : `Voulez-vous activer la cotisation "${selectedCotisation?.libelle}" ? Les membres pourront à nouveau y adhérer.`
        }
        confirmLabel={selectedCotisation?.actif ? 'Désactiver' : 'Activer'}
        variant="default"
        onConfirm={handleDeactivate}
      />
    </div>
  );
}
