'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import { Send, Eye } from 'lucide-react';
import type { NotificationLog } from '@/types';

const mockLogs: (NotificationLog & Record<string, unknown>)[] = [
  { id: '1', type: 'EMAIL', recipientId: 'MEM-001', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Bienvenue sur Serenity', content: 'Bienvenue...', status: 'SENT', sentAt: '2024-06-01 09:00', createdAt: '2024-06-01 09:00' },
  { id: '2', type: 'SMS', recipientId: 'MEM-002', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Votre code OTP est 123456', status: 'DELIVERED', sentAt: '2024-06-01 10:15', createdAt: '2024-06-01 10:15' },
  { id: '3', type: 'PUSH', recipientId: 'MEM-003', recipientType: 'MEMBRE', channel: 'PUSH', subject: 'Rappel cotisation', content: 'Votre cotisation est due...', status: 'FAILED', errorMessage: 'Device not registered', sentAt: undefined, createdAt: '2024-06-02 08:30' },
  { id: '4', type: 'EMAIL', recipientId: 'MEM-004', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Confirmation de paiement', content: 'Votre paiement a été confirmé...', status: 'SENT', sentAt: '2024-06-02 11:00', createdAt: '2024-06-02 11:00' },
  { id: '5', type: 'SMS', recipientId: 'MEM-005', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Rappel: cotisation dans 2 jours', status: 'PENDING', sentAt: undefined, createdAt: '2024-06-03 07:00' },
  { id: '6', type: 'EMAIL', recipientId: 'ADM-001', recipientType: 'ADMIN', channel: 'EMAIL', subject: 'Rapport quotidien', content: 'Résumé des transactions du jour...', status: 'SENT', sentAt: '2024-06-03 18:00', createdAt: '2024-06-03 18:00' },
  { id: '7', type: 'PUSH', recipientId: 'MEM-006', recipientType: 'MEMBRE', channel: 'PUSH', subject: 'Nano-crédit approuvé', content: 'Votre nano-crédit a été approuvé...', status: 'DELIVERED', sentAt: '2024-06-04 09:30', createdAt: '2024-06-04 09:30' },
  { id: '8', type: 'SMS', recipientId: 'MEM-007', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Paiement de 25000 FCFA reçu', status: 'DELIVERED', sentAt: '2024-06-04 14:20', createdAt: '2024-06-04 14:20' },
  { id: '9', type: 'EMAIL', recipientId: 'MEM-008', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Mise à jour des conditions', content: 'Nous avons mis à jour...', status: 'FAILED', errorMessage: 'Mailbox full', sentAt: undefined, createdAt: '2024-06-05 10:00' },
  { id: '10', type: 'SMS', recipientId: 'COL-001', recipientType: 'COLLECTEUR', channel: 'SMS', subject: '', content: 'Session de collecte ouverte', status: 'SENT', sentAt: '2024-06-05 08:00', createdAt: '2024-06-05 08:00' },
  { id: '11', type: 'EMAIL', recipientId: 'MEM-009', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Reçu de cotisation', content: 'Votre reçu de cotisation mensuelle...', status: 'DELIVERED', sentAt: '2024-06-05 12:15', createdAt: '2024-06-05 12:15' },
  { id: '12', type: 'SMS', recipientId: 'MEM-010', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Votre épargne de 15000 FCFA a été créditée', status: 'DELIVERED', sentAt: '2024-06-06 09:45', createdAt: '2024-06-06 09:45' },
  { id: '13', type: 'PUSH', recipientId: 'MEM-011', recipientType: 'MEMBRE', channel: 'PUSH', subject: 'Nouvelle cotisation disponible', content: 'Une nouvelle cotisation est disponible...', status: 'SENT', sentAt: '2024-06-06 11:00', createdAt: '2024-06-06 11:00' },
  { id: '14', type: 'EMAIL', recipientId: 'ADM-002', recipientType: 'ADMIN', channel: 'EMAIL', subject: 'Alerte : échec de paiement', content: 'Un paiement de 75000 FCFA a échoué...', status: 'SENT', sentAt: '2024-06-07 08:30', createdAt: '2024-06-07 08:30' },
  { id: '15', type: 'SMS', recipientId: 'MEM-012', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Code OTP : 789012. Expire dans 5 min', status: 'FAILED', errorMessage: 'Invalid phone number', sentAt: undefined, createdAt: '2024-06-07 10:00' },
  { id: '16', type: 'EMAIL', recipientId: 'MEM-013', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Bienvenue sur Serenity', content: 'Bienvenue sur notre plateforme...', status: 'DELIVERED', sentAt: '2024-06-07 14:20', createdAt: '2024-06-07 14:20' },
  { id: '17', type: 'PUSH', recipientId: 'MEM-014', recipientType: 'MEMBRE', channel: 'PUSH', subject: 'Remboursement nano-crédit', content: 'Votre remboursement est dû dans 3 jours...', status: 'DELIVERED', sentAt: '2024-06-08 07:00', createdAt: '2024-06-08 07:00' },
  { id: '18', type: 'SMS', recipientId: 'COL-002', recipientType: 'COLLECTEUR', channel: 'SMS', subject: '', content: 'Rappel: clôture de session à 17h', status: 'SENT', sentAt: '2024-06-08 15:00', createdAt: '2024-06-08 15:00' },
  { id: '19', type: 'EMAIL', recipientId: 'MEM-015', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Confirmation engagement', content: 'Votre engagement de 50000 FCFA...', status: 'PENDING', sentAt: undefined, createdAt: '2024-06-09 08:00' },
  { id: '20', type: 'SMS', recipientId: 'MEM-016', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Retrait de 20000 FCFA effectué', status: 'DELIVERED', sentAt: '2024-06-09 11:30', createdAt: '2024-06-09 11:30' },
  { id: '21', type: 'PUSH', recipientId: 'ADM-001', recipientType: 'ADMIN', channel: 'PUSH', subject: 'Nouveau membre inscrit', content: 'Un nouveau membre vient de s\'inscrire...', status: 'SENT', sentAt: '2024-06-09 16:45', createdAt: '2024-06-09 16:45' },
  { id: '22', type: 'EMAIL', recipientId: 'MEM-017', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Pénalité de retard', content: 'Une pénalité de 500 FCFA a été appliquée...', status: 'FAILED', errorMessage: 'SMTP timeout', sentAt: undefined, createdAt: '2024-06-10 09:00' },
  { id: '23', type: 'SMS', recipientId: 'MEM-018', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Solde épargne : 125000 FCFA', status: 'DELIVERED', sentAt: '2024-06-10 10:30', createdAt: '2024-06-10 10:30' },
  { id: '24', type: 'EMAIL', recipientId: 'MEM-019', recipientType: 'MEMBRE', channel: 'EMAIL', subject: 'Invitation à rejoindre un groupe', content: 'Vous avez été invité à rejoindre...', status: 'DELIVERED', sentAt: '2024-06-10 14:00', createdAt: '2024-06-10 14:00' },
  { id: '25', type: 'PUSH', recipientId: 'MEM-020', recipientType: 'MEMBRE', channel: 'PUSH', subject: 'Cotisation remboursée', content: 'Votre cotisation a été remboursée...', status: 'PENDING', sentAt: undefined, createdAt: '2024-06-11 08:15' },
  { id: '26', type: 'SMS', recipientId: 'MEM-021', recipientType: 'MEMBRE', channel: 'SMS', subject: '', content: 'Confirmation : 35000 FCFA reçus', status: 'SENT', sentAt: '2024-06-11 09:00', createdAt: '2024-06-11 09:00' },
  { id: '27', type: 'EMAIL', recipientId: 'ADM-003', recipientType: 'ADMIN', channel: 'EMAIL', subject: 'Rapport hebdomadaire', content: 'Résumé des activités de la semaine...', status: 'DELIVERED', sentAt: '2024-06-11 18:00', createdAt: '2024-06-11 18:00' },
];

const TYPE_LABELS: Record<string, string> = { EMAIL: 'Email', SMS: 'SMS', PUSH: 'Push', IN_APP: 'In-App' };
const CHANNEL_LABELS: Record<string, string> = { EMAIL: 'Email', SMS: 'SMS', PUSH: 'Push', IN_APP: 'In-App' };

export default function NotificationsPage() {
  const [logs, setLogs] = useState(mockLogs);
  const [sendOpen, setSendOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [selectedLog, setSelectedLog] = useState<(NotificationLog & Record<string, unknown>) | null>(null);
  const [form, setForm] = useState({ type: 'EMAIL', recipientId: '', channel: 'EMAIL', subject: '', content: '' });

  const columns: Column<NotificationLog & Record<string, unknown>>[] = [
    {
      key: 'type', header: 'Type', sortable: true,
      render: (item) => <span className="font-medium">{TYPE_LABELS[item.type as string] || item.type}</span>,
    },
    { key: 'recipientId', header: 'Destinataire', sortable: true },
    {
      key: 'channel', header: 'Canal', sortable: true,
      render: (item) => <span>{CHANNEL_LABELS[item.channel as string] || item.channel}</span>,
    },
    {
      key: 'subject', header: 'Sujet',
      render: (item) => <span className="max-w-[200px] truncate block">{(item.subject as string) || '-'}</span>,
    },
    {
      key: 'status', header: 'Statut', sortable: true,
      render: (item) => <StatusBadge statut={item.status as string} />,
    },
    {
      key: 'sentAt', header: 'Date envoi', sortable: true,
      render: (item) => <span>{item.sentAt || '-'}</span>,
    },
  ];

  const handleSend = () => {
    const newLog: NotificationLog & Record<string, unknown> = {
      id: String(logs.length + 1),
      type: form.type as NotificationLog['type'],
      recipientId: form.recipientId,
      recipientType: 'MEMBRE',
      channel: form.channel as NotificationLog['channel'],
      subject: form.subject,
      content: form.content,
      status: 'PENDING',
      createdAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
    };
    setLogs([newLog, ...logs]);
    setSendOpen(false);
    setForm({ type: 'EMAIL', recipientId: '', channel: 'EMAIL', subject: '', content: '' });
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Journal des Notifications"
        description="Historique et envoi de notifications"
        actions={
          <Button size="sm" className="gap-2" onClick={() => setSendOpen(true)}>
            <Send className="h-4 w-4" />
            Envoyer
          </Button>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={logs}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['recipientId', 'subject']}
            searchPlaceholder="Rechercher une notification..."
            pageSize={10}
            selectable={true}
            exportable={true}
            exportFilename="notifications"
            filters={[
              {
                key: 'status',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'PENDING' },
                  { label: 'Envoyé', value: 'SENT' },
                  { label: 'Échoué', value: 'FAILED' },
                  { label: 'Délivré', value: 'DELIVERED' },
                ],
              },
              {
                key: 'type',
                label: 'Type',
                options: [
                  { label: 'Email', value: 'EMAIL' },
                  { label: 'SMS', value: 'SMS' },
                  { label: 'Push', value: 'PUSH' },
                ],
              },
              {
                key: 'channel',
                label: 'Canal',
                options: [
                  { label: 'Email', value: 'EMAIL' },
                  { label: 'SMS', value: 'SMS' },
                  { label: 'Push', value: 'PUSH' },
                ],
              },
            ]}
            actions={(item) => [
              { label: 'Détails', onClick: () => { setSelectedLog(item); setDetailOpen(true); } },
            ]}
          />
        </CardContent>
      </Card>

      {/* Send Notification Dialog */}
      <Dialog open={sendOpen} onOpenChange={setSendOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Envoyer une Notification</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Type</Label>
                <Select value={form.type} onValueChange={(v) => setForm({ ...form, type: v, channel: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="EMAIL">Email</SelectItem>
                    <SelectItem value="SMS">SMS</SelectItem>
                    <SelectItem value="PUSH">Push</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Canal</Label>
                <Select value={form.channel} onValueChange={(v) => setForm({ ...form, channel: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="EMAIL">Email</SelectItem>
                    <SelectItem value="SMS">SMS</SelectItem>
                    <SelectItem value="PUSH">Push</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-2">
              <Label>ID Destinataire</Label>
              <Input value={form.recipientId} onChange={(e) => setForm({ ...form, recipientId: e.target.value })} placeholder="MEM-001" />
            </div>
            <div className="space-y-2">
              <Label>Sujet</Label>
              <Input value={form.subject} onChange={(e) => setForm({ ...form, subject: e.target.value })} placeholder="Sujet de la notification" />
            </div>
            <div className="space-y-2">
              <Label>Contenu</Label>
              <Textarea value={form.content} onChange={(e) => setForm({ ...form, content: e.target.value })} placeholder="Contenu de la notification..." rows={4} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setSendOpen(false)}>Annuler</Button>
            <Button onClick={handleSend} className="gap-2">
              <Send className="h-4 w-4" />
              Envoyer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Detail Dialog */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Eye className="h-5 w-5" />
              Détails de la Notification
            </DialogTitle>
          </DialogHeader>
          {selectedLog && (
            <div className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <p className="text-sm text-muted-foreground">Type</p>
                  <p className="font-medium">{TYPE_LABELS[selectedLog.type as string]}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Canal</p>
                  <p className="font-medium">{CHANNEL_LABELS[selectedLog.channel as string]}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Destinataire</p>
                  <p className="font-medium">{selectedLog.recipientId}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Statut</p>
                  <StatusBadge statut={selectedLog.status as string} />
                </div>
              </div>
              {selectedLog.subject && (
                <div>
                  <p className="text-sm text-muted-foreground">Sujet</p>
                  <p className="font-medium">{selectedLog.subject}</p>
                </div>
              )}
              <div>
                <p className="text-sm text-muted-foreground">Contenu</p>
                <p className="text-sm mt-1 p-3 bg-muted rounded-md">{selectedLog.content}</p>
              </div>
              {selectedLog.errorMessage && (
                <div>
                  <p className="text-sm text-muted-foreground">Erreur</p>
                  <p className="text-sm text-destructive mt-1">{selectedLog.errorMessage}</p>
                </div>
              )}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <p className="text-sm text-muted-foreground">Date création</p>
                  <p className="font-medium">{selectedLog.createdAt}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Date envoi</p>
                  <p className="font-medium">{selectedLog.sentAt || '-'}</p>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
