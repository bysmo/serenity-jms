'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Card, CardContent } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Plus } from 'lucide-react';
import type { EpargnePlan, EpargnePlanRequest } from '@/types';
import { formatCurrency, FREQUENCE_LABELS } from '@/lib/constants';

// TODO: Replace with API call
const initialPlans: EpargnePlan[] = [
  {
    id: '1',
    nom: 'Épargne Court Terme',
    description: "Plan d'épargne sur 6 mois pour les nouveaux membres",
    montantMin: 5000,
    montantMax: 500000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 3.5,
    dureeMois: 6,
    actif: true,
    createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: '2',
    nom: 'Épargne Moyen Terme',
    description: "Plan d'épargne sur 12 mois avec rendement attractif",
    montantMin: 10000,
    montantMax: 2000000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 5.0,
    dureeMois: 12,
    actif: true,
    createdAt: '2024-02-01T10:00:00Z',
  },
  {
    id: '3',
    nom: 'Épargne Long Terme',
    description: "Plan d'épargne sur 24 mois pour les projets ambitieux",
    montantMin: 20000,
    montantMax: 5000000,
    frequence: 'TRIMESTRIELLE',
    tauxRemuneration: 7.5,
    dureeMois: 24,
    actif: true,
    createdAt: '2024-03-10T10:00:00Z',
  },
  {
    id: '4',
    nom: 'Épargne Quotidienne',
    description: 'Épargne avec fréquence journalière pour les petits montants',
    montantMin: 500,
    montantMax: 100000,
    frequence: 'JOURNALIERE',
    tauxRemuneration: 2.0,
    dureeMois: 3,
    actif: false,
    createdAt: '2024-04-05T10:00:00Z',
  },
  {
    id: '5',
    nom: 'Épargne Hebdomadaire',
    description: 'Épargne hebdomadaire pour une épargne régulière',
    montantMin: 2000,
    montantMax: 300000,
    frequence: 'HEBDOMADAIRE',
    tauxRemuneration: 4.0,
    dureeMois: 9,
    actif: true,
    createdAt: '2024-05-20T10:00:00Z',
  },
  {
    id: '6',
    nom: 'Épargne Éducation',
    description: "Plan d'épargne dédié aux frais scolaires et universitaires",
    montantMin: 10000,
    montantMax: 1500000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 4.5,
    dureeMois: 10,
    actif: true,
    createdAt: '2024-06-01T10:00:00Z',
  },
  {
    id: '7',
    nom: 'Épargne Santé',
    description: 'Épargne pour couvrir les dépenses de santé imprévues',
    montantMin: 5000,
    montantMax: 1000000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 3.0,
    dureeMois: 12,
    actif: true,
    createdAt: '2024-06-15T10:00:00Z',
  },
  {
    id: '8',
    nom: 'Épargne Mariage',
    description: "Plan d'épargne pour financer les cérémonies de mariage",
    montantMin: 15000,
    montantMax: 3000000,
    frequence: 'TRIMESTRIELLE',
    tauxRemuneration: 6.0,
    dureeMois: 18,
    actif: true,
    createdAt: '2024-07-01T10:00:00Z',
  },
  {
    id: '9',
    nom: 'Épargne Immobilier',
    description: 'Épargne longue durée pour un projet immobilier',
    montantMin: 50000,
    montantMax: 10000000,
    frequence: 'TRIMESTRIELLE',
    tauxRemuneration: 8.0,
    dureeMois: 36,
    actif: true,
    createdAt: '2024-07-20T10:00:00Z',
  },
  {
    id: '10',
    nom: 'Épargne Urgence',
    description: "Fonds d'urgence accessible rapidement en cas de besoin",
    montantMin: 1000,
    montantMax: 200000,
    frequence: 'JOURNALIERE',
    tauxRemuneration: 1.5,
    dureeMois: 3,
    actif: true,
    createdAt: '2024-08-05T10:00:00Z',
  },
  {
    id: '11',
    nom: 'Épargne Retraite',
    description: "Plan d'épargne retraite pour sécuriser l'avenir",
    montantMin: 10000,
    montantMax: 8000000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 6.5,
    dureeMois: 60,
    actif: true,
    createdAt: '2024-08-15T10:00:00Z',
  },
  {
    id: '12',
    nom: 'Épargne Commerce',
    description: 'Épargne pour les commerçants souhaitant développer leur activité',
    montantMin: 20000,
    montantMax: 4000000,
    frequence: 'HEBDOMADAIRE',
    tauxRemuneration: 5.5,
    dureeMois: 12,
    actif: false,
    createdAt: '2024-09-01T10:00:00Z',
  },
  {
    id: '13',
    nom: 'Épargne Pèlerinage',
    description: "Plan d'épargne pour financer le pèlerinage",
    montantMin: 25000,
    montantMax: 5000000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 4.0,
    dureeMois: 24,
    actif: true,
    createdAt: '2024-09-15T10:00:00Z',
  },
  {
    id: '14',
    nom: 'Épargne Agricole',
    description: "Épargne adaptée au cycle agricole avec paiements saisonniers",
    montantMin: 5000,
    montantMax: 2000000,
    frequence: 'TRIMESTRIELLE',
    tauxRemuneration: 5.5,
    dureeMois: 12,
    actif: true,
    createdAt: '2024-10-01T10:00:00Z',
  },
  {
    id: '15',
    nom: 'Épargne Jeunesse',
    description: "Plan d'épargne pour les jeunes de 18 à 30 ans",
    montantMin: 1000,
    montantMax: 500000,
    frequence: 'HEBDOMADAIRE',
    tauxRemuneration: 4.5,
    dureeMois: 6,
    actif: true,
    createdAt: '2024-10-20T10:00:00Z',
  },
  {
    id: '16',
    nom: 'Épargne Solidaire',
    description: 'Épargne solidaire avec partage des intérêts pour la communauté',
    montantMin: 5000,
    montantMax: 1000000,
    frequence: 'MENSUELLE',
    tauxRemuneration: 3.0,
    dureeMois: 12,
    actif: false,
    createdAt: '2024-11-01T10:00:00Z',
  },
];

type PlanRow = EpargnePlan & Record<string, unknown>;

const columns: Column<PlanRow>[] = [
  { key: 'nom', header: 'Nom', sortable: true },
  {
    key: 'montantMin',
    header: 'Montant Min',
    sortable: true,
    render: (item) => <span>{formatCurrency(item.montantMin as number)}</span>,
  },
  {
    key: 'montantMax',
    header: 'Montant Max',
    sortable: true,
    render: (item) => <span>{formatCurrency(item.montantMax as number)}</span>,
  },
  {
    key: 'frequence',
    header: 'Fréquence',
    sortable: true,
    render: (item) => (
      <span>{FREQUENCE_LABELS[item.frequence as string] || (item.frequence as string)}</span>
    ),
  },
  {
    key: 'tauxRemuneration',
    header: 'Taux Rémunération (%)',
    sortable: true,
    render: (item) => <span className="font-medium">{item.tauxRemuneration as number}%</span>,
  },
  {
    key: 'dureeMois',
    header: 'Durée (mois)',
    sortable: true,
    render: (item) => <span>{item.dureeMois as number} mois</span>,
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

const emptyForm: EpargnePlanRequest = {
  nom: '',
  description: '',
  montantMin: 0,
  montantMax: 0,
  frequence: 'MENSUELLE',
  tauxRemuneration: 0,
  dureeMois: 6,
  actif: true,
};

export default function EpargnePage() {
  const [plans, setPlans] = useState<EpargnePlan[]>(initialPlans);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editingPlan, setEditingPlan] = useState<PlanRow | null>(null);
  const [selectedPlan, setSelectedPlan] = useState<PlanRow | null>(null);
  const [formData, setFormData] = useState<EpargnePlanRequest>({ ...emptyForm });

  const openCreate = () => {
    setEditingPlan(null);
    setFormData({ ...emptyForm });
    setDialogOpen(true);
  };

  const openEdit = (plan: PlanRow) => {
    setEditingPlan(plan);
    setFormData({
      nom: plan.nom,
      description: plan.description || '',
      montantMin: plan.montantMin as number,
      montantMax: plan.montantMax as number,
      frequence: plan.frequence as EpargnePlanRequest['frequence'],
      tauxRemuneration: plan.tauxRemuneration as number,
      dureeMois: plan.dureeMois as number,
      actif: plan.actif as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    // TODO: Replace with API call
    if (editingPlan) {
      setPlans(
        plans.map((p) =>
          p.id === editingPlan.id ? { ...p, ...formData, updatedAt: new Date().toISOString() } : p
        )
      );
    } else {
      const newPlan: EpargnePlan = {
        id: String(Date.now()),
        ...formData,
        createdAt: new Date().toISOString(),
      };
      setPlans([...plans, newPlan]);
    }
    setDialogOpen(false);
  };

  const handleDelete = () => {
    if (selectedPlan) {
      setPlans(plans.filter((p) => p.id !== selectedPlan.id));
      setDeleteOpen(false);
      setSelectedPlan(null);
    }
  };

  const handleToggleActive = (plan: PlanRow) => {
    // TODO: Replace with API call
    setPlans(plans.map((p) => (p.id === plan.id ? { ...p, actif: !p.actif } : p)));
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Plans d'Épargne"
        description="Gestion des plans d'épargne de la coopérative"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouveau Plan
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={plans as PlanRow[]}
            columns={columns}
            keyExtractor={(item) => item.id as string}
            searchKeys={['nom']}
            searchPlaceholder="Rechercher un plan..."
            pageSize={10}
            exportable={true}
            exportFilename="epargne-plans"
            actions={(item) => [
              { label: 'Modifier', onClick: () => openEdit(item) },
              {
                label: item.actif ? 'Désactiver' : 'Activer',
                onClick: () => handleToggleActive(item),
              },
              {
                label: 'Supprimer',
                onClick: () => {
                  setSelectedPlan(item);
                  setDeleteOpen(true);
                },
                variant: 'destructive',
              },
            ]}
          />
        </CardContent>
      </Card>

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>{editingPlan ? 'Modifier le plan' : "Nouveau plan d'épargne"}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="plan-nom">Nom</Label>
              <Input
                id="plan-nom"
                value={formData.nom}
                onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                placeholder="Nom du plan"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="plan-description">Description</Label>
              <Textarea
                id="plan-description"
                value={formData.description || ''}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Description du plan"
                rows={3}
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="plan-montantMin">Montant Min (FCFA)</Label>
                <Input
                  id="plan-montantMin"
                  type="number"
                  value={formData.montantMin}
                  onChange={(e) => setFormData({ ...formData, montantMin: Number(e.target.value) })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="plan-montantMax">Montant Max (FCFA)</Label>
                <Input
                  id="plan-montantMax"
                  type="number"
                  value={formData.montantMax}
                  onChange={(e) => setFormData({ ...formData, montantMax: Number(e.target.value) })}
                />
              </div>
            </div>
            <div className="grid grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label htmlFor="plan-frequence">Fréquence</Label>
                <Select
                  value={formData.frequence}
                  onValueChange={(v) =>
                    setFormData({ ...formData, frequence: v as EpargnePlanRequest['frequence'] })
                  }
                >
                  <SelectTrigger id="plan-frequence">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="JOURNALIERE">Journalière</SelectItem>
                    <SelectItem value="HEBDOMADAIRE">Hebdomadaire</SelectItem>
                    <SelectItem value="MENSUELLE">Mensuelle</SelectItem>
                    <SelectItem value="TRIMESTRIELLE">Trimestrielle</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="plan-taux">Taux Rémunération (%)</Label>
                <Input
                  id="plan-taux"
                  type="number"
                  step="0.1"
                  value={formData.tauxRemuneration}
                  onChange={(e) =>
                    setFormData({ ...formData, tauxRemuneration: Number(e.target.value) })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="plan-duree">Durée (mois)</Label>
                <Input
                  id="plan-duree"
                  type="number"
                  value={formData.dureeMois}
                  onChange={(e) => setFormData({ ...formData, dureeMois: Number(e.target.value) })}
                />
              </div>
            </div>
            <div className="flex items-center justify-between rounded-lg border p-3">
              <div>
                <Label htmlFor="plan-actif" className="cursor-pointer">
                  Plan actif
                </Label>
                <p className="text-xs text-muted-foreground">
                  Les membres pourront souscrire si actif
                </p>
              </div>
              <Switch
                id="plan-actif"
                checked={formData.actif}
                onCheckedChange={(v) => setFormData({ ...formData, actif: v })}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleSave}>{editingPlan ? 'Modifier' : 'Créer'}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirm Dialog */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer le plan"
        description={`Êtes-vous sûr de vouloir supprimer le plan "${selectedPlan?.nom}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
