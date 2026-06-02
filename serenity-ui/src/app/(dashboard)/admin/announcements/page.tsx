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
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { Plus, Edit, Trash2 } from 'lucide-react';
import type { Annonce, AnnonceType } from '@/types';

const mockAnnouncements: (Annonce & Record<string, unknown>)[] = [
  { id: '1', titre: 'Maintenance prévue', contenu: 'Une maintenance est prévue le 15 juin de 22h à 02h. Le service sera temporairement indisponible.', dateDebut: '2024-06-10', dateFin: '2024-06-16', statut: 'ACTIVE', type: 'MAINTENANCE', ordre: 1, segment: 'ALL', createdAt: '2024-06-01' },
  { id: '2', titre: 'Nouvelle fonctionnalité : Nano-crédit', contenu: 'Découvrez notre nouvelle offre de nano-crédit avec des taux préférentiels pour les membres actifs.', dateDebut: '2024-06-01', dateFin: '2024-07-01', statut: 'ACTIVE', type: 'PROMOTION', ordre: 2, segment: 'PREMIUM', createdAt: '2024-05-20' },
  { id: '3', titre: 'Mise à jour des conditions', contenu: 'Les conditions générales d\'utilisation ont été mises à jour. Merci de les consulter.', dateDebut: '2024-05-01', dateFin: '2024-05-31', statut: 'EXPIREE', type: 'INFO', ordre: 3, segment: 'ALL', createdAt: '2024-04-25' },
  { id: '4', titre: 'Alerte sécurité', contenu: 'Nous avons détecté des tentatives de hameçonnage. Ne partagez jamais votre code PIN.', dateDebut: '2024-06-05', dateFin: '2024-06-30', statut: 'ACTIVE', type: 'ALERTE', ordre: 0, segment: 'ALL', createdAt: '2024-06-05' },
  { id: '5', titre: 'Promotion été 2024', contenu: 'Profitez de -50% sur les frais de cotisation pour toute nouvelle adhésion avant le 31 juillet.', dateDebut: '2024-07-01', dateFin: '2024-07-31', statut: 'INACTIVE', type: 'PROMOTION', ordre: 4, segment: 'STANDARD', createdAt: '2024-06-10' },
  { id: '6', titre: 'Mise à jour application mobile', contenu: 'Une nouvelle version de l\'application mobile est disponible. Mettez à jour pour profiter des dernières améliorations.', dateDebut: '2024-06-12', dateFin: '2024-07-12', statut: 'ACTIVE', type: 'INFO', ordre: 5, segment: 'ALL', createdAt: '2024-06-12' },
  { id: '7', titre: 'Fête de l\'indépendance', contenu: 'En ce jour de fête, toute l\'équipe Serenity vous souhaite une excellente célébration.', dateDebut: '2024-08-04', dateFin: '2024-08-04', statut: 'INACTIVE', type: 'INFO', ordre: 6, segment: 'ALL', createdAt: '2024-06-15' },
  { id: '8', titre: 'Alerte : retard de paiement', contenu: 'Plusieurs membres ont des retards de paiement. Des pénalités seront appliquées conformément aux conditions.', dateDebut: '2024-06-08', dateFin: '2024-06-30', statut: 'ACTIVE', type: 'ALERTE', ordre: 1, segment: 'STANDARD', createdAt: '2024-06-08' },
  { id: '9', titre: 'Offre parrainage', contenu: 'Parrainez un nouveau membre et recevez 5000 FCFA de réduction sur votre prochaine cotisation.', dateDebut: '2024-06-15', dateFin: '2024-08-15', statut: 'ACTIVE', type: 'PROMOTION', ordre: 3, segment: 'PREMIUM', createdAt: '2024-06-15' },
  { id: '10', titre: 'Maintenance serveur base de données', contenu: 'Opération de maintenance planifiée sur nos serveurs de base de données le 20 juin de 23h à 03h.', dateDebut: '2024-06-18', dateFin: '2024-06-21', statut: 'ACTIVE', type: 'MAINTENANCE', ordre: 0, segment: 'ALL', createdAt: '2024-06-16' },
  { id: '11', titre: 'Nouvelle réglementation', contenu: 'Conformément à la nouvelle réglementation BCEAO, les conditions de nano-crédit ont été mises à jour.', dateDebut: '2024-05-15', dateFin: '2024-05-30', statut: 'EXPIREE', type: 'INFO', ordre: 7, segment: 'ALL', createdAt: '2024-05-14' },
  { id: '12', titre: 'Fin d\'année : bilan épargne', contenu: 'Consultez votre bilan épargne de fin d\'année dans votre espace personnel.', dateDebut: '2024-12-20', dateFin: '2024-12-31', statut: 'INACTIVE', type: 'INFO', ordre: 8, segment: 'PREMIUM', createdAt: '2024-06-18' },
  { id: '13', titre: 'Alerte : tentative de fraude', contenu: 'Des tentatives de fraude ont été signalées. Vérifiez vos transactions et signalez toute activité suspecte.', dateDebut: '2024-06-19', dateFin: '2024-07-19', statut: 'ACTIVE', type: 'ALERTE', ordre: 0, segment: 'ALL', createdAt: '2024-06-19' },
  { id: '14', titre: 'Promotion rentrée scolaire', contenu: 'Bénéficiez de taux préférentiels sur les nano-crédits pour la rentrée scolaire.', dateDebut: '2024-09-01', dateFin: '2024-09-30', statut: 'INACTIVE', type: 'PROMOTION', ordre: 9, segment: 'STANDARD', createdAt: '2024-06-20' },
  { id: '15', titre: 'Mise à jour politique de confidentialité', contenu: 'Notre politique de confidentialité a été mise à jour. Merci de la consulter dans vos paramètres.', dateDebut: '2024-06-20', dateFin: '2024-07-20', statut: 'ACTIVE', type: 'INFO', ordre: 10, segment: 'ALL', createdAt: '2024-06-20' },
  { id: '16', titre: 'Maintenance réseau télécoms', contenu: 'Interruption possible des services SMS le 25 juin de 02h à 06h.', dateDebut: '2024-06-23', dateFin: '2024-06-26', statut: 'INACTIVE', type: 'MAINTENANCE', ordre: 2, segment: 'ALL', createdAt: '2024-06-21' },
];

const TYPE_LABELS: Record<string, string> = { INFO: 'Information', ALERTE: 'Alerte', PROMOTION: 'Promotion', MAINTENANCE: 'Maintenance' };

const emptyForm = { titre: '', contenu: '', type: 'INFO' as AnnonceType, dateDebut: '', dateFin: '', ordre: 0, segment: 'ALL' };

export default function AnnouncementsPage() {
  const [announcements, setAnnouncements] = useState(mockAnnouncements);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [editing, setEditing] = useState<(Annonce & Record<string, unknown>) | null>(null);
  const [selected, setSelected] = useState<(Annonce & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState(emptyForm);

  const columns: Column<Annonce & Record<string, unknown>>[] = [
    { key: 'titre', header: 'Titre', sortable: true },
    {
      key: 'type', header: 'Type', sortable: true,
      render: (item) => <span className="font-medium">{TYPE_LABELS[item.type as string] || item.type}</span>,
    },
    {
      key: 'statut', header: 'Statut', sortable: true,
      render: (item) => <StatusBadge statut={item.statut as string} />,
    },
    { key: 'dateDebut', header: 'Date début', sortable: true },
    { key: 'dateFin', header: 'Date fin', sortable: true },
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

  const openEdit = (item: Annonce & Record<string, unknown>) => {
    setEditing(item);
    setForm({
      titre: item.titre,
      contenu: item.contenu,
      type: item.type as AnnonceType,
      dateDebut: item.dateDebut,
      dateFin: item.dateFin,
      ordre: item.ordre as number,
      segment: (item.segment as string) || 'ALL',
    });
    setDialogOpen(true);
  };

  const handleSave = () => {
    if (editing) {
      setAnnouncements(announcements.map((a) => a.id === editing.id ? { ...a, ...form } : a));
    } else {
      const newAnnouncement: Annonce & Record<string, unknown> = {
        id: String(announcements.length + 1),
        ...form,
        statut: 'ACTIVE',
        createdAt: new Date().toISOString().slice(0, 10),
      };
      setAnnouncements([...announcements, newAnnouncement]);
    }
    setDialogOpen(false);
    setForm(emptyForm);
    setEditing(null);
  };

  const handleDelete = () => {
    if (selected) {
      setAnnouncements(announcements.filter((a) => a.id !== selected.id));
      setDeleteOpen(false);
      setSelected(null);
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Annonces"
        description="Gestion des annonces et communications"
        actions={
          <Button size="sm" className="gap-2" onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nouvelle Annonce
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={announcements}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['titre']}
            searchPlaceholder="Rechercher une annonce..."
            pageSize={10}
            exportable={true}
            exportFilename="annonces"
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
            <DialogTitle>{editing ? 'Modifier l\'Annonce' : 'Nouvelle Annonce'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Titre</Label>
              <Input value={form.titre} onChange={(e) => setForm({ ...form, titre: e.target.value })} placeholder="Titre de l'annonce" />
            </div>
            <div className="space-y-2">
              <Label>Contenu</Label>
              <Textarea value={form.contenu} onChange={(e) => setForm({ ...form, contenu: e.target.value })} placeholder="Contenu de l'annonce..." rows={4} />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Type</Label>
                <Select value={form.type} onValueChange={(v) => setForm({ ...form, type: v as AnnonceType })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="INFO">Information</SelectItem>
                    <SelectItem value="ALERTE">Alerte</SelectItem>
                    <SelectItem value="PROMOTION">Promotion</SelectItem>
                    <SelectItem value="MAINTENANCE">Maintenance</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Segment</Label>
                <Select value={form.segment} onValueChange={(v) => setForm({ ...form, segment: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ALL">Tous</SelectItem>
                    <SelectItem value="PREMIUM">Premium</SelectItem>
                    <SelectItem value="STANDARD">Standard</SelectItem>
                    <SelectItem value="BASIQUE">Basique</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="grid grid-cols-3 gap-4">
              <div className="space-y-2">
                <Label>Date début</Label>
                <Input type="date" value={form.dateDebut} onChange={(e) => setForm({ ...form, dateDebut: e.target.value })} />
              </div>
              <div className="space-y-2">
                <Label>Date fin</Label>
                <Input type="date" value={form.dateFin} onChange={(e) => setForm({ ...form, dateFin: e.target.value })} />
              </div>
              <div className="space-y-2">
                <Label>Ordre</Label>
                <Input type="number" value={form.ordre} onChange={(e) => setForm({ ...form, ordre: Number(e.target.value) })} />
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
        title="Supprimer l'annonce"
        description={`Êtes-vous sûr de vouloir supprimer l'annonce "${selected?.titre}" ? Cette action est irréversible.`}
        confirmLabel="Supprimer"
        variant="destructive"
        onConfirm={handleDelete}
      />
    </div>
  );
}
