'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { Edit, Save, X } from 'lucide-react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale/fr';
import type {
  ParrainageConfig,
  ParrainageCommission,
  TypeRemuneration,
  DeclencheurParrainage,
} from '@/types';

// ── Currency helper ──────────────────────────────────────────────────────
const fmtCurrency = (amount: number) =>
  new Intl.NumberFormat('fr-SN').format(amount) + ' FCFA';

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

// ── Labels ───────────────────────────────────────────────────────────────
const TYPE_REMUNERATION_LABELS: Record<string, string> = {
  FIXE: 'Montant fixe',
  POURCENTAGE: 'Pourcentage',
};

const DECLENCHEUR_LABELS: Record<string, string> = {
  INSCRIPTION: 'Inscription',
  PREMIER_PAIEMENT: 'Premier paiement',
  PREMIERE_COTISATION: 'Première cotisation',
};

// ── Mock data: ParrainageConfig ──────────────────────────────────────────
const mockConfig: ParrainageConfig = {
  id: '1',
  actif: true,
  typeRemuneration: 'FIXE',
  montantFixe: 2000,
  pourcentage: undefined,
  declencheur: 'INSCRIPTION',
  niveauMax: 3,
  delaiDisponibiliteJours: 30,
  plafondMensuel: 10000,
};

// ── Extended type with resolved names ────────────────────────────────────
type CommissionRow = ParrainageCommission & Record<string, unknown> & {
  parrain: string;
  filleul: string;
  date: string;
};

// ── Build display rows ───────────────────────────────────────────────────
const buildCommissionRows = (commissions: ParrainageCommission[]): CommissionRow[] =>
  commissions.map((c) => ({
    ...c,
    parrain: memberNames[c.parrainId] || c.parrainId,
    filleul: memberNames[c.filleulId] || c.filleulId,
    date: format(new Date(c.createdAt), 'yyyy-MM-dd'),
  }));

// ── Mock data: 20 commissions ────────────────────────────────────────────
const mockCommissions: ParrainageCommission[] = [
  {
    id: '1',
    parrainId: '1',
    filleulId: '2',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-02-20T00:00:00',
    createdAt: '2024-02-20T10:00:00',
  },
  {
    id: '2',
    parrainId: '1',
    filleulId: '4',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-04-05T00:00:00',
    createdAt: '2024-04-05T14:00:00',
  },
  {
    id: '3',
    parrainId: '2',
    filleulId: '5',
    montant: 2000,
    statut: 'RECLAMEE',
    dateDisponibilite: '2024-05-12T00:00:00',
    dateReclamation: '2024-05-15T10:00:00',
    createdAt: '2024-05-12T09:00:00',
  },
  {
    id: '4',
    parrainId: '6',
    filleulId: '8',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-08-30T00:00:00',
    createdAt: '2024-08-30T16:00:00',
  },
  {
    id: '5',
    parrainId: '4',
    filleulId: '6',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-06-18T00:00:00',
    createdAt: '2024-06-18T11:00:00',
  },
  {
    id: '6',
    parrainId: '1',
    filleulId: '9',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-09-15T00:00:00',
    createdAt: '2024-09-15T10:20:00',
  },
  {
    id: '7',
    parrainId: '9',
    filleulId: '11',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-01-30T00:00:00',
    createdAt: '2024-01-22T09:00:00',
  },
  {
    id: '8',
    parrainId: '12',
    filleulId: '14',
    montant: 2000,
    statut: 'RECLAMEE',
    dateDisponibilite: '2024-04-18T00:00:00',
    dateReclamation: '2024-04-20T08:00:00',
    createdAt: '2024-04-18T08:45:00',
  },
  {
    id: '9',
    parrainId: '11',
    filleulId: '15',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-05-20T00:00:00',
    createdAt: '2024-05-05T14:20:00',
  },
  {
    id: '10',
    parrainId: '17',
    filleulId: '16',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-06-15T00:00:00',
    createdAt: '2024-06-10T10:00:00',
  },
  {
    id: '11',
    parrainId: '15',
    filleulId: '18',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-08-25T00:00:00',
    createdAt: '2024-08-15T09:45:00',
  },
  {
    id: '12',
    parrainId: '20',
    filleulId: '21',
    montant: 2000,
    statut: 'RECLAMEE',
    dateDisponibilite: '2024-10-15T00:00:00',
    dateReclamation: '2024-10-18T14:00:00',
    createdAt: '2024-10-08T15:15:00',
  },
  {
    id: '13',
    parrainId: '21',
    filleulId: '22',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-10-30T00:00:00',
    createdAt: '2024-10-25T10:00:00',
  },
  {
    id: '14',
    parrainId: '8',
    filleulId: '26',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-12-10T00:00:00',
    createdAt: '2024-12-10T11:30:00',
  },
  {
    id: '15',
    parrainId: '26',
    filleulId: '27',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-12-20T00:00:00',
    createdAt: '2024-12-15T16:00:00',
  },
  {
    id: '16',
    parrainId: '4',
    filleulId: '28',
    montant: 2000,
    statut: 'RECLAMEE',
    dateDisponibilite: '2024-12-25T00:00:00',
    dateReclamation: '2024-12-26T09:00:00',
    createdAt: '2024-12-20T08:00:00',
  },
  {
    id: '17',
    parrainId: '1',
    filleulId: '3',
    montant: 1500,
    statut: 'PAYEE',
    dateDisponibilite: '2024-03-10T00:00:00',
    createdAt: '2024-03-10T14:00:00',
  },
  {
    id: '18',
    parrainId: '6',
    filleulId: '10',
    montant: 2000,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-10-05T00:00:00',
    createdAt: '2024-10-01T15:00:00',
  },
  {
    id: '19',
    parrainId: '18',
    filleulId: '20',
    montant: 2000,
    statut: 'PAYEE',
    dateDisponibilite: '2024-09-25T00:00:00',
    createdAt: '2024-09-20T07:30:00',
  },
  {
    id: '20',
    parrainId: '14',
    filleulId: '24',
    montant: 2500,
    statut: 'DISPONIBLE',
    dateDisponibilite: '2024-11-20T00:00:00',
    createdAt: '2024-11-15T14:00:00',
  },
];

// ── Commission columns ───────────────────────────────────────────────────
const columns: Column<CommissionRow>[] = [
  {
    key: 'parrain',
    header: 'Parrain',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{item.parrain as string}</span>
    ),
  },
  {
    key: 'filleul',
    header: 'Filleul',
    render: (item) => (
      <span>{item.filleul as string}</span>
    ),
  },
  {
    key: 'montant',
    header: 'Montant',
    sortable: true,
    render: (item) => (
      <span className="font-medium">
        {fmtCurrency(item.montant as number)}
      </span>
    ),
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => <StatusBadge statut={item.statut as string} />,
  },
  {
    key: 'date',
    header: 'Date',
    sortable: true,
    render: (item) => (
      <span className="text-sm text-muted-foreground">
        {format(new Date(item.createdAt as string), 'dd MMM yyyy', { locale: fr })}
      </span>
    ),
  },
];

// ── Config form type ─────────────────────────────────────────────────────
interface ConfigForm {
  typeRemuneration: TypeRemuneration;
  montantFixe: number;
  pourcentage: number;
  niveauMax: number;
  delaiDisponibiliteJours: number;
  plafondMensuel: number;
  actif: boolean;
}

// ── Page Component ───────────────────────────────────────────────────────
export default function ParrainagePage() {
  const [config, setConfig] = useState(mockConfig);
  const [commissions] = useState(mockCommissions);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState<ConfigForm>({
    typeRemuneration: config.typeRemuneration,
    montantFixe: config.montantFixe || 0,
    pourcentage: config.pourcentage || 0,
    niveauMax: config.niveauMax,
    delaiDisponibiliteJours: config.delaiDisponibiliteJours,
    plafondMensuel: config.plafondMensuel || 0,
    actif: config.actif,
  });

  const commissionRows = buildCommissionRows(commissions);

  // ── Handlers ──────────────────────────────────────────────────────────
  const handleSaveConfig = () => {
    setConfig({
      ...config,
      typeRemuneration: formData.typeRemuneration,
      montantFixe:
        formData.typeRemuneration === 'FIXE'
          ? formData.montantFixe
          : undefined,
      pourcentage:
        formData.typeRemuneration === 'POURCENTAGE'
          ? formData.pourcentage
          : undefined,
      niveauMax: formData.niveauMax,
      delaiDisponibiliteJours: formData.delaiDisponibiliteJours,
      plafondMensuel: formData.plafondMensuel || undefined,
      actif: formData.actif,
    });
    setEditing(false);
  };

  const startEditing = () => {
    setFormData({
      typeRemuneration: config.typeRemuneration,
      montantFixe: config.montantFixe || 0,
      pourcentage: config.pourcentage || 0,
      niveauMax: config.niveauMax,
      delaiDisponibiliteJours: config.delaiDisponibiliteJours,
      plafondMensuel: config.plafondMensuel || 0,
      actif: config.actif,
    });
    setEditing(true);
  };

  // ── Render ────────────────────────────────────────────────────────────
  return (
    <div className="space-y-6">
      <PageHeader
        title="Parrainage"
        description="Configuration et suivi du programme de parrainage"
      />

      {/* ── Config Section ────────────────────────────────────────────── */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
          <CardTitle className="text-base">
            Configuration du Parrainage
          </CardTitle>
          {!editing ? (
            <Button
              variant="outline"
              size="sm"
              className="gap-2"
              onClick={startEditing}
            >
              <Edit className="h-4 w-4" />
              Modifier
            </Button>
          ) : (
            <div className="flex gap-2">
              <Button
                size="sm"
                className="gap-2"
                onClick={handleSaveConfig}
              >
                <Save className="h-4 w-4" />
                Enregistrer
              </Button>
              <Button
                variant="outline"
                size="sm"
                className="gap-2"
                onClick={() => setEditing(false)}
              >
                <X className="h-4 w-4" />
                Annuler
              </Button>
            </div>
          )}
        </CardHeader>
        <CardContent>
          {editing ? (
            <div className="space-y-5">
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {/* Type rémunération */}
                <div className="space-y-2">
                  <Label>Type de rémunération</Label>
                  <Select
                    value={formData.typeRemuneration}
                    onValueChange={(v) =>
                      setFormData({
                        ...formData,
                        typeRemuneration: v as TypeRemuneration,
                      })
                    }
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="FIXE">Montant fixe</SelectItem>
                      <SelectItem value="POURCENTAGE">Pourcentage</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                {/* Montant fixe ou Pourcentage */}
                {formData.typeRemuneration === 'FIXE' ? (
                  <div className="space-y-2">
                    <Label>Montant fixe (FCFA)</Label>
                    <Input
                      type="number"
                      value={formData.montantFixe}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          montantFixe: Number(e.target.value),
                        })
                      }
                    />
                  </div>
                ) : (
                  <div className="space-y-2">
                    <Label>Pourcentage (%)</Label>
                    <Input
                      type="number"
                      step="0.1"
                      value={formData.pourcentage}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          pourcentage: Number(e.target.value),
                        })
                      }
                    />
                  </div>
                )}

                {/* Niveau max */}
                <div className="space-y-2">
                  <Label>Niveau maximum</Label>
                  <Input
                    type="number"
                    min={1}
                    value={formData.niveauMax}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        niveauMax: Number(e.target.value),
                      })
                    }
                  />
                </div>

                {/* Délai disponibilité */}
                <div className="space-y-2">
                  <Label>Délai disponibilité (jours)</Label>
                  <Input
                    type="number"
                    min={0}
                    value={formData.delaiDisponibiliteJours}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        delaiDisponibiliteJours: Number(e.target.value),
                      })
                    }
                  />
                </div>

                {/* Plafond mensuel */}
                <div className="space-y-2">
                  <Label>Plafond mensuel (FCFA)</Label>
                  <Input
                    type="number"
                    min={0}
                    value={formData.plafondMensuel}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        plafondMensuel: Number(e.target.value),
                      })
                    }
                  />
                </div>
              </div>

              <Separator />

              {/* Actif toggle */}
              <div className="flex items-center justify-between rounded-lg border p-4">
                <div className="space-y-0.5">
                  <Label className="text-base">Programme actif</Label>
                  <p className="text-sm text-muted-foreground">
                    Activer ou désactiver le programme de parrainage
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
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              <div>
                <p className="text-sm text-muted-foreground">
                  Type de rémunération
                </p>
                <p className="font-medium">
                  {TYPE_REMUNERATION_LABELS[config.typeRemuneration]}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  {config.typeRemuneration === 'FIXE'
                    ? 'Montant fixe'
                    : 'Pourcentage'}
                </p>
                <p className="font-medium">
                  {config.typeRemuneration === 'FIXE'
                    ? fmtCurrency(config.montantFixe || 0)
                    : `${config.pourcentage}%`}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Déclencheur</p>
                <p className="font-medium">
                  {DECLENCHEUR_LABELS[config.declencheur]}
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Niveau maximum
                </p>
                <p className="font-medium">{config.niveauMax}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Délai disponibilité
                </p>
                <p className="font-medium">
                  {config.delaiDisponibiliteJours} jours
                </p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Plafond mensuel
                </p>
                <p className="font-medium">
                  {config.plafondMensuel
                    ? fmtCurrency(config.plafondMensuel)
                    : 'Non défini'}
                </p>
              </div>
              <div className="sm:col-span-2 lg:col-span-3">
                <p className="text-sm text-muted-foreground">Statut</p>
                <StatusBadge
                  statut={config.actif ? 'ACTIF' : 'INACTIF'}
                  label={config.actif ? 'Actif' : 'Inactif'}
                />
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      {/* ── Commissions Section ───────────────────────────────────────── */}
      <Card>
        <CardContent className="p-6">
          <h3 className="text-base font-semibold mb-4">
            Liste des Commissions
          </h3>
          <DataTable
            data={commissionRows}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['parrain', 'filleul']}
            searchPlaceholder="Rechercher par parrain ou filleul..."
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'Disponible', value: 'DISPONIBLE' },
                  { label: 'Réclamée', value: 'RECLAMEE' },
                  { label: 'Payée', value: 'PAYEE' },
                ],
              },
            ]}
            pageSize={10}
            selectable
            onSelectionChange={(selected) => {
              console.log('Commissions sélectionnées:', selected.length);
            }}
            exportable
            exportFilename="parrainage-commissions"
          />
        </CardContent>
      </Card>
    </div>
  );
}
