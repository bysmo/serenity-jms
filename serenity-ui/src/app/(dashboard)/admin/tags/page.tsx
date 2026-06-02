'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { ConfirmDialog } from '@/components/shared/confirm-dialog';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Plus, Edit, Trash2 } from 'lucide-react';
import type { Tag } from '@/types';

const mockTags: (Tag & Record<string, unknown>)[] = [
  { id: '1', nom: 'Urgent', type: 'COTISATION', description: 'Cotisation nécessitant une attention immédiate', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: '2', nom: 'Premium', type: 'MEMBRE', description: 'Membre premium', createdAt: '2024-01-15', updatedAt: '2024-01-15' },
  { id: '3', nom: 'Social', type: 'COTISATION', description: 'Cotisation à caractère social', createdAt: '2024-02-01', updatedAt: '2024-02-01' },
  { id: '4', nom: 'Nouveau', type: 'MEMBRE', description: 'Nouveau membre', createdAt: '2024-02-15', updatedAt: '2024-02-15' },
  { id: '5', nom: 'Automatique', type: 'EPARGNE', description: 'Épargne automatique', createdAt: '2024-03-01', updatedAt: '2024-03-01' },
  { id: '6', nom: 'Rappel', type: 'NOTIFICATION', description: 'Notification de rappel', createdAt: '2024-03-15', updatedAt: '2024-03-15' },
  { id: '7', nom: 'En retard', type: 'NANO_CREDIT', description: 'Nano-crédit en retard de paiement', createdAt: '2024-03-20', updatedAt: '2024-04-01' },
  { id: '8', nom: 'VIP', type: 'MEMBRE', description: 'Membre VIP avec avantages exclusifs', createdAt: '2024-04-01', updatedAt: '2024-04-01' },
  { id: '9', nom: 'Annuelle', type: 'COTISATION', description: 'Cotisation annuelle', createdAt: '2024-04-10', updatedAt: '2024-04-10' },
  { id: '10', nom: 'Bloqué', type: 'MEMBRE', description: 'Membre dont le compte est bloqué', createdAt: '2024-04-15', updatedAt: '2024-05-01' },
  { id: '11', nom: 'Programmée', type: 'EPARGNE', description: 'Épargne programmée récurrente', createdAt: '2024-05-01', updatedAt: '2024-05-01' },
  { id: '12', nom: 'Promo', type: 'COTISATION', description: 'Cotisation en promotion', createdAt: '2024-05-10', updatedAt: '2024-05-15' },
  { id: '13', nom: 'Sécurité', type: 'NOTIFICATION', description: 'Alerte de sécurité', createdAt: '2024-05-20', updatedAt: '2024-05-20' },
  { id: '14', nom: 'Approuvé', type: 'NANO_CREDIT', description: 'Nano-crédit approuvé en attente de déboursement', createdAt: '2024-06-01', updatedAt: '2024-06-01' },
  { id: '15', nom: 'Mensuelle', type: 'COTISATION', description: 'Cotisation mensuelle standard', createdAt: '2024-06-05', updatedAt: '2024-06-05' },
  { id: '16', nom: 'Inactif', type: 'MEMBRE', description: 'Membre inactif depuis plus de 3 mois', createdAt: '2024-06-10', updatedAt: '2024-06-10' },
];

const TAG_TYPES = ['COTISATION', 'MEMBRE', 'EPARGNE', 'NANO_CREDIT', 'NOTIFICATION'];

const emptyForm = { nom: '', type: 'COTISATION', description: '' };

export default function TagsPage() {
  const [tags, setTags] = useState(mockTags);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editing, setEditing] = useState<(Tag & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(Tag & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<Tag & Record<string, unknown>>[] = [
    {
      key: 'nom', header: 'Nom', sortable: true,
      render: (item) => <span className="font-medium">{item.nom}</span>,
    },
    {
      key: 'type', header: 'Type', sortable: true,
      render: (item) => <Badge variant="outline">{(item.type as string).replace(/_/g, ' ')}</Badge>,
    },
    { key: 'description', header: 'Description' },
    { key: 'updatedAt', header: 'Modifié le', sortable: true },
  ];

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (item: Tag & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      nom: item.nom,
      type: item.type as string,
      description: (item.description as string) || '',
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setTags(tags.map((t) => t.id === editing.id ? { ...t, ...form, updatedAt: new Date().toISOString().slice(0, 10) } : t));
    } else {
      const newTag: Tag & Record<string, unknown> = {
        id: String(tags.length + 1),
        ...form,
        createdAt: new Date().toISOString().slice(0, 10),
        updatedAt: new Date().toISOString().slice(0, 10),
      };
      setTags([...tags, newTag]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
  };

  const handleDelete = () => {
    if (selected) {
      setTags(tags.filter((t) => t.id !== selected.id));
      setDeleteOpen(false);
      setSelected(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Tags"
        description="Gestion des tags de catégorisation"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouveau Tag
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={tags}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['nom', 'description']}
            searchPlaceholder="Rechercher un tag..."
            pageSize={10}
            exportable={true}
            exportFilename="tags"
            filters={[
              {
                key: 'type',
                label: 'Type',
                options: TAG_TYPES.map((t) => ({ label: t.replace(/_/g, ' '), value: t })),
              },
            ]}
            actions={(item) => [
              { label: 'Modifier', onClick: () => openEdit(item) },
              { label: 'Supprimer', onClick: () => { setSelected(item); setDeleteOpen(true); }, variant: 'destructive' },
            ]}
          />
        </CardContent>
      </Card>

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editing ? 'Modifier le Tag' : 'Nouveau Tag'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Nom</Label>
              <Input value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} placeholder="Nom du tag" />
            </div>
            <div className="space-y-2">
              <Label>Type</Label>
              <Select value={form.type} onValueChange={(v) => setForm({ ...form, type: v })}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  {TAG_TYPES.map((t) => (
                    <SelectItem key={t} value={t}>{t.replace(/_/g, ' ')}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Description</Label>
              <Input value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Description du tag" />
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
        title="Supprimer le tag"
        description={`Êtes-vous sûr de vouloir supprimer le tag "${selected?.nom}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
