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
import { Switch } from '@/components/ui/switch';
import { Card, CardContent } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Plus, CheckCircle2, XCircle } from 'lucide-react';
import type { NanoCreditPalier, NanoCreditPalierRequest, FrequenceRemboursement } from '@/types';
import { formatCurrency } from '@/lib/constants';

// --- Mock data ---------------------------------------------------------------

const initialPaliers: NanoCreditPalier[] = [
  {
    id: '1', numero: '1', nom: 'Découverte', montantPlafond: 50000, dureeJours: 30,
    frequenceRemboursement: 'JOURNALIERE', tauxInteret: 5.0, penaliteParJour: 100,
    minMontantTotalRembourse: 30000, minEpargneCumulee: 10000, minEpargnePercent: 20,
    minGarantQualite: 50, pourcentagePartageGarant: 50, actif: true,
  },
  {
    id: '2', numero: '2', nom: 'Essentiel', montantPlafond: 150000, dureeJours: 60,
    frequenceRemboursement: 'HEBDOMADAIRE', tauxInteret: 7.5, penaliteParJour: 250,
    minMontantTotalRembourse: 80000, minEpargneCumulee: 30000, minEpargnePercent: 25,
    minGarantQualite: 60, pourcentagePartageGarant: 40, actif: true,
  },
  {
    id: '3', numero: '3', nom: 'Premium', montantPlafond: 300000, dureeJours: 90,
    frequenceRemboursement: 'MENSUELLE', tauxInteret: 10.0, penaliteParJour: 500,
    minMontantTotalRembourse: 150000, minEpargneCumulee: 60000, minEpargnePercent: 30,
    minGarantQualite: 70, pourcentagePartageGarant: 30, actif: true,
  },
  {
    id: '4', numero: '4', nom: 'Élite', montantPlafond: 500000, dureeJours: 120,
    frequenceRemboursement: 'MENSUELLE', tauxInteret: 8.0, penaliteParJour: 750,
    minMontantTotalRembourse: 250000, minEpargneCumulee: 100000, minEpargnePercent: 35,
    minGarantQualite: 80, pourcentagePartageGarant: 25, actif: false,
  },
  {
    id: '5', numero: '5', nom: 'Micro', montantPlafond: 25000, dureeJours: 15,
    frequenceRemboursement: 'JOURNALIERE', tauxInteret: 4.0, penaliteParJour: 50,
    minMontantTotalRembourse: 15000, minEpargneCumulee: 5000, minEpargnePercent: 15,
    minGarantQualite: 40, pourcentagePartageGarant: 60, actif: true,
  },
  {
    id: '6', numero: '6', nom: 'Standard', montantPlafond: 100000, dureeJours: 45,
    frequenceRemboursement: 'HEBDOMADAIRE', tauxInteret: 6.5, penaliteParJour: 200,
    minMontantTotalRembourse: 60000, minEpargneCumulee: 25000, minEpargnePercent: 22,
    minGarantQualite: 55, pourcentagePartageGarant: 45, actif: true,
  },
  {
    id: '7', numero: '7', nom: 'Avancé', montantPlafond: 200000, dureeJours: 75,
    frequenceRemboursement: 'MENSUELLE', tauxInteret: 8.5, penaliteParJour: 400,
    minMontantTotalRembourse: 120000, minEpargneCumulee: 50000, minEpargnePercent: 28,
    minGarantQualite: 65, pourcentagePartageGarant: 35, actif: true,
  },
  {
    id: '8', numero: '8', nom: 'Express', montantPlafond: 75000, dureeJours: 21,
    frequenceRemboursement: 'JOURNALIERE', tauxInteret: 6.0, penaliteParJour: 150,
    minMontantTotalRembourse: 45000, minEpargneCumulee: 15000, minEpargnePercent: 18,
    minGarantQualite: 45, pourcentagePartageGarant: 55, actif: true,
  },
  {
    id: '9', numero: '9', nom: 'Ambition', montantPlafond: 400000, dureeJours: 100,
    frequenceRemboursement: 'MENSUELLE', tauxInteret: 9.0, penaliteParJour: 600,
    minMontantTotalRembourse: 200000, minEpargneCumulee: 80000, minEpargnePercent: 32,
    minGarantQualite: 75, pourcentagePartageGarant: 28, actif: false,
  },
  {
    id: '10', numero: '10', nom: 'Solidarité', montantPlafond: 60000, dureeJours: 30,
    frequenceRemboursement: 'HEBDOMADAIRE', tauxInteret: 3.5, penaliteParJour: 75,
    minMontantTotalRembourse: 35000, minEpargneCumulee: 12000, minEpargnePercent: 16,
    minGarantQualite: 35, pourcentagePartageGarant: 65, actif: true,
  },
  {
    id: '11', numero: '11', nom: 'Scolaire', montantPlafond: 35000, dureeJours: 20,
    frequenceRemboursement: 'JOURNALIERE', tauxInteret: 3.0, penaliteParJour: 60,
    minMontantTotalRembourse: 20000, minEpargneCumulee: 8000, minEpargnePercent: 12,
    minGarantQualite: 30, pourcentagePartageGarant: 70, actif: true,
  },
];

const FREQUENCE_REMBOURSEMENT_LABELS: Record<string, string> = {
  JOURNALIERE: 'Journalière',
  HEBDOMADAIRE: 'Hebdomadaire',
  MENSUELLE: 'Mensuelle',
};

// --- Table columns -----------------------------------------------------------

type PalierRow = NanoCreditPalier & Record<string, unknown>;

const columns: Column<PalierRow>[] = [
  { key: 'numero', header: 'Numéro', sortable: true },
  { key: 'nom', header: 'Nom', sortable: true },
  {
    key: 'montantPlafond',
    header: 'Plafond',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatCurrency(item.montantPlafond as number)}</span>
    ),
  },
  {
    key: 'dureeJours',
    header: 'Durée (jours)',
    sortable: true,
    render: (item) => <span>{item.dureeJours as number} jours</span>,
  },
  {
    key: 'frequenceRemboursement',
    header: 'Fréquence remboursement',
    render: (item) => (
      <span>
        {FREQUENCE_REMBOURSEMENT_LABELS[item.frequenceRemboursement as string] ||
          (item.frequenceRemboursement as string)}
      </span>
    ),
  },
  {
    key: 'tauxInteret',
    header: 'Taux intérêt (%)',
    sortable: true,
    render: (item) => <span className="font-medium">{item.tauxInteret as number}%</span>,
  },
  {
    key: 'penaliteParJour',
    header: 'Pénalité/jour',
    render: (item) => (
      <span>{formatCurrency(item.penaliteParJour as number)}</span>
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

// --- Form defaults -----------------------------------------------------------

const emptyForm: NanoCreditPalierRequest = {
  numero: '',
  nom: '',
  montantPlafond: 0,
  dureeJours: 30,
  frequenceRemboursement: 'MENSUELLE',
  tauxInteret: 5.0,
  penaliteParJour: 100,
  actif: true,
};

// --- Component ---------------------------------------------------------------

export default function PaliersPage() {
  const [paliers, setPaliers] = useState<NanoCreditPalier[]>(initialPaliers);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [eligibilityOpen, setEligibilityOpen] = useState(false);
  const [editingPalier, setEditingPalier] = useState<PalierRow | null>(null);
  const [selectedPalier, setSelectedPalier] = useState<PalierRow | null>(null);
  const [formData, setFormData] = useState<NanoCreditPalierRequest>({ ...emptyForm });

  // Eligibility dialog state
  const [membreIdInput, setMembreIdInput] = useState('');
  const [eligibilityResult, setEligibilityResult] = useState<{
    eligible: boolean;
    message: string;
  } | null>(null);

  const openCreate = () => {
    setEditingPalier(null);
    setFormData({ ...emptyForm, numero: String(paliers.length + 1) });
    setDialogOpen(true);
  };

  const openEdit = (palier: PalierRow) => {
    setEditingPalier(palier);
    setFormData({
      numero: palier.numero as string,
      nom: palier.nom as string,
      montantPlafond: palier.montantPlafond as number,
      dureeJours: palier.dureeJours as number,
      frequenceRemboursement: palier.frequenceRemboursement as FrequenceRemboursement,
      tauxInteret: palier.tauxInteret as number,
      penaliteParJour: palier.penaliteParJour as number,
      actif: palier.actif as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    // TODO: Replace with API call
    if (editingPalier) {
      setPaliers(
        paliers.map((p) =>
          p.id === editingPalier.id ? { ...p, ...formData } : p
        )
      );
    } else {
      const newPalier: NanoCreditPalier = {
        id: String(Date.now()),
        ...formData,
        minMontantTotalRembourse: 0,
        minEpargneCumulee: 0,
        minEpargnePercent: 0,
        minGarantQualite: 0,
        pourcentagePartageGarant: 50,
      };
      setPaliers([...paliers, newPalier]);
    }
    setDialogOpen(false);
  };

  const handleDelete = () => {
    if (selectedPalier) {
      setPaliers(paliers.filter((p) => p.id !== selectedPalier.id));
      setDeleteOpen(false);
      setSelectedPalier(null);
    }
  };

  const openEligibility = (palier: PalierRow) => {
    setSelectedPalier(palier);
    setMembreIdInput('');
    setEligibilityResult(null);
    setEligibilityOpen(true);
  };

  const handleCheckEligibility = () => {
    // TODO: Replace with API call
    if (!membreIdInput.trim()) return;

    // Simulate eligibility check
    const isEligible = Math.random() > 0.4;
    setEligibilityResult({
      eligible: isEligible,
      message: isEligible
        ? `Le membre ${membreIdInput} est éligible au palier ${selectedPalier?.nom}.`
        : `Le membre ${membreIdInput} n'est pas éligible au palier ${selectedPalier?.nom}. Conditions non remplies.`,
    });
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Paliers Nano-Crédit"
        description="Configuration des paliers de nano-crédits"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouveau Palier
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={paliers as PalierRow[]}
            columns={columns}
            keyExtractor={(item) => item.id as string}
            searchKeys={['nom']}
            searchPlaceholder="Rechercher un palier..."
            pageSize={10}
            exportable={true}
            exportFilename="nano-credit-paliers"
            actions={(item) => [
              { label: 'Modifier', onClick: () => openEdit(item) },
              {
                label: "Vérifier éligibilité",
                onClick: () => openEligibility(item),
              },
              {
                label: 'Supprimer',
                onClick: () => {
                  setSelectedPalier(item);
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
            <DialogTitle>{editingPalier ? 'Modifier le palier' : 'Nouveau palier'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="palier-numero">Numéro</Label>
                <Input
                  id="palier-numero"
                  value={formData.numero}
                  onChange={(e) => setFormData({ ...formData, numero: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="palier-nom">Nom</Label>
                <Input
                  id="palier-nom"
                  value={formData.nom}
                  onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="palier-plafond">Plafond (FCFA)</Label>
                <Input
                  id="palier-plafond"
                  type="number"
                  value={formData.montantPlafond}
                  onChange={(e) =>
                    setFormData({ ...formData, montantPlafond: Number(e.target.value) })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="palier-duree">Durée (jours)</Label>
                <Input
                  id="palier-duree"
                  type="number"
                  value={formData.dureeJours}
                  onChange={(e) =>
                    setFormData({ ...formData, dureeJours: Number(e.target.value) })
                  }
                />
              </div>
            </div>
            <div className="grid grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label htmlFor="palier-frequence">Fréquence remboursement</Label>
                <Select
                  value={formData.frequenceRemboursement}
                  onValueChange={(v) =>
                    setFormData({
                      ...formData,
                      frequenceRemboursement: v as FrequenceRemboursement,
                    })
                  }
                >
                  <SelectTrigger id="palier-frequence">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="JOURNALIERE">Journalière</SelectItem>
                    <SelectItem value="HEBDOMADAIRE">Hebdomadaire</SelectItem>
                    <SelectItem value="MENSUELLE">Mensuelle</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="palier-taux">Taux intérêt (%)</Label>
                <Input
                  id="palier-taux"
                  type="number"
                  step="0.1"
                  value={formData.tauxInteret}
                  onChange={(e) =>
                    setFormData({ ...formData, tauxInteret: Number(e.target.value) })
                  }
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="palier-penalite">Pénalité/jour (FCFA)</Label>
                <Input
                  id="palier-penalite"
                  type="number"
                  value={formData.penaliteParJour}
                  onChange={(e) =>
                    setFormData({ ...formData, penaliteParJour: Number(e.target.value) })
                  }
                />
              </div>
            </div>
            <div className="flex items-center justify-between rounded-lg border p-3">
              <div>
                <Label htmlFor="palier-actif" className="cursor-pointer">
                  Palier actif
                </Label>
                <p className="text-xs text-muted-foreground">
                  Les membres pourront y accéder si actif
                </p>
              </div>
              <Switch
                id="palier-actif"
                checked={formData.actif}
                onCheckedChange={(v) => setFormData({ ...formData, actif: v })}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleSave}>{editingPalier ? 'Modifier' : 'Créer'}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Eligibility Check Dialog */}
      <Dialog open={eligibilityOpen} onOpenChange={setEligibilityOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Vérification d&apos;éligibilité</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Palier : <span className="font-semibold text-foreground">{selectedPalier?.nom}</span>{' '}
              — Plafond :{' '}
              <span className="font-semibold text-foreground">
                {selectedPalier ? formatCurrency(selectedPalier.montantPlafond as number) : ''}
              </span>
            </p>
            <div className="space-y-2">
              <Label htmlFor="elig-membreId">ID Membre</Label>
              <Input
                id="elig-membreId"
                value={membreIdInput}
                onChange={(e) => setMembreIdInput(e.target.value)}
                placeholder="Entrez l'identifiant du membre"
              />
            </div>

            {/* Result display */}
            {eligibilityResult && (
              <div
                className={`rounded-lg border p-4 flex items-start gap-3 ${
                  eligibilityResult.eligible
                    ? 'border-emerald-200 bg-emerald-50 dark:border-emerald-900 dark:bg-emerald-950'
                    : 'border-red-200 bg-red-50 dark:border-red-900 dark:bg-red-950'
                }`}
              >
                {eligibilityResult.eligible ? (
                  <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-emerald-600" />
                ) : (
                  <XCircle className="mt-0.5 h-5 w-5 shrink-0 text-red-600" />
                )}
                <div>
                  <p
                    className={`text-sm font-medium ${
                      eligibilityResult.eligible ? 'text-emerald-800 dark:text-emerald-200' : 'text-red-800 dark:text-red-200'
                    }`}
                  >
                    {eligibilityResult.eligible ? 'Éligible' : 'Non éligible'}
                  </p>
                  <p className="text-xs text-muted-foreground mt-1">
                    {eligibilityResult.message}
                  </p>
                </div>
              </div>
            )}

            {!eligibilityResult && (
              <div className="rounded-lg border p-4 bg-muted/50">
                <p className="text-sm text-muted-foreground">
                  Entrez l&apos;identifiant d&apos;un membre pour vérifier son éligibilité à ce
                  palier de nano-crédit.
                </p>
              </div>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEligibilityOpen(false)}>
              Fermer
            </Button>
            <Button
              className="gap-2"
              onClick={handleCheckEligibility}
              disabled={!membreIdInput.trim()}
            >
              <CheckCircle2 className="h-4 w-4" />
              Vérifier
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirm Dialog */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer le palier"
        description={`Êtes-vous sûr de vouloir supprimer le palier "${selectedPalier?.nom}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
