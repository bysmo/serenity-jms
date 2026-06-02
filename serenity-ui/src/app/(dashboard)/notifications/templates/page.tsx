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
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { Plus, Edit, Trash2 } from 'lucide-react';
import type { EmailTemplate } from '@/types';

const mockTemplates: (EmailTemplate & Record<string, unknown>)[] = [
  { id: '1', nom: 'welcome_email', sujet: 'Bienvenue sur Serenity', corps: 'Bonjour {{prenom}}, bienvenue sur la plateforme Serenity...', type: 'BIENVENUE', actif: true, createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: '2', nom: 'payment_confirmation', sujet: 'Confirmation de paiement', corps: 'Votre paiement de {{montant}} FCFA a été confirmé...', type: 'PAIEMENT', actif: true, createdAt: '2024-01-15', updatedAt: '2024-02-10' },
  { id: '3', nom: 'cotisation_reminder', sujet: 'Rappel de cotisation', corps: 'Bonjour {{prenom}}, votre cotisation est due le {{date}}...', type: 'RAPPEL', actif: true, createdAt: '2024-02-01', updatedAt: '2024-03-05' },
  { id: '4', nom: 'nanocredit_approval', sujet: 'Nano-crédit approuvé', corps: 'Félicitations! Votre nano-crédit de {{montant}} FCFA a été approuvé...', type: 'NANO_CREDIT', actif: false, createdAt: '2024-03-10', updatedAt: '2024-03-10' },
  { id: '5', nom: 'otp_verification', sujet: 'Code de vérification', corps: 'Votre code de vérification est {{code}}. Il expire dans 5 minutes...', type: 'SECURITE', actif: true, createdAt: '2024-04-01', updatedAt: '2024-04-20' },
  { id: '6', nom: 'epargne_confirmation', sujet: 'Confirmation épargne', corps: 'Votre épargne de {{montant}} FCFA a été enregistrée avec succès...', type: 'EPARGNE', actif: true, createdAt: '2024-04-15', updatedAt: '2024-04-15' },
  { id: '7', nom: 'penalty_notification', sujet: 'Notification de pénalité', corps: 'Une pénalité de {{montant_penalite}} FCFA a été appliquée pour retard de {{jours_retard}} jours...', type: 'RAPPEL', actif: true, createdAt: '2024-05-01', updatedAt: '2024-05-10' },
  { id: '8', nom: 'disbursement_success', sujet: 'Décaissement effectué', corps: 'Un décaissement de {{montant}} FCFA a été effectué vers {{telephone}}...', type: 'PAIEMENT', actif: true, createdAt: '2024-05-15', updatedAt: '2024-05-20' },
  { id: '9', nom: 'member_activation', sujet: 'Compte activé', corps: 'Bonjour {{prenom}}, votre compte a été activé. Vous pouvez maintenant vous connecter...', type: 'BIENVENUE', actif: false, createdAt: '2024-05-20', updatedAt: '2024-05-20' },
  { id: '10', nom: 'collection_session_open', sujet: 'Session de collecte ouverte', corps: 'Une session de collecte est maintenant ouverte pour la date du {{date}}...', type: 'COLLECTE', actif: true, createdAt: '2024-06-01', updatedAt: '2024-06-01' },
  { id: '11', nom: 'password_reset', sujet: 'Réinitialisation du mot de passe', corps: 'Cliquez sur le lien suivant pour réinitialiser votre mot de passe : {{lien}}...', type: 'SECURITE', actif: true, createdAt: '2024-06-05', updatedAt: '2024-06-05' },
  { id: '12', nom: 'monthly_report', sujet: 'Rapport mensuel', corps: 'Voici votre rapport mensuel : Total cotisations {{total_cotisations}} FCFA...', type: 'RAPPORT', actif: true, createdAt: '2024-06-10', updatedAt: '2024-06-10' },
  { id: '13', nom: 'engagement_confirmation', sujet: 'Confirmation d\'engagement', corps: 'Votre engagement de {{montant}} FCFA pour la cotisation {{cotisation_libelle}} a été confirmé...', type: 'PAIEMENT', actif: false, createdAt: '2024-06-15', updatedAt: '2024-06-15' },
];

const emptyForm = { nom: '', sujet: '', corps: '', type: 'BIENVENUE', actif: true };

export default function TemplatesPage() {
  const [templates, setTemplates] = useState(mockTemplates);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editing, setEditing] = useState<(EmailTemplate & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(EmailTemplate & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<EmailTemplate & Record<string, unknown>>[] = [
    { key: 'nom', header: 'Nom', sortable: true },
    { key: 'sujet', header: 'Sujet', sortable: true },
    {
      key: 'type', header: 'Type', sortable: true,
      render: (item) => <span className="font-medium">{(item.type as string).replace(/_/g, ' ')}</span>,
    },
    {
      key: 'actif', header: 'Actif', sortable: true,
      render: (item) => <StatusBadge statut={item.actif ? 'ACTIF' : 'INACTIF'} label={item.actif ? 'Actif' : 'Inactif'} />,
    },
    { key: 'updatedAt', header: 'Modifié le', sortable: true },
  ];

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (item: EmailTemplate & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      nom: item.nom,
      sujet: item.sujet,
      corps: item.corps,
      type: item.type as string,
      actif: item.actif as boolean,
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setTemplates(templates.map((t) => t.id === editing.id ? { ...t, ...form, updatedAt: new Date().toISOString().slice(0, 10) } : t));
    } else {
      const newTemplate: EmailTemplate & Record<string, unknown> = {
        id: String(templates.length + 1),
        ...form,
        createdAt: new Date().toISOString().slice(0, 10),
        updatedAt: new Date().toISOString().slice(0, 10),
      };
      setTemplates([...templates, newTemplate]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
  };

  const handleDelete = () => {
    if (selected) {
      setTemplates(templates.filter((t) => t.id !== selected.id));
      setDeleteOpen(false);
      setSelected(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Modèles Email"
        description="Gestion des modèles d'emails"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouveau Modèle
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={templates}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['nom', 'sujet']}
            searchPlaceholder="Rechercher un modèle..."
            pageSize={10}
            exportable={true}
            exportFilename="modeles-email"
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
            <DialogTitle>{editing ? 'Modifier le Modèle' : 'Nouveau Modèle'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Nom</Label>
                <Input value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} placeholder="template_name" />
              </div>
              <div className="space-y-2">
                <Label>Type</Label>
                <Select value={form.type} onValueChange={(v) => setForm({ ...form, type: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="BIENVENUE">Bienvenue</SelectItem>
                    <SelectItem value="PAIEMENT">Paiement</SelectItem>
                    <SelectItem value="RAPPEL">Rappel</SelectItem>
                    <SelectItem value="NANO_CREDIT">Nano-crédit</SelectItem>
                    <SelectItem value="SECURITE">Sécurité</SelectItem>
                    <SelectItem value="EPARGNE">Épargne</SelectItem>
                    <SelectItem value="COLLECTE">Collecte</SelectItem>
                    <SelectItem value="RAPPORT">Rapport</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-2">
              <Label>Sujet</Label>
              <Input value={form.sujet} onChange={(e) => setForm({ ...form, sujet: e.target.value })} placeholder="Sujet de l'email" />
            </div>
            <div className="space-y-2">
              <Label>Corps</Label>
              <Textarea value={form.corps} onChange={(e) => setForm({ ...form, corps: e.target.value })} placeholder="Contenu du modèle... Utilisez {{variable}} pour les variables dynamiques" rows={8} className="font-mono text-sm" />
            </div>
            <div className="flex items-center gap-2">
              <Switch checked={form.actif} onCheckedChange={(v) => setForm({ ...form, actif: v })} />
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
        title="Supprimer le modèle"
        description={`Êtes-vous sûr de vouloir supprimer le modèle "${selected?.nom}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
