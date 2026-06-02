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
import { Plus } from 'lucide-react';
import type { Segment } from '@/types';

// ── Mock data: 14 segments ───────────────────────────────────────────────
const mockSegments: Segment[] = [
  {
    id: '1',
    nom: 'Premium',
    slug: 'premium',
    description: 'Membres premium avec avantages exclusifs',
    couleur: '#10b981',
    isDefault: false,
    actif: true,
    createdAt: '2024-01-01T00:00:00',
  },
  {
    id: '2',
    nom: 'Standard',
    slug: 'standard',
    description: 'Membres standards de la coopérative',
    couleur: '#f59e0b',
    isDefault: true,
    actif: true,
    createdAt: '2024-01-01T00:00:00',
  },
  {
    id: '3',
    nom: 'Basique',
    slug: 'basique',
    description: 'Membres basiques avec accès limité',
    couleur: '#6366f1',
    isDefault: false,
    actif: true,
    createdAt: '2024-01-15T00:00:00',
  },
  {
    id: '4',
    nom: 'Junior',
    slug: 'junior',
    description: 'Membres juniors (18-25 ans)',
    couleur: '#3b82f6',
    isDefault: false,
    actif: true,
    createdAt: '2024-02-01T00:00:00',
  },
  {
    id: '5',
    nom: 'Senior',
    slug: 'senior',
    description: 'Membres seniors (60 ans et plus)',
    couleur: '#ef4444',
    isDefault: false,
    actif: false,
    createdAt: '2024-03-01T00:00:00',
  },
  {
    id: '6',
    nom: 'Entrepreneur',
    slug: 'entrepreneur',
    description: 'Membres entrepreneurs et auto-entrepreneurs',
    couleur: '#8b5cf6',
    isDefault: false,
    actif: true,
    createdAt: '2024-02-15T00:00:00',
  },
  {
    id: '7',
    nom: 'Fonctionnaire',
    slug: 'fonctionnaire',
    description: 'Agents de la fonction publique',
    couleur: '#0ea5e9',
    isDefault: false,
    actif: true,
    createdAt: '2024-03-10T00:00:00',
  },
  {
    id: '8',
    nom: 'Étudiant',
    slug: 'etudiant',
    description: 'Étudiants universitaires et stagiaires',
    couleur: '#14b8a6',
    isDefault: false,
    actif: true,
    createdAt: '2024-04-01T00:00:00',
  },
  {
    id: '9',
    nom: 'Commerçant',
    slug: 'commercant',
    description: 'Commerçants et marchands',
    couleur: '#f97316',
    isDefault: false,
    actif: true,
    createdAt: '2024-04-15T00:00:00',
  },
  {
    id: '10',
    nom: 'Agricole',
    slug: 'agricole',
    description: 'Acteurs du secteur agricole et rural',
    couleur: '#84cc16',
    isDefault: false,
    actif: true,
    createdAt: '2024-05-01T00:00:00',
  },
  {
    id: '11',
    nom: 'VIP',
    slug: 'vip',
    description: 'Membres très importants avec privilèges spéciaux',
    couleur: '#eab308',
    isDefault: false,
    actif: true,
    createdAt: '2024-05-20T00:00:00',
  },
  {
    id: '12',
    nom: 'Diaspora',
    slug: 'diaspora',
    description: 'Membres de la diaspora africaine',
    couleur: '#ec4899',
    isDefault: false,
    actif: true,
    createdAt: '2024-06-01T00:00:00',
  },
  {
    id: '13',
    nom: 'Retraité',
    slug: 'retraite',
    description: 'Membres retraités avec revenus de pension',
    couleur: '#78716c',
    isDefault: false,
    actif: false,
    createdAt: '2024-06-15T00:00:00',
  },
  {
    id: '14',
    nom: 'Association',
    slug: 'association',
    description: 'Groupements et associations partenaires',
    couleur: '#a855f7',
    isDefault: false,
    actif: true,
    createdAt: '2024-07-01T00:00:00',
  },
];

// ── Columns ──────────────────────────────────────────────────────────────
const columns: Column<Segment & Record<string, unknown>>[] = [
  {
    key: 'nom',
    header: 'Nom',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{item.nom as string}</span>
    ),
  },
  {
    key: 'slug',
    header: 'Slug',
    sortable: true,
    render: (item) => (
      <span className="font-mono text-sm text-muted-foreground">
        {item.slug as string}
      </span>
    ),
  },
  {
    key: 'description',
    header: 'Description',
    render: (item) => (
      <span className="text-sm line-clamp-2 max-w-[200px]">
        {(item.description as string) || '—'}
      </span>
    ),
  },
  {
    key: 'couleur',
    header: 'Couleur',
    render: (item) => (
      <div className="flex items-center gap-2">
        <div
          className="h-4 w-4 rounded-full border"
          style={{ backgroundColor: item.couleur as string }}
        />
        <span className="text-sm font-mono">{item.couleur as string}</span>
      </div>
    ),
  },
  {
    key: 'isDefault',
    header: 'Par défaut',
    sortable: true,
    render: (item) => (
      <StatusBadge
        statut={item.isDefault ? 'ACTIF' : 'INACTIF'}
        label={item.isDefault ? 'Oui' : 'Non'}
      />
    ),
  },
  {
    key: 'actif',
    header: 'Actif',
    sortable: true,
    render: (item) => (
      <StatusBadge
        statut={item.actif ? 'ACTIF' : 'INACTIF'}
        label={item.actif ? 'Oui' : 'Non'}
      />
    ),
  },
];

// ── Form default ─────────────────────────────────────────────────────────
interface SegmentForm {
  nom: string;
  slug: string;
  description: string;
  couleur: string;
  isDefault: boolean;
  actif: boolean;
}

const EMPTY_FORM: SegmentForm = {
  nom: '',
  slug: '',
  description: '',
  couleur: '#10b981',
  isDefault: false,
  actif: true,
};

// ── Page Component ───────────────────────────────────────────────────────
export default function SegmentsPage() {
  const [segments, setSegments] = useState(mockSegments);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editingSegment, setEditingSegment] = useState<
    (Segment & Record<string, unknown>) | null
  >(null);
  const [selectedSegment, setSelectedSegment] = useState<
    (Segment & Record<string, unknown>) | null
  >(null);
  const [formData, setFormData] = useState<SegmentForm>({ ...EMPTY_FORM });

  // ── Helpers ───────────────────────────────────────────────────────────
  const slugify = (text: string) =>
    text
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '');

  // ── Handlers ──────────────────────────────────────────────────────────
  const openCreate = () => {
    setEditingSegment(null);
    setFormData({ ...EMPTY_FORM });
    setDialogOpen(true);
  };

  const openEdit = (segment: Segment & Record<string, unknown>) => {
    setEditingSegment(segment);
    setFormData({
      nom: segment.nom,
      slug: segment.slug,
      description: (segment.description as string) || '',
      couleur: (segment.couleur as string) || '#10b981',
      isDefault: segment.isDefault as boolean,
      actif: segment.actif as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editingSegment) {
      setSegments((prev) =>
        prev.map((s) =>
          s.id === editingSegment.id
            ? { ...s, ...formData, slug: formData.slug || slugify(formData.nom) }
            : s
        )
      );
    } else {
      const newSegment: Segment = {
        id: String(Date.now()),
        ...formData,
        slug: formData.slug || slugify(formData.nom),
        createdAt: new Date().toISOString(),
      };
      setSegments((prev) => [...prev, newSegment]);
    }
    setDialogOpen(false);
  };

  const handleDelete = () => {
    if (selectedSegment) {
      setSegments((prev) => prev.filter((s) => s.id !== selectedSegment.id));
      setDeleteOpen(false);
      setSelectedSegment(null);
    }
  };

  // ── Render ────────────────────────────────────────────────────────────
  return (
    <div className="space-y-6">
      <PageHeader
        title="Segments"
        description="Gestion des segments de membres"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouveau Segment
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={segments as (Segment & Record<string, unknown>)[]}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['nom', 'slug']}
            searchPlaceholder="Rechercher un segment..."
            pageSize={10}
            exportable
            exportFilename="segments"
            actions={(item) => [
              {
                label: 'Modifier',
                onClick: () => openEdit(item),
              },
              {
                label: 'Supprimer',
                onClick: () => {
                  setSelectedSegment(item);
                  setDeleteOpen(true);
                },
                variant: 'destructive',
              },
            ]}
          />
        </CardContent>
      </Card>

      {/* ── Create / Edit Dialog ──────────────────────────────────────── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {editingSegment ? 'Modifier le segment' : 'Nouveau segment'}
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="seg-nom">Nom</Label>
              <Input
                id="seg-nom"
                placeholder="Nom du segment"
                value={formData.nom}
                onChange={(e) => {
                  const nom = e.target.value;
                  setFormData({
                    ...formData,
                    nom,
                    slug: slugify(nom),
                  });
                }}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="seg-slug">Slug</Label>
              <Input
                id="seg-slug"
                placeholder="slug-du-segment"
                value={formData.slug}
                onChange={(e) =>
                  setFormData({ ...formData, slug: e.target.value })
                }
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="seg-desc">Description</Label>
              <Textarea
                id="seg-desc"
                placeholder="Description du segment..."
                value={formData.description}
                onChange={(e) =>
                  setFormData({ ...formData, description: e.target.value })
                }
                rows={3}
              />
            </div>
            <div className="space-y-2">
              <Label>Couleur</Label>
              <div className="flex items-center gap-3">
                <input
                  type="color"
                  value={formData.couleur}
                  onChange={(e) =>
                    setFormData({ ...formData, couleur: e.target.value })
                  }
                  className="h-10 w-14 cursor-pointer rounded border"
                />
                <Input
                  value={formData.couleur}
                  onChange={(e) =>
                    setFormData({ ...formData, couleur: e.target.value })
                  }
                  className="flex-1 font-mono"
                />
              </div>
            </div>
            <div className="flex items-center justify-between rounded-lg border p-3">
              <div className="space-y-0.5">
                <Label>Segment par défaut</Label>
                <p className="text-xs text-muted-foreground">
                  Les nouveaux membres seront affectés à ce segment
                </p>
              </div>
              <Switch
                checked={formData.isDefault}
                onCheckedChange={(v) =>
                  setFormData({ ...formData, isDefault: v })
                }
              />
            </div>
            <div className="flex items-center justify-between rounded-lg border p-3">
              <div className="space-y-0.5">
                <Label>Actif</Label>
                <p className="text-xs text-muted-foreground">
                  Segment visible et utilisable
                </p>
              </div>
              <Switch
                checked={formData.actif}
                onCheckedChange={(v) =>
                  setFormData({ ...formData, actif: v })
                }
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleSave}>
              {editingSegment ? 'Modifier' : 'Créer'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ── Delete Confirm Dialog ─────────────────────────────────────── */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer le segment"
        description={`Êtes-vous sûr de vouloir supprimer le segment "${selectedSegment?.nom}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
