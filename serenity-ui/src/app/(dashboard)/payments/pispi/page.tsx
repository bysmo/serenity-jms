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
import { Card, CardContent } from '@/components/ui/card';
import { Plus, Edit, Trash2 } from 'lucide-react';
import type { PiSpiConfig } from '@/types';

const maskValue = (val: string) => val.length > 8 ? `${val.slice(0, 4)}${'•'.repeat(8)}${val.slice(-4)}` : '••••••••';

const mockConfigs: (PiSpiConfig & Record<string, unknown>)[] = [
  {
    id: '1', organisationId: 'org-1', clientId: 'client_test_a1b2c3d4e5f6g7h8', clientSecret: 'secret_test_x9y8z7w6v5u4', apiKey: 'api_test_m1n2o3p4q5r6', payeAlias: 'paye_test_serenity', mode: 'test', callbackUrl: 'https://api.serenity.sn/webhooks/pispi', isActive: true,
  },
  {
    id: '2', organisationId: 'org-1', clientId: 'client_live_z9y8x7w6v5u4t3s2', clientSecret: 'secret_live_r1q0p9o8n7m6', apiKey: 'api_live_u7v8w9x0y1z2', payeAlias: 'paye_live_serenity', mode: 'live', callbackUrl: 'https://api.serenity.sn/webhooks/pispi', isActive: false,
  },
  {
    id: '3', organisationId: 'org-2', clientId: 'client_test_b2c3d4e5f6g7h8i9', clientSecret: 'secret_test_a1b2c3d4e5f6', apiKey: 'api_test_n2o3p4q5r6s7', payeAlias: 'paye_test_serenity2', mode: 'test', callbackUrl: 'https://api.serenity-2.sn/webhooks/pispi', isActive: false,
  },
  {
    id: '4', organisationId: 'org-1', clientId: 'client_test_c3d4e5f6g7h8i9j0', clientSecret: 'secret_test_b2c3d4e5f6g7', apiKey: 'api_test_o3p4q5r6s7t8', payeAlias: 'paye_test_serenity_stg', mode: 'test', callbackUrl: 'https://api.serenity.sn/webhooks/pispi-staging', isActive: false,
  },
  {
    id: '5', organisationId: 'org-3', clientId: 'client_live_d4e5f6g7h8i9j0k1', clientSecret: 'secret_live_c3d4e5f6g7h8', apiKey: 'api_live_p4q5r6s7t8u9', payeAlias: 'paye_live_serenity3', mode: 'live', callbackUrl: 'https://api.serenity-3.sn/webhooks/pispi', isActive: false,
  },
  {
    id: '6', organisationId: 'org-2', clientId: 'client_live_e5f6g7h8i9j0k1l2', clientSecret: 'secret_live_d4e5f6g7h8i9', apiKey: 'api_live_q5r6s7t8u9v0', payeAlias: 'paye_live_serenity2', mode: 'live', callbackUrl: 'https://api.serenity-2.sn/webhooks/pispi', isActive: false,
  },
  {
    id: '7', organisationId: 'org-1', clientId: 'client_test_f6g7h8i9j0k1l2m3', clientSecret: 'secret_test_e5f6g7h8i9j0', apiKey: 'api_test_r6s7t8u9v0w1', payeAlias: 'paye_test_serenity_uat', mode: 'test', callbackUrl: 'https://api.serenity.sn/webhooks/pispi-uat', isActive: false,
  },
];

const emptyForm = { clientId: '', clientSecret: '', apiKey: '', payeAlias: '', mode: 'test', callbackUrl: '' };

export default function PiSpiPage() {
  const [configs, setConfigs] = useState(mockConfigs);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [activateOpen, setActivateOpen] = useState(false);
  const [editing, setEditing] = useState<(PiSpiConfig & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(PiSpiConfig & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<PiSpiConfig & Record<string, unknown>>[] = [
    {
      key: 'clientId', header: 'Client ID', sortable: true,
      render: (item) => <span className="font-mono text-xs">{maskValue(item.clientId as string)}</span>,
    },
    {
      key: 'mode', header: 'Mode', sortable: true,
      render: (item) => (
        <StatusBadge
          statut={item.mode === 'live' ? 'ACTIF' : 'EN_ATTENTE'}
          label={item.mode === 'live' ? 'Live' : 'Test'}
        />
      ),
    },
    {
      key: 'isActive', header: 'Actif', sortable: true,
      render: (item) => <StatusBadge statut={item.isActive ? 'ACTIF' : 'INACTIF'} label={item.isActive ? 'Actif' : 'Inactif'} />,
    },
  ];

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (item: PiSpiConfig & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      clientId: item.clientId as string,
      clientSecret: item.clientSecret as string,
      apiKey: item.apiKey as string,
      payeAlias: item.payeAlias as string,
      mode: item.mode as string,
      callbackUrl: (item.callbackUrl as string) || '',
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setConfigs(configs.map((c) => c.id === editing.id ? { ...c, ...form } : c));
    } else {
      const newConfig: PiSpiConfig & Record<string, unknown> = {
        id: String(configs.length + 1),
        organisationId: 'org-1',
        ...form,
        isActive: false,
      };
      setConfigs([...configs, newConfig]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
  };

  const handleActivate = () => {
    if (selected) {
      setConfigs(configs.map((c) => ({
        ...c,
        isActive: c.id === selected.id,
      })));
      setActivateOpen(false);
      setSelected(null);
    }
  };

  const handleDelete = () => {
    if (selected) {
      setConfigs(configs.filter((c) => c.id !== selected.id));
      setDeleteOpen(false);
      setSelected(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Configuration Pi-SPI"
        description="Gestion des configurations de la passerelle Pi-SPI"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouvelle Config
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={configs}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchable={false}
            pageSize={10}
            exportable={true}
            exportFilename="pispi-configs"
            actions={(item) => {
              const actions: { label: string; onClick: () => void; variant?: 'default' | 'destructive' }[] = [];
              if (!item.isActive) {
                actions.push({ label: 'Activer', onClick: () => { setSelected(item); setActivateOpen(true); } });
              }
              actions.push({ label: 'Modifier', onClick: () => openEdit(item) });
              actions.push({ label: 'Supprimer', onClick: () => { setSelected(item); setDeleteOpen(true); }, variant: 'destructive' });
              return actions;
            }}
          />
        </CardContent>
      </Card>

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>{editing ? 'Modifier la Configuration' : 'Nouvelle Configuration Pi-SPI'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Client ID</Label>
              <Input value={form.clientId} onChange={(e) => setForm({ ...form, clientId: e.target.value })} placeholder="Client ID" />
            </div>
            <div className="space-y-2">
              <Label>Client Secret</Label>
              <Input type="password" value={form.clientSecret} onChange={(e) => setForm({ ...form, clientSecret: e.target.value })} placeholder="Client Secret" />
            </div>
            <div className="space-y-2">
              <Label>API Key</Label>
              <Input type="password" value={form.apiKey} onChange={(e) => setForm({ ...form, apiKey: e.target.value })} placeholder="API Key" />
            </div>
            <div className="space-y-2">
              <Label>Paye Alias</Label>
              <Input value={form.payeAlias} onChange={(e) => setForm({ ...form, payeAlias: e.target.value })} placeholder="paye_alias" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Mode</Label>
                <Select value={form.mode} onValueChange={(v) => setForm({ ...form, mode: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="test">Test</SelectItem>
                    <SelectItem value="live">Live</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Callback URL</Label>
                <Input value={form.callbackUrl} onChange={(e) => setForm({ ...form, callbackUrl: e.target.value })} placeholder="https://..." />
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>Annuler</Button>
            <Button onClick={handleSave}>{editing ? 'Mettre à jour' : 'Créer'}</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Activate Confirm */}
      <ConfirmDialog
        open={activateOpen}
        onOpenChange={setActivateOpen}
        title="Activer cette configuration"
        description="Une seule configuration peut être active à la fois. La configuration actuellement active sera désactivée."
        confirmLabel="Activer"
        onConfirm={handleActivate}
      />

      {/* Delete Dialog */}
      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Supprimer la configuration"
        description="Êtes-vous sûr de vouloir supprimer cette configuration Pi-SPI ? Cette action est irréversible."
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
