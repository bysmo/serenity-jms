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
import { Plus, Edit, Trash2 } from 'lucide-react';
import type { SmsGateway } from '@/types';

const mockGateways: (SmsGateway & Record<string, unknown>)[] = [
  { id: '1', nom: 'Orange SMS Sénégal', providerCode: 'ORANGE', apiUrl: 'https://api.orange.com/sms', apiKey: 'orange_api_key_x1y2z3', senderName: 'SERENITY', isActive: true, ordre: 1, maxRetries: 3, timeoutSeconds: 30 },
  { id: '2', nom: 'Clickatell Primary', providerCode: 'CLICKATELL', apiUrl: 'https://api.clickatell.com/rest', apiKey: 'click_api_key_a1b2c3', senderName: 'SERENITY', isActive: true, ordre: 2, maxRetries: 2, timeoutSeconds: 20 },
  { id: '3', nom: 'Twilio Backup', providerCode: 'TWILIO', apiUrl: 'https://api.twilio.com/2010-04-01', apiKey: 'twilio_api_key_m1n2o3', senderName: 'SERENITE', isActive: false, ordre: 3, maxRetries: 1, timeoutSeconds: 15 },
  { id: '4', nom: 'Vonage (Nexmo)', providerCode: 'VONAGE', apiUrl: 'https://api.nexmo.com/v1', apiKey: 'vonage_api_key_p4q5r6', senderName: 'SERENITY', isActive: true, ordre: 4, maxRetries: 2, timeoutSeconds: 25 },
  { id: '5', nom: 'Free Mobile SMS', providerCode: 'FREE_MOBILE', apiUrl: 'https://smsapi.free-mobile.fr/send', apiKey: 'free_api_key_s7t8u9', senderName: 'SERENITY', isActive: false, ordre: 5, maxRetries: 1, timeoutSeconds: 10 },
  { id: '6', nom: 'InfoBip Gateway', providerCode: 'INFOBIP', apiUrl: 'https://api.infobip.com/sms/2', apiKey: 'infobip_api_key_v0w1x2', senderName: 'SERENITY', isActive: true, ordre: 6, maxRetries: 3, timeoutSeconds: 30 },
  { id: '7', nom: 'MessageBird', providerCode: 'MESSAGEBIRD', apiUrl: 'https://rest.messagebird.com/messages', apiKey: 'msgbird_api_key_y3z4a5', senderName: 'SERENITE', isActive: false, ordre: 7, maxRetries: 2, timeoutSeconds: 20 },
  { id: '8', nom: 'Orange SMS Côte d\'Ivoire', providerCode: 'ORANGE_CI', apiUrl: 'https://api.orange.ci/sms', apiKey: 'orange_ci_key_b6c7d8', senderName: 'SERENITY', isActive: true, ordre: 8, maxRetries: 3, timeoutSeconds: 30 },
  { id: '9', nom: 'Route Mobile', providerCode: 'ROUTE_MOBILE', apiUrl: 'https://api.routemobile.com/sms', apiKey: 'route_api_key_e9f0g1', senderName: 'SERENITY', isActive: false, ordre: 9, maxRetries: 1, timeoutSeconds: 15 },
];

const emptyForm = { nom: '', providerCode: '', apiUrl: '', apiKey: '', senderName: '', isActive: true, ordre: 1, maxRetries: 3, timeoutSeconds: 30 };

export default function SmsGatewaysPage() {
  const [gateways, setGateways] = useState(mockGateways);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editing, setEditing] = useState<(SmsGateway & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(SmsGateway & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<SmsGateway & Record<string, unknown>>[] = [
    { key: 'nom', header: 'Nom', sortable: true },
    {
      key: 'providerCode', header: 'Provider', sortable: true,
      render: (item) => <span className="font-medium">{item.providerCode}</span>,
    },
    { key: 'senderName', header: 'Expéditeur', sortable: true },
    {
      key: 'isActive', header: 'Actif', sortable: true,
      render: (item) => <StatusBadge statut={item.isActive ? 'ACTIF' : 'INACTIF'} label={item.isActive ? 'Actif' : 'Inactif'} />,
    },
    {
      key: 'ordre', header: 'Ordre', sortable: true,
      render: (item) => <span className="text-center">{item.ordre}</span>,
    },
  ];

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (item: SmsGateway & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      nom: item.nom,
      providerCode: item.providerCode as string,
      apiUrl: item.apiUrl as string,
      apiKey: item.apiKey as string,
      senderName: item.senderName as string,
      isActive: item.isActive as boolean,
      ordre: item.ordre as number,
      maxRetries: item.maxRetries as number,
      timeoutSeconds: item.timeoutSeconds as number,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setGateways(gateways.map((g) => g.id === editing.id ? { ...g, ...form } : g));
    } else {
      const newGateway: SmsGateway & Record<string, unknown> = {
        id: String(gateways.length + 1),
        ...form,
      };
      setGateways([...gateways, newGateway]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
  };

  const handleDelete = () => {
    if (selected) {
      setGateways(gateways.filter((g) => g.id !== selected.id));
      setDeleteOpen(false);
      setSelected(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Passerelles SMS"
        description="Configuration des passerelles d'envoi SMS"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouvelle Passerelle
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={gateways}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['nom', 'providerCode']}
            searchPlaceholder="Rechercher une passerelle..."
            pageSize={10}
            exportable={true}
            exportFilename="passerelles-sms"
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
            <DialogTitle>{editing ? 'Modifier la Passerelle' : 'Nouvelle Passerelle SMS'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Nom</Label>
                <Input value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} placeholder="Nom de la passerelle" />
              </div>
              <div className="space-y-2">
                <Label>Provider</Label>
                <Input value={form.providerCode} onChange={(e) => setForm({ ...form, providerCode: e.target.value })} placeholder="ORANGE" />
              </div>
            </div>
            <div className="space-y-2">
              <Label>API URL</Label>
              <Input value={form.apiUrl} onChange={(e) => setForm({ ...form, apiUrl: e.target.value })} placeholder="https://api.example.com/sms" />
            </div>
            <div className="space-y-2">
              <Label>API Key</Label>
              <Input type="password" value={form.apiKey} onChange={(e) => setForm({ ...form, apiKey: e.target.value })} placeholder="API Key" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Expéditeur</Label>
                <Input value={form.senderName} onChange={(e) => setForm({ ...form, senderName: e.target.value })} placeholder="SERENITY" />
              </div>
              <div className="space-y-2">
                <Label>Ordre</Label>
                <Input type="number" value={form.ordre} onChange={(e) => setForm({ ...form, ordre: Number(e.target.value) })} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Max tentatives</Label>
                <Input type="number" value={form.maxRetries} onChange={(e) => setForm({ ...form, maxRetries: Number(e.target.value) })} />
              </div>
              <div className="space-y-2">
                <Label>Timeout (sec)</Label>
                <Input type="number" value={form.timeoutSeconds} onChange={(e) => setForm({ ...form, timeoutSeconds: Number(e.target.value) })} />
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
        title="Supprimer la passerelle"
        description={`Êtes-vous sûr de vouloir supprimer la passerelle "${selected?.nom}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
