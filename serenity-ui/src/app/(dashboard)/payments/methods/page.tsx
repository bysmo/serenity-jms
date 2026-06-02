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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Switch } from '@/components/ui/switch';
import { Card, CardContent } from '@/components/ui/card';
import { Plus, Edit, Trash2 } from 'lucide-react';
import type { PaymentMethod } from '@/types';

const formatFCFA = (amount: number) => `${amount.toLocaleString('fr-FR')} FCFA`;

const mockMethods: (PaymentMethod & Record<string, unknown>)[] = [
  { id: '1', code: 'ORANGE_MONEY', name: 'Orange Money', gateway: 'PAYDUNYA', isActive: true, minAmount: 100, maxAmount: 500000, feesPercentage: 1.5, feesFixed: 0, description: 'Paiement via Orange Money' },
  { id: '2', code: 'WAVE', name: 'Wave', gateway: 'PAYDUNYA', isActive: true, minAmount: 100, maxAmount: 1000000, feesPercentage: 1.0, feesFixed: 0, description: 'Paiement via Wave' },
  { id: '3', code: 'FREE_MONEY', name: 'Free Money', gateway: 'PISPI', isActive: true, minAmount: 500, maxAmount: 300000, feesPercentage: 2.0, feesFixed: 50, description: 'Paiement via Free Money' },
  { id: '4', code: 'CARTE_BANCAIRE', name: 'Carte Bancaire', gateway: 'PAYDUNYA', isActive: false, minAmount: 1000, maxAmount: 2000000, feesPercentage: 2.5, feesFixed: 100, description: 'Paiement par carte bancaire' },
  { id: '5', code: 'VIREMENT', name: 'Virement', gateway: 'PISPI', isActive: true, minAmount: 5000, maxAmount: 5000000, feesPercentage: 0.5, feesFixed: 200, description: 'Virement bancaire' },
  { id: '6', code: 'ESPECES', name: 'Espèces', gateway: 'PAYDUNYA', isActive: true, minAmount: 0, maxAmount: 100000, feesPercentage: 0, feesFixed: 0, description: 'Paiement en espèces' },
  { id: '7', code: 'MTN_MONEY', name: 'MTN Mobile Money', gateway: 'PISPI', isActive: true, minAmount: 200, maxAmount: 400000, feesPercentage: 1.8, feesFixed: 25, description: 'Paiement via MTN Mobile Money' },
  { id: '8', code: 'MOOV_MONEY', name: 'Moov Money', gateway: 'PISPI', isActive: false, minAmount: 300, maxAmount: 250000, feesPercentage: 2.2, feesFixed: 75, description: 'Paiement via Moov Money' },
  { id: '9', code: 'CHEQUE', name: 'Chèque', gateway: 'PAYDUNYA', isActive: false, minAmount: 10000, maxAmount: 5000000, feesPercentage: 0, feesFixed: 500, description: 'Paiement par chèque bancaire' },
  { id: '10', code: 'WIZALL', name: 'Wizall Money', gateway: 'PISPI', isActive: true, minAmount: 100, maxAmount: 200000, feesPercentage: 1.5, feesFixed: 0, description: 'Paiement via Wizall Money' },
  { id: '11', code: 'EMERALD', name: 'Emerald Money', gateway: 'PAYDUNYA', isActive: true, minAmount: 500, maxAmount: 300000, feesPercentage: 1.2, feesFixed: 0, description: 'Paiement via Emerald Money' },
];

const GATEWAY_LABELS: Record<string, string> = { PAYDUNYA: 'PayDunya', PISPI: 'Pi-SPI' };

const emptyForm = { code: '', name: '', gateway: 'PAYDUNYA', description: '', minAmount: 0, maxAmount: 0, feesPercentage: 0, feesFixed: 0, isActive: true };

export default function PaymentMethodsPage() {
  const [methods, setMethods] = useState(mockMethods);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editing, setEditing] = useState<(PaymentMethod & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(PaymentMethod & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<PaymentMethod & Record<string, unknown>>[] = [
    { key: 'code', header: 'Code', sortable: true },
    { key: 'name', header: 'Nom', sortable: true },
    {
      key: 'gateway', header: 'Gateway', sortable: true,
      render: (item) => <span className="text-sm font-medium">{GATEWAY_LABELS[item.gateway as string] || item.gateway}</span>,
    },
    {
      key: 'isActive', header: 'Actif', sortable: true,
      render: (item) => <StatusBadge statut={item.isActive ? 'ACTIF' : 'INACTIF'} label={item.isActive ? 'Actif' : 'Inactif'} />,
    },
    {
      key: 'minAmount', header: 'Montant Min', sortable: true,
      render: (item) => <span>{formatFCFA(item.minAmount as number)}</span>,
    },
    {
      key: 'maxAmount', header: 'Montant Max', sortable: true,
      render: (item) => <span>{formatFCFA(item.maxAmount as number)}</span>,
    },
  ];

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (item: PaymentMethod & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      code: item.code,
      name: item.name,
      gateway: item.gateway as string,
      description: (item.description as string) || '',
      minAmount: item.minAmount as number,
      maxAmount: item.maxAmount as number,
      feesPercentage: (item.feesPercentage as number) || 0,
      feesFixed: (item.feesFixed as number) || 0,
      isActive: item.isActive as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setMethods(methods.map((m) => m.id === editing.id ? { ...m, ...form } : m));
    } else {
      const newMethod: PaymentMethod & Record<string, unknown> = {
        id: String(methods.length + 1),
        ...form,
      };
      setMethods([...methods, newMethod]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
  };

  const handleDelete = () => {
    if (selected) {
      setMethods(methods.filter((m) => m.id !== selected.id));
      setDeleteOpen(false);
      setSelected(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Méthodes de Paiement"
        description="Configuration des méthodes de paiement disponibles"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouvelle Méthode
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={methods}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['code', 'name']}
            searchPlaceholder="Rechercher une méthode..."
            pageSize={10}
            exportable={true}
            exportFilename="methodes-paiement"
            actions={(item) => [
              { label: 'Modifier', onClick: () => openEdit(item) },
              { label: 'Supprimer', onClick: () => { setSelected(item); setDeleteOpen(true); }, variant: 'destructive' },
            ]}
          />
        </CardContent>
      </Card>

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>{editing ? 'Modifier la Méthode' : 'Nouvelle Méthode'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Code</Label>
                <Input value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} placeholder="ORANGE_MONEY" />
              </div>
              <div className="space-y-2">
                <Label>Nom</Label>
                <Input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Orange Money" />
              </div>
            </div>
            <div className="space-y-2">
              <Label>Gateway</Label>
              <Select value={form.gateway} onValueChange={(v) => setForm({ ...form, gateway: v })}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="PAYDUNYA">PayDunya</SelectItem>
                  <SelectItem value="PISPI">Pi-SPI</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Description</Label>
              <Input value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Montant Min (FCFA)</Label>
                <Input type="number" value={form.minAmount || ''} onChange={(e) => setForm({ ...form, minAmount: Number(e.target.value) })} />
              </div>
              <div className="space-y-2">
                <Label>Montant Max (FCFA)</Label>
                <Input type="number" value={form.maxAmount || ''} onChange={(e) => setForm({ ...form, maxAmount: Number(e.target.value) })} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Frais (%)</Label>
                <Input type="number" step="0.1" value={form.feesPercentage || ''} onChange={(e) => setForm({ ...form, feesPercentage: Number(e.target.value) })} />
              </div>
              <div className="space-y-2">
                <Label>Frais fixes (FCFA)</Label>
                <Input type="number" value={form.feesFixed || ''} onChange={(e) => setForm({ ...form, feesFixed: Number(e.target.value) })} />
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Switch checked={form.isActive} onCheckedChange={(v) => setForm({ ...form, isActive: v })} />
              <Label>Actif</Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>Annuler</Button>
            <Button onClick={handleSave}>{editing ? 'Mettre à jour' : 'Créer'}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Dialog */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer la méthode"
        description={`Êtes-vous sûr de vouloir supprimer la méthode "${selected?.name}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
