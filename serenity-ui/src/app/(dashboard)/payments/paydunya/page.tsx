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
import { Plus, Edit, Trash2, Power } from 'lucide-react';
import type { PayDunyaConfig } from '@/types';

const maskValue = (val: string) => val.length > 8 ? `${val.slice(0, 4)}${'•'.repeat(8)}${val.slice(-4)}` : '••••••••';

const mockConfigs: (PayDunyaConfig & Record<string, unknown>)[] = [
  {
    id: '1', organisationId: 'org-1', masterKey: 'mk_test_a1b2c3d4e5f6g7h8i9j0k1l2m3', privateKey: 'pk_test_x9y8z7w6v5u4t3s2r1q0p9o8', publicKey: 'pub_test_a1b2c3d4e5f6g7h8', token: 'tok_test_m1n2o3p4q5r6s7t8', mode: 'test', ipnUrl: 'https://api.serenity.sn/webhooks/paydunya', isActive: true,
  },
  {
    id: '2', organisationId: 'org-1', masterKey: 'mk_live_z9y8x7w6v5u4t3s2r1q0p9o8n7', privateKey: 'pk_live_m6n5o4p3q2r1s0t9u8v7w6', publicKey: 'pub_live_z9y8x7w6v5u4t3s2', token: 'tok_live_u7v8w9x0y1z2a3b4', mode: 'live', ipnUrl: 'https://api.serenity.sn/webhooks/paydunya', isActive: false,
  },
  {
    id: '3', organisationId: 'org-2', masterKey: 'mk_test_b2c3d4e5f6g7h8i9j0k1l2m3n4', privateKey: 'pk_test_y8z7w6v5u4t3s2r1q0p9o8n7', publicKey: 'pub_test_b2c3d4e5f6g7h8i9', token: 'tok_test_n2o3p4q5r6s7t8u9', mode: 'test', ipnUrl: 'https://api.serenity-2.sn/webhooks/paydunya', isActive: false,
  },
  {
    id: '4', organisationId: 'org-1', masterKey: 'mk_test_c3d4e5f6g7h8i9j0k1l2m3n4o5', privateKey: 'pk_test_z7w6v5u4t3s2r1q0p9o8n7m6', publicKey: 'pub_test_c3d4e5f6g7h8i9j0', token: 'tok_test_o3p4q5r6s7t8u9v0', mode: 'test', ipnUrl: 'https://api.serenity.sn/webhooks/paydunya-v2', isActive: false,
  },
  {
    id: '5', organisationId: 'org-3', masterKey: 'mk_live_a4b5c6d7e8f9g0h1i2j3k4l5m6', privateKey: 'pk_live_w6v5u4t3s2r1q0p9o8n7m6l5', publicKey: 'pub_live_a4b5c6d7e8f9g0h1', token: 'tok_live_p4q5r6s7t8u9v0w1', mode: 'live', ipnUrl: 'https://api.serenity-3.sn/webhooks/paydunya', isActive: false,
  },
  {
    id: '6', organisationId: 'org-1', masterKey: 'mk_test_d4e5f6g7h8i9j0k1l2m3n4o5p6', privateKey: 'pk_test_a6b5c4d3e2f1g0h9i8j7k6', publicKey: 'pub_test_d4e5f6g7h8i9j0k1', token: 'tok_test_q4r5s6t7u8v9w0x1', mode: 'test', ipnUrl: 'https://api.serenity.sn/webhooks/paydunya-staging', isActive: false,
  },
  {
    id: '7', organisationId: 'org-2', masterKey: 'mk_live_e5f6g7h8i9j0k1l2m3n4o5p6q7', privateKey: 'pk_live_b7c6d5e4f3g2h1i0j9k8l7', publicKey: 'pub_live_e5f6g7h8i9j0k1l2', token: 'tok_live_r5s6t7u8v9w0x1y2', mode: 'live', ipnUrl: 'https://api.serenity-2.sn/webhooks/paydunya', isActive: false,
  },
];

const emptyForm = { masterKey: '', privateKey: '', publicKey: '', token: '', mode: 'test', ipnUrl: '' };

export default function PayDunyaPage() {
  const [configs, setConfigs] = useState(mockConfigs);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [activateOpen, setActivateOpen] = useState(false);
  const [editing, setEditing] = useState<(PayDunyaConfig & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(PayDunyaConfig & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<PayDunyaConfig & Record<string, unknown>>[] = [
    {
      key: 'masterKey', header: 'Master Key', sortable: true,
      render: (item) => <span className="font-mono text-xs">{maskValue(item.masterKey as string)}</span>,
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

  const openEdit = (item: PayDunyaConfig & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      masterKey: item.masterKey as string,
      privateKey: item.privateKey as string,
      publicKey: item.publicKey as string,
      token: item.token as string,
      mode: item.mode as string,
      ipnUrl: (item.ipnUrl as string) || '',
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setConfigs(configs.map((c) => c.id === editing.id ? { ...c, ...form } : c));
    } else {
      const newConfig: PayDunyaConfig & Record<string, unknown> = {
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
        title="Configuration PayDunya"
        description="Gestion des configurations de la passerelle PayDunya"
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
            exportFilename="paydunya-configs"
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
            <DialogTitle>{editing ? 'Modifier la Configuration' : 'Nouvelle Configuration PayDunya'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Master Key</Label>
              <Input value={form.masterKey} onChange={(e) => setForm({ ...form, masterKey: e.target.value })} placeholder="Master Key" />
            </div>
            <div className="space-y-2">
              <Label>Private Key</Label>
              <Input value={form.privateKey} onChange={(e) => setForm({ ...form, privateKey: e.target.value })} placeholder="Private Key" />
            </div>
            <div className="space-y-2">
              <Label>Public Key</Label>
              <Input value={form.publicKey} onChange={(e) => setForm({ ...form, publicKey: e.target.value })} placeholder="Public Key" />
            </div>
            <div className="space-y-2">
              <Label>Token</Label>
              <Input value={form.token} onChange={(e) => setForm({ ...form, token: e.target.value })} placeholder="Token" />
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
                <Label>IPN URL</Label>
                <Input value={form.ipnUrl} onChange={(e) => setForm({ ...form, ipnUrl: e.target.value })} placeholder="https://..." />
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
        description="Êtes-vous sûr de vouloir supprimer cette configuration PayDunya ? Cette action est irréversible."
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
