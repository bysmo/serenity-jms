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
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Plus, X, Check } from 'lucide-react';
import type { CollecteSession, Collecte, TypeCollecte } from '@/types';
import { formatCurrency } from '@/lib/constants';

const mockSessions: (CollecteSession & Record<string, unknown>)[] = [
  { id: '1', userId: 'COL-001', dateSession: '2024-06-15', statut: 'OUVERT', montantOuverture: 50000, openedAt: '2024-06-15 08:00', createdAt: '2024-06-15' },
  { id: '2', userId: 'COL-001', dateSession: '2024-06-14', statut: 'FERME', montantOuverture: 50000, montantFermeture: 120000, openedAt: '2024-06-14 08:00', closedAt: '2024-06-14 16:30', createdAt: '2024-06-14' },
  { id: '3', userId: 'COL-001', dateSession: '2024-06-13', statut: 'RECONCILIE', montantOuverture: 30000, montantFermeture: 95000, openedAt: '2024-06-13 08:00', closedAt: '2024-06-13 17:00', createdAt: '2024-06-13' },
  { id: '4', userId: 'COL-001', dateSession: '2024-06-12', statut: 'FERME', montantOuverture: 25000, montantFermeture: 87500, openedAt: '2024-06-12 08:30', closedAt: '2024-06-12 16:00', createdAt: '2024-06-12' },
  { id: '5', userId: 'COL-001', dateSession: '2024-06-11', statut: 'RECONCILIE', montantOuverture: 40000, montantFermeture: 135000, openedAt: '2024-06-11 07:45', closedAt: '2024-06-11 17:15', createdAt: '2024-06-11' },
  { id: '6', userId: 'COL-001', dateSession: '2024-06-10', statut: 'FERME', montantOuverture: 35000, montantFermeture: 102000, openedAt: '2024-06-10 08:00', closedAt: '2024-06-10 16:45', createdAt: '2024-06-10' },
  { id: '7', userId: 'COL-001', dateSession: '2024-06-09', statut: 'RECONCILIE', montantOuverture: 45000, montantFermeture: 148000, openedAt: '2024-06-09 08:15', closedAt: '2024-06-09 17:00', createdAt: '2024-06-09' },
  { id: '8', userId: 'COL-001', dateSession: '2024-06-08', statut: 'FERME', montantOuverture: 20000, montantFermeture: 67000, openedAt: '2024-06-08 08:30', closedAt: '2024-06-08 16:00', createdAt: '2024-06-08' },
  { id: '9', userId: 'COL-001', dateSession: '2024-06-07', statut: 'RECONCILIE', montantOuverture: 55000, montantFermeture: 175000, openedAt: '2024-06-07 07:30', closedAt: '2024-06-07 17:30', createdAt: '2024-06-07' },
  { id: '10', userId: 'COL-001', dateSession: '2024-06-06', statut: 'FERME', montantOuverture: 30000, montantFermeture: 88000, openedAt: '2024-06-06 08:00', closedAt: '2024-06-06 16:15', createdAt: '2024-06-06' },
  { id: '11', userId: 'COL-001', dateSession: '2024-06-05', statut: 'RECONCILIE', montantOuverture: 40000, montantFermeture: 112000, openedAt: '2024-06-05 08:00', closedAt: '2024-06-05 17:00', createdAt: '2024-06-05' },
  { id: '12', userId: 'COL-001', dateSession: '2024-06-04', statut: 'FERME', montantOuverture: 25000, montantFermeture: 73000, openedAt: '2024-06-04 08:30', closedAt: '2024-06-04 16:00', createdAt: '2024-06-04' },
  { id: '13', userId: 'COL-001', dateSession: '2024-06-03', statut: 'RECONCILIE', montantOuverture: 60000, montantFermeture: 195000, openedAt: '2024-06-03 07:30', closedAt: '2024-06-03 17:30', createdAt: '2024-06-03' },
  { id: '14', userId: 'COL-001', dateSession: '2024-06-02', statut: 'FERME', montantOuverture: 35000, montantFermeture: 98000, openedAt: '2024-06-02 08:00', closedAt: '2024-06-02 16:30', createdAt: '2024-06-02' },
  { id: '15', userId: 'COL-001', dateSession: '2024-06-01', statut: 'RECONCILIE', montantOuverture: 50000, montantFermeture: 162000, openedAt: '2024-06-01 08:00', closedAt: '2024-06-01 17:00', createdAt: '2024-06-01' },
  { id: '16', userId: 'COL-001', dateSession: '2024-05-31', statut: 'FERME', montantOuverture: 30000, montantFermeture: 85000, openedAt: '2024-05-31 08:15', closedAt: '2024-05-31 16:00', createdAt: '2024-05-31' },
];

const mockCollectes: (Collecte & Record<string, unknown>)[] = [
  { id: '1', collecteSessionId: '1', membreId: '1', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: true, confirmedAt: '2024-06-15 09:30', referenceTransaction: 'REF-001', createdAt: '2024-06-15 09:25' },
  { id: '2', collecteSessionId: '1', membreId: '2', typeCollecte: 'EPARGNE', montant: 10000, isConfirmed: true, confirmedAt: '2024-06-15 10:15', referenceTransaction: 'REF-002', createdAt: '2024-06-15 10:10' },
  { id: '3', collecteSessionId: '1', membreId: '4', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 11:00' },
  { id: '4', collecteSessionId: '1', membreId: '5', typeCollecte: 'NANO_CREDIT', montant: 15000, isConfirmed: true, confirmedAt: '2024-06-15 12:00', referenceTransaction: 'REF-003', createdAt: '2024-06-15 11:50' },
  { id: '5', collecteSessionId: '1', membreId: '3', typeCollecte: 'COTISATION', montant: 7500, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 13:20' },
  { id: '6', collecteSessionId: '1', membreId: '6', typeCollecte: 'EPARGNE', montant: 20000, isConfirmed: true, confirmedAt: '2024-06-15 14:00', referenceTransaction: 'REF-004', createdAt: '2024-06-15 13:55' },
  { id: '7', collecteSessionId: '1', membreId: '7', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: true, confirmedAt: '2024-06-15 14:30', referenceTransaction: 'REF-005', createdAt: '2024-06-15 14:25' },
  { id: '8', collecteSessionId: '1', membreId: '8', typeCollecte: 'NANO_CREDIT', montant: 10000, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 15:00' },
  { id: '9', collecteSessionId: '1', membreId: '9', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: true, confirmedAt: '2024-06-15 15:30', referenceTransaction: 'REF-006', createdAt: '2024-06-15 15:25' },
  { id: '10', collecteSessionId: '1', membreId: '10', typeCollecte: 'EPARGNE', montant: 25000, isConfirmed: true, confirmedAt: '2024-06-15 16:00', referenceTransaction: 'REF-007', createdAt: '2024-06-15 15:50' },
  { id: '11', collecteSessionId: '1', membreId: '11', typeCollecte: 'COTISATION', montant: 7500, isConfirmed: true, confirmedAt: '2024-06-15 16:15', referenceTransaction: 'REF-008', createdAt: '2024-06-15 16:10' },
  { id: '12', collecteSessionId: '1', membreId: '12', typeCollecte: 'NANO_CREDIT', montant: 20000, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 16:30' },
  { id: '13', collecteSessionId: '1', membreId: '13', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: true, confirmedAt: '2024-06-15 16:45', referenceTransaction: 'REF-009', createdAt: '2024-06-15 16:40' },
  { id: '14', collecteSessionId: '1', membreId: '14', typeCollecte: 'EPARGNE', montant: 15000, isConfirmed: true, confirmedAt: '2024-06-15 17:00', referenceTransaction: 'REF-010', createdAt: '2024-06-15 16:55' },
  { id: '15', collecteSessionId: '1', membreId: '15', typeCollecte: 'COTISATION', montant: 10000, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 17:10' },
  { id: '16', collecteSessionId: '1', membreId: '16', typeCollecte: 'NANO_CREDIT', montant: 30000, isConfirmed: true, confirmedAt: '2024-06-15 17:20', referenceTransaction: 'REF-011', createdAt: '2024-06-15 17:15' },
  { id: '17', collecteSessionId: '1', membreId: '17', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: true, confirmedAt: '2024-06-15 17:30', referenceTransaction: 'REF-012', createdAt: '2024-06-15 17:28' },
  { id: '18', collecteSessionId: '1', membreId: '18', typeCollecte: 'EPARGNE', montant: 30000, isConfirmed: true, confirmedAt: '2024-06-15 17:40', referenceTransaction: 'REF-013', createdAt: '2024-06-15 17:35' },
  { id: '19', collecteSessionId: '1', membreId: '19', typeCollecte: 'COTISATION', montant: 7500, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 17:45' },
  { id: '20', collecteSessionId: '1', membreId: '20', typeCollecte: 'NANO_CREDIT', montant: 12000, isConfirmed: true, confirmedAt: '2024-06-15 17:55', referenceTransaction: 'REF-014', createdAt: '2024-06-15 17:50' },
  { id: '21', collecteSessionId: '1', membreId: '21', typeCollecte: 'COTISATION', montant: 5000, isConfirmed: true, confirmedAt: '2024-06-15 18:00', referenceTransaction: 'REF-015', createdAt: '2024-06-15 17:58' },
  { id: '22', collecteSessionId: '1', membreId: '22', typeCollecte: 'EPARGNE', montant: 18000, isConfirmed: false, referenceTransaction: undefined, createdAt: '2024-06-15 18:05' },
];

const memberNames: Record<string, string> = {
  '1': 'Diop Aminata', '2': 'Ndiaye Fatou', '3': 'Sow Mamadou',
  '4': 'Ba Ibrahima', '5': 'Diallo Aissatou', '6': 'Fall Ousmane',
  '7': 'Sy Mariama', '8': 'Gueye Moussa', '9': 'Seck Khady',
  '10': 'Mbaye Cheikh', '11': 'Faye Awa', '12': 'Sarr Birame',
  '13': 'Cissé Fatoumata', '14': 'Kane Assane', '15': 'Thiam Bineta',
  '16': 'Lo Abdoulaye', '17': 'Dieng Coumba', '18': 'Niang Lamine',
  '19': 'Wane Rama', '20': 'Bâ Idrissa', '21': 'Dia Sokhna',
  '22': 'Sylla Modou',
};

const TYPE_LABELS: Record<string, string> = {
  COTISATION: 'Cotisation',
  EPARGNE: 'Épargne',
  NANO_CREDIT: 'Nano-crédit',
};

export default function CollectorPage() {
  const [sessions, setSessions] = useState(mockSessions);
  const [collectes, setCollectes] = useState(mockCollectes);
  const [openSessionOpen, setOpenSessionOpen] = useState(false);
  const [closeSessionOpen, setCloseSessionOpen] = useState(false);
  const [confirmCollecteOpen, setConfirmCollecteOpen] = useState(false);
  const [selectedCollecte, setSelectedCollecte] = useState<(Collecte & Record<string, unknown>) | null>(null);
  const [openAmount, setOpenAmount] = useState(50000);
  const [selectedSession, setSelectedSession] = useState<string | null>('1');

  const activeSession = sessions.find((s) => s.statut === 'OUVERT');
  const activeCollectes = collectes.filter((c) => c.collecteSessionId === (selectedSession || activeSession?.id));
  const totalConfirmed = activeCollectes.filter((c) => c.isConfirmed).reduce((sum, c) => sum + (c.montant as number), 0);
  const totalAll = activeCollectes.reduce((sum, c) => sum + (c.montant as number), 0);

  const handleOpenSession = () => {
    const newSession: CollecteSession & Record<string, unknown> = {
      id: String(sessions.length + 1),
      userId: 'COL-001',
      dateSession: new Date().toISOString().split('T')[0],
      statut: 'OUVERT',
      montantOuverture: openAmount,
      openedAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
      createdAt: new Date().toISOString().split('T')[0],
    };
    setSessions([newSession, ...sessions]);
    setSelectedSession(newSession.id);
    setOpenSessionOpen(false);
    setOpenAmount(50000);
  };

  const handleCloseSession = () => {
    if (activeSession) {
      const totalAmount = (activeSession.montantOuverture as number) + totalConfirmed;
      setSessions(sessions.map((s) =>
        s.id === activeSession.id
          ? { ...s, statut: 'FERME' as const, montantFermeture: totalAmount, closedAt: new Date().toISOString().slice(0, 16).replace('T', ' ') }
          : s
      ));
      setCloseSessionOpen(false);
    }
  };

  const handleConfirmCollecte = () => {
    if (selectedCollecte) {
      setCollectes(collectes.map((c) =>
        c.id === selectedCollecte.id
          ? { ...c, isConfirmed: true, confirmedAt: new Date().toISOString().slice(0, 16).replace('T', ' '), referenceTransaction: `REF-${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}` }
          : c
      ));
      setConfirmCollecteOpen(false);
      setSelectedCollecte(null);
    }
  };

  const sessionColumns: Column<CollecteSession & Record<string, unknown>>[] = [
    { key: 'dateSession', header: 'Date', sortable: true },
    {
      key: 'statut', header: 'Statut', sortable: true,
      render: (item) => <StatusBadge statut={item.statut as string} />,
    },
    {
      key: 'montantOuverture', header: 'Montant Ouverture', sortable: true,
      render: (item) => <span className="font-medium">{formatCurrency(item.montantOuverture as number)}</span>,
    },
    {
      key: 'montantFermeture', header: 'Montant Fermeture', sortable: true,
      render: (item) => <span>{item.montantFermeture ? formatCurrency(item.montantFermeture as number) : '-'}</span>,
    },
    { key: 'openedAt', header: 'Ouvert le', sortable: true },
    {
      key: 'closedAt', header: 'Fermé le',
      render: (item) => <span>{item.closedAt || '-'}</span>,
    },
  ];

  const collecteColumns: Column<Collecte & Record<string, unknown>>[] = [
    {
      key: 'membreId', header: 'Membre', sortable: true,
      render: (item) => <span className="font-medium">{memberNames[item.membreId as string] || item.membreId}</span>,
    },
    {
      key: 'typeCollecte', header: 'Type', sortable: true,
      render: (item) => <Badge variant="outline">{TYPE_LABELS[item.typeCollecte as string] || item.typeCollecte}</Badge>,
    },
    {
      key: 'montant', header: 'Montant', sortable: true,
      render: (item) => <span className="font-medium">{formatCurrency(item.montant as number)}</span>,
    },
    {
      key: 'isConfirmed', header: 'Confirmé', sortable: true,
      render: (item) => (
        <span className="inline-flex items-center gap-1">
          {item.isConfirmed ? (
            <><Check className="h-4 w-4 text-emerald-500" /> Oui</>
          ) : (
            <><X className="h-4 w-4 text-amber-500" /> Non</>
          )}
        </span>
      ),
    },
    {
      key: 'referenceTransaction', header: 'Référence',
      render: (item) => <span className="font-mono text-xs">{item.referenceTransaction || '-'}</span>,
    },
  ];

  return (
    <div className="space-y-6">
      <PageHeader
        title="Collecteur"
        description="Gestion des sessions de collecte"
        actions={
          !activeSession ? (
            <Button size="sm" className="gap-2" onClick={() => setOpenSessionOpen(true)}>
              <Plus className="h-4 w-4" />
              Ouvrir une session
            </Button>
          ) : (
            <Button variant="destructive" size="sm" className="gap-2" onClick={() => setCloseSessionOpen(true)}>
              <X className="h-4 w-4" />
              Fermer la session
            </Button>
          )
        }
      />

      {/* Active Session Card */}
      {activeSession && (
        <Card className="border-emerald-200 bg-emerald-50/50 dark:border-emerald-800 dark:bg-emerald-950/20">
          <CardHeader>
            <CardTitle className="text-base flex items-center gap-2">
              <div className="h-3 w-3 rounded-full bg-emerald-500 animate-pulse" />
              Session Active — {activeSession.dateSession}
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-4">
              <div>
                <p className="text-sm text-muted-foreground">Montant Ouverture</p>
                <p className="font-medium">{formatCurrency(activeSession.montantOuverture as number)}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Statut</p>
                <StatusBadge statut={activeSession.statut as string} />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Total Confirmé</p>
                <p className="font-medium text-emerald-600 dark:text-emerald-400">{formatCurrency(totalConfirmed)}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Total (toutes)</p>
                <p className="font-medium">{formatCurrency(totalAll)}</p>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Session List */}
      <Card>
        <CardContent className="p-6">
          <h3 className="text-base font-semibold mb-4">Liste des Sessions</h3>
          <DataTable
            data={sessions}
            columns={sessionColumns}
            keyExtractor={(item) => item.id}
            searchable={false}
            pageSize={10}
            exportable={true}
            exportFilename="sessions-collecte"
            actions={(item) => [
              {
                label: 'Voir collectes',
                onClick: () => setSelectedSession(item.id),
              },
            ]}
          />
        </CardContent>
      </Card>

      {/* Collectes for selected session */}
      {selectedSession && (
        <Card>
          <CardContent className="p-6">
            <h3 className="text-base font-semibold mb-4">
              Collectes — Session du {sessions.find((s) => s.id === selectedSession)?.dateSession}
            </h3>
            <DataTable
              data={activeCollectes}
              columns={collecteColumns}
              keyExtractor={(item) => item.id}
              searchable={false}
              pageSize={10}
              actions={(item) => {
                const actions: { label: string; onClick: () => void; variant?: 'default' | 'destructive' }[] = [];
                if (!item.isConfirmed) {
                  actions.push({
                    label: 'Confirmer',
                    onClick: () => { setSelectedCollecte(item); setConfirmCollecteOpen(true); },
                  });
                  actions.push({
                    label: 'Supprimer',
                    onClick: () => {
                      setCollectes(collectes.filter((c) => c.id !== item.id));
                    },
                    variant: 'destructive',
                  });
                }
                return actions;
              }}
            />
          </CardContent>
        </Card>
      )}

      {/* Open Session Dialog */}
      <Dialog open={openSessionOpen} onOpenChange={setOpenSessionOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Ouvrir une session de collecte</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Montant d&apos;ouverture (FCFA)</Label>
              <Input
                type="number"
                value={openAmount || ''}
                onChange={(e) => setOpenAmount(Number(e.target.value))}
                placeholder="50000"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpenSessionOpen(false)}>Annuler</Button>
            <Button onClick={handleOpenSession}>Ouvrir</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Confirm Collecte Dialog */}
      <ConfirmDialog
        open={confirmCollecteOpen}
        onOpenChange={setConfirmCollecteOpen}
        title="Confirmer la collecte"
        description={`Confirmer la collecte de ${selectedCollecte ? formatCurrency(selectedCollecte.montant as number) : ''} pour ${selectedCollecte ? memberNames[selectedCollecte.membreId as string] || selectedCollecte.membreId : ''} ?`}
        confirmLabel="Confirmer"
        onConfirm={handleConfirmCollecte}
      />

      {/* Close Session Dialog */}
      <ConfirmDialog
        open={closeSessionOpen}
        onOpenChange={setCloseSessionOpen}
        title="Fermer la session"
        description="Êtes-vous sûr de vouloir fermer cette session de collecte ? Les collectes non confirmées seront perdues."
        confirmLabel="Fermer"
        variant="destructive"
        onConfirm={handleCloseSession}
      />
    </div>
  );
}
