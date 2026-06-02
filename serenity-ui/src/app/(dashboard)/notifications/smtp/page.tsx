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
import type { SmtpConfiguration } from '@/types';

const mockConfigs: (SmtpConfiguration & Record<string, unknown>)[] = [
  {
    id: '1', host: 'smtp.gmail.com', port: 587, username: 'notifications@serenity.sn', password: 'app_password_x1y2z3', authEnabled: true, starttlsEnabled: true, sslEnabled: false, fromEmail: 'notifications@serenity.sn', fromName: 'Serenity JMS', actif: true,
  },
  {
    id: '2', host: 'mail.serenity.sn', port: 465, username: 'admin@serenity.sn', password: 'smtp_pass_a1b2c3', authEnabled: true, starttlsEnabled: false, sslEnabled: true, fromEmail: 'admin@serenity.sn', fromName: 'Serenity Admin', actif: false,
  },
  {
    id: '3', host: 'smtp.office365.com', port: 587, username: 'support@serenity.sn', password: 'office365_pass_d4e5f6', authEnabled: true, starttlsEnabled: true, sslEnabled: false, fromEmail: 'support@serenity.sn', fromName: 'Serenity Support', actif: false,
  },
  {
    id: '4', host: 'smtp.sendgrid.net', port: 587, username: 'apikey', password: 'sendgrid_api_key_g7h8i9', authEnabled: true, starttlsEnabled: true, sslEnabled: false, fromEmail: 'noreply@serenity.sn', fromName: 'Serenity No-Reply', actif: false,
  },
  {
    id: '5', host: 'smtp.mailgun.org', port: 587, username: 'postmaster@mg.serenity.sn', password: 'mailgun_pass_j0k1l2', authEnabled: true, starttlsEnabled: true, sslEnabled: false, fromEmail: 'mail@serenity.sn', fromName: 'Serenity Mail', actif: false,
  },
  {
    id: '6', host: 'mail2.serenity.sn', port: 25, username: 'system@serenity.sn', password: 'internal_pass_m3n4o5', authEnabled: false, starttlsEnabled: false, sslEnabled: false, fromEmail: 'system@serenity.sn', fromName: 'Serenity System', actif: false,
  },
  {
    id: '7', host: 'smtp.aws.serenity.sn', port: 465, username: 'aws-smtp@serenity.sn', password: 'aws_smtp_key_p6q7r8', authEnabled: true, starttlsEnabled: false, sslEnabled: true, fromEmail: 'aws@serenity.sn', fromName: 'Serenity AWS', actif: false,
  },
];

const emptyForm = { host: '', port: 587, username: '', password: '', authEnabled: true, starttlsEnabled: true, sslEnabled: false, fromEmail: '', fromName: '', actif: true };

export default function SmtpPage() {
  const [configs, setConfigs] = useState(mockConfigs);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editing, setEditing] = useState<(SmtpConfiguration & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(SmtpConfiguration & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<SmtpConfiguration & Record<string, unknown>>[] = [
    { key: 'host', header: 'Hôte', sortable: true },
    {
      key: 'port', header: 'Port', sortable: true,
      render: (item) => <span>{item.port}</span>,
    },
    { key: 'username', header: 'Utilisateur', sortable: true },
    { key: 'fromEmail', header: 'Email Exp.', sortable: true },
    {
      key: 'actif', header: 'Actif', sortable: true,
      render: (item) => <StatusBadge statut={item.actif ? 'ACTIF' : 'INACTIF'} label={item.actif ? 'Actif' : 'Inactif'} />,
    },
  ];

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (item: SmtpConfiguration & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      host: item.host,
      port: item.port as number,
      username: item.username,
      password: item.password,
      authEnabled: item.authEnabled as boolean,
      starttlsEnabled: item.starttlsEnabled as boolean,
      sslEnabled: item.sslEnabled as boolean,
      fromEmail: item.fromEmail,
      fromName: item.fromName,
      actif: item.actif as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setConfigs(configs.map((c) => c.id === editing.id ? { ...c, ...form } : c));
    } else {
      const newConfig: SmtpConfiguration & Record<string, unknown> = {
        id: String(configs.length + 1),
        ...form,
      };
      setConfigs([...configs, newConfig]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
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
        title="Configuration SMTP"
        description="Gestion des configurations d'envoi d'emails"
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
            searchKeys={['host', 'username']}
            searchPlaceholder="Rechercher une configuration..."
            pageSize={10}
            exportable={true}
            exportFilename="smtp-configs"
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
            <DialogTitle>{editing ? 'Modifier la Configuration' : 'Nouvelle Configuration SMTP'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-3 gap-4">
              <div className="col-span-2 space-y-2">
                <Label>Hôte</Label>
                <Input value={form.host} onChange={(e) => setForm({ ...form, host: e.target.value })} placeholder="smtp.example.com" />
              </div>
              <div className="space-y-2">
                <Label>Port</Label>
                <Input type="number" value={form.port} onChange={(e) => setForm({ ...form, port: Number(e.target.value) })} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Utilisateur</Label>
                <Input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} placeholder="user@example.com" />
              </div>
              <div className="space-y-2">
                <Label>Mot de passe</Label>
                <Input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Email Expéditeur</Label>
                <Input type="email" value={form.fromEmail} onChange={(e) => setForm({ ...form, fromEmail: e.target.value })} placeholder="noreply@example.com" />
              </div>
              <div className="space-y-2">
                <Label>Nom Expéditeur</Label>
                <Input value={form.fromName} onChange={(e) => setForm({ ...form, fromName: e.target.value })} placeholder="Serenity JMS" />
              </div>
            </div>
            <div className="flex flex-wrap gap-6">
              <div className="flex items-center gap-2">
                <Switch checked={form.authEnabled} onCheckedChange={(v) => setForm({ ...form, authEnabled: v })} />
                <Label>Authentification</Label>
              </div>
              <div className="flex items-center gap-2">
                <Switch checked={form.starttlsEnabled} onCheckedChange={(v) => setForm({ ...form, starttlsEnabled: v })} />
                <Label>STARTTLS</Label>
              </div>
              <div className="flex items-center gap-2">
                <Switch checked={form.sslEnabled} onCheckedChange={(v) => setForm({ ...form, sslEnabled: v })} />
                <Label>SSL</Label>
              </div>
              <div className="flex items-center gap-2">
                <Switch checked={form.actif} onCheckedChange={(v) => setForm({ ...form, actif: v })} />
                <Label>Actif</Label>
              </div>
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
        title="Supprimer la configuration"
        description="Êtes-vous sûr de vouloir supprimer cette configuration SMTP ? Cette action est irréversible."
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
