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
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import type { NanoCredit, NanoCreditStatut, NanoCreditEcheance, NanoCreditVersement } from '@/types';
import { formatCurrency } from '@/lib/constants';

// --- Extended row type with display fields for sorting ---

type CreditRow = NanoCredit & {
  membreNom: string;
  palierNom: string;
} & Record<string, unknown>;

// --- Mock data ---------------------------------------------------------------

const initialCredits: CreditRow[] = [
  // DEMANDE_EN_ATTENTE (4)
  {
    id: '1', membreId: '1', palierId: '1', membreNom: 'Diop Aminata', palierNom: 'Palier 1 — Découverte', montant: 50000, statut: 'DEMANDE_EN_ATTENTE', createdAt: '2024-06-01T09:00:00Z',
  },
  {
    id: '2', membreId: '2', palierId: '1', membreNom: 'Ndiaye Fatou', palierNom: 'Palier 1 — Découverte', montant: 45000, statut: 'DEMANDE_EN_ATTENTE', createdAt: '2024-06-05T14:30:00Z',
  },
  {
    id: '3', membreId: '25', palierId: '2', membreNom: 'Konaté Fatoumata', palierNom: 'Palier 2 — Essentiel', montant: 130000, statut: 'DEMANDE_EN_ATTENTE', createdAt: '2024-12-01T10:00:00Z',
  },
  {
    id: '4', membreId: '26', palierId: '3', membreNom: 'Traore Modibo', palierNom: 'Palier 3 — Premium', montant: 250000, statut: 'DEMANDE_EN_ATTENTE', createdAt: '2024-12-10T08:00:00Z',
  },

  // EN_ETUDE (3)
  {
    id: '5', membreId: '3', palierId: '2', membreNom: 'Sow Mamadou', palierNom: 'Palier 2 — Essentiel', montant: 120000, statut: 'EN_ETUDE', scoreGlobal: 62, createdAt: '2024-05-28T11:00:00Z',
  },
  {
    id: '6', membreId: '27', palierId: '1', membreNom: 'Dembelé Sekou', palierNom: 'Palier 1 — Découverte', montant: 40000, statut: 'EN_ETUDE', scoreGlobal: 55, createdAt: '2024-11-20T09:30:00Z',
  },
  {
    id: '7', membreId: '28', palierId: '3', membreNom: 'Bah Aissatou', palierNom: 'Palier 3 — Premium', montant: 280000, statut: 'EN_ETUDE', scoreGlobal: 70, createdAt: '2024-11-25T15:00:00Z',
  },

  // ACCEPTE (3)
  {
    id: '8', membreId: '4', palierId: '2', membreNom: 'Ba Ibrahima', palierNom: 'Palier 2 — Essentiel', montant: 100000, statut: 'ACCEPTE', scoreGlobal: 85, createdAt: '2024-05-20T08:00:00Z',
  },
  {
    id: '9', membreId: '29', palierId: '1', membreNom: 'Camara Ibrahim', palierNom: 'Palier 1 — Découverte', montant: 48000, statut: 'ACCEPTE', scoreGlobal: 78, createdAt: '2024-10-15T10:00:00Z',
  },
  {
    id: '10', membreId: '30', palierId: '2', membreNom: 'Toure Kadiatou', palierNom: 'Palier 2 — Essentiel', montant: 140000, statut: 'ACCEPTE', scoreGlobal: 82, createdAt: '2024-11-01T14:00:00Z',
  },

  // REFUSE (3)
  {
    id: '11', membreId: '5', palierId: '1', membreNom: 'Diallo Aissatou', palierNom: 'Palier 1 — Découverte', montant: 50000, statut: 'REFUSE', scoreGlobal: 35, createdAt: '2024-05-15T16:00:00Z',
  },
  {
    id: '12', membreId: '31', palierId: '3', membreNom: 'Wade Moustapha', palierNom: 'Palier 3 — Premium', montant: 300000, statut: 'REFUSE', scoreGlobal: 28, createdAt: '2024-09-20T11:00:00Z',
  },
  {
    id: '13', membreId: '32', palierId: '2', membreNom: 'Faye Awa', palierNom: 'Palier 2 — Essentiel', montant: 110000, statut: 'REFUSE', scoreGlobal: 40, createdAt: '2024-10-05T09:00:00Z',
  },

  // DEBOURSE (3)
  {
    id: '14', membreId: '6', palierId: '3', membreNom: 'Fall Ousmane', palierNom: 'Palier 3 — Premium', montant: 200000, statut: 'DEBOURSE', scoreGlobal: 72, dateOctroi: '2024-04-10', createdAt: '2024-04-01T10:00:00Z',
  },
  {
    id: '15', membreId: '33', palierId: '1', membreNom: 'Seck Boubacar', palierNom: 'Palier 1 — Découverte', montant: 50000, statut: 'DEBOURSE', scoreGlobal: 75, dateOctroi: '2024-11-01', createdAt: '2024-10-20T10:00:00Z',
  },
  {
    id: '16', membreId: '34', palierId: '2', membreNom: 'Balde Mariama', palierNom: 'Palier 2 — Essentiel', montant: 145000, statut: 'DEBOURSE', scoreGlobal: 68, dateOctroi: '2024-11-10', createdAt: '2024-10-25T10:00:00Z',
  },

  // EN_REMBOURSEMENT (3)
  {
    id: '17', membreId: '8', palierId: '2', membreNom: 'Gueye Moussa', palierNom: 'Palier 2 — Essentiel', montant: 100000, statut: 'EN_REMBOURSEMENT', scoreGlobal: 78, dateOctroi: '2024-03-05', createdAt: '2024-03-01T10:00:00Z',
  },
  {
    id: '18', membreId: '35', palierId: '1', membreNom: 'Diao Abdoul', palierNom: 'Palier 1 — Découverte', montant: 35000, statut: 'EN_REMBOURSEMENT', scoreGlobal: 80, dateOctroi: '2024-08-15', createdAt: '2024-08-01T10:00:00Z',
  },
  {
    id: '19', membreId: '36', palierId: '3', membreNom: 'Samb Rokhaya', palierNom: 'Palier 3 — Premium', montant: 250000, statut: 'EN_REMBOURSEMENT', scoreGlobal: 73, dateOctroi: '2024-07-20', createdAt: '2024-07-10T10:00:00Z',
  },

  // REMBOURSE (2)
  {
    id: '20', membreId: '9', palierId: '1', membreNom: 'Sarr Abdoulaye', palierNom: 'Palier 1 — Découverte', montant: 30000, statut: 'REMBOURSE', scoreGlobal: 90, dateOctroi: '2024-01-20', createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: '21', membreId: '37', palierId: '2', membreNom: 'Diouf Aminata', palierNom: 'Palier 2 — Essentiel', montant: 95000, statut: 'REMBOURSE', scoreGlobal: 88, dateOctroi: '2024-02-01', createdAt: '2024-01-20T10:00:00Z',
  },

  // EN_RETARD (3)
  {
    id: '22', membreId: '7', palierId: '1', membreNom: 'Sy Mariama', palierNom: 'Palier 1 — Découverte', montant: 50000, statut: 'EN_RETARD', scoreGlobal: 55, dateOctroi: '2024-02-10', joursRetard: 12, createdAt: '2024-02-01T10:00:00Z',
  },
  {
    id: '23', membreId: '38', palierId: '2', membreNom: 'Niang Cheikh', palierNom: 'Palier 2 — Essentiel', montant: 120000, statut: 'EN_RETARD', scoreGlobal: 48, dateOctroi: '2024-05-01', joursRetard: 8, createdAt: '2024-04-20T10:00:00Z',
  },
  {
    id: '24', membreId: '39', palierId: '1', membreNom: 'Cisse Youssouf', palierNom: 'Palier 1 — Découverte', montant: 45000, statut: 'EN_RETARD', scoreGlobal: 42, dateOctroi: '2024-06-15', joursRetard: 20, createdAt: '2024-06-01T10:00:00Z',
  },

  // ANNULE (2)
  {
    id: '25', membreId: '10', palierId: '3', membreNom: 'Mbaye Khady', palierNom: 'Palier 3 — Premium', montant: 250000, statut: 'ANNULE', createdAt: '2024-05-25T10:00:00Z',
  },
  {
    id: '26', membreId: '40', palierId: '1', membreNom: 'Lo Mamadou', palierNom: 'Palier 1 — Découverte', montant: 20000, statut: 'ANNULE', createdAt: '2024-08-10T10:00:00Z',
  },
];

// Mock echeances for nano credits
const mockNanoEcheances: Record<string, NanoCreditEcheance[]> = {
  '14': [
    { id: 'ne14-1', nanoCreditId: '14', numeroEcheance: 1, montant: 70000, dateEcheance: '2024-05-10', statut: 'PAYEE', datePaiement: '2024-05-10', montantPaye: 70000 },
    { id: 'ne14-2', nanoCreditId: '14', numeroEcheance: 2, montant: 70000, dateEcheance: '2024-06-10', statut: 'EN_ATTENTE' },
    { id: 'ne14-3', nanoCreditId: '14', numeroEcheance: 3, montant: 70000, dateEcheance: '2024-07-10', statut: 'EN_ATTENTE' },
  ],
  '17': [
    { id: 'ne17-1', nanoCreditId: '17', numeroEcheance: 1, montant: 25000, dateEcheance: '2024-04-05', statut: 'PAYEE', datePaiement: '2024-04-05', montantPaye: 25000 },
    { id: 'ne17-2', nanoCreditId: '17', numeroEcheance: 2, montant: 25000, dateEcheance: '2024-05-05', statut: 'PAYEE', datePaiement: '2024-05-06', montantPaye: 25000 },
    { id: 'ne17-3', nanoCreditId: '17', numeroEcheance: 3, montant: 25000, dateEcheance: '2024-06-05', statut: 'EN_ATTENTE' },
    { id: 'ne17-4', nanoCreditId: '17', numeroEcheance: 4, montant: 25000, dateEcheance: '2024-07-05', statut: 'EN_ATTENTE' },
  ],
  '22': [
    { id: 'ne22-1', nanoCreditId: '22', numeroEcheance: 1, montant: 17500, dateEcheance: '2024-03-10', statut: 'PAYEE', datePaiement: '2024-03-10', montantPaye: 17500 },
    { id: 'ne22-2', nanoCreditId: '22', numeroEcheance: 2, montant: 17500, dateEcheance: '2024-04-10', statut: 'EN_RETARD', montantPenalite: 500 },
    { id: 'ne22-3', nanoCreditId: '22', numeroEcheance: 3, montant: 17500, dateEcheance: '2024-05-10', statut: 'EN_ATTENTE' },
  ],
  '20': [
    { id: 'ne20-1', nanoCreditId: '20', numeroEcheance: 1, montant: 10500, dateEcheance: '2024-02-20', statut: 'PAYEE', datePaiement: '2024-02-20', montantPaye: 10500 },
    { id: 'ne20-2', nanoCreditId: '20', numeroEcheance: 2, montant: 10500, dateEcheance: '2024-03-20', statut: 'PAYEE', datePaiement: '2024-03-20', montantPaye: 10500 },
    { id: 'ne20-3', nanoCreditId: '20', numeroEcheance: 3, montant: 10500, dateEcheance: '2024-04-20', statut: 'PAYEE', datePaiement: '2024-04-20', montantPaye: 10500 },
  ],
  '23': [
    { id: 'ne23-1', nanoCreditId: '23', numeroEcheance: 1, montant: 30000, dateEcheance: '2024-06-01', statut: 'PAYEE', datePaiement: '2024-06-01', montantPaye: 30000 },
    { id: 'ne23-2', nanoCreditId: '23', numeroEcheance: 2, montant: 30000, dateEcheance: '2024-07-01', statut: 'EN_RETARD', montantPenalite: 750 },
    { id: 'ne23-3', nanoCreditId: '23', numeroEcheance: 3, montant: 30000, dateEcheance: '2024-08-01', statut: 'EN_ATTENTE' },
    { id: 'ne23-4', nanoCreditId: '23', numeroEcheance: 4, montant: 30000, dateEcheance: '2024-09-01', statut: 'EN_ATTENTE' },
  ],
  '18': [
    { id: 'ne18-1', nanoCreditId: '18', numeroEcheance: 1, montant: 12000, dateEcheance: '2024-09-15', statut: 'PAYEE', datePaiement: '2024-09-15', montantPaye: 12000 },
    { id: 'ne18-2', nanoCreditId: '18', numeroEcheance: 2, montant: 12000, dateEcheance: '2024-10-15', statut: 'EN_ATTENTE' },
    { id: 'ne18-3', nanoCreditId: '18', numeroEcheance: 3, montant: 11000, dateEcheance: '2024-11-15', statut: 'EN_ATTENTE' },
  ],
  '19': [
    { id: 'ne19-1', nanoCreditId: '19', numeroEcheance: 1, montant: 85000, dateEcheance: '2024-08-20', statut: 'PAYEE', datePaiement: '2024-08-20', montantPaye: 85000 },
    { id: 'ne19-2', nanoCreditId: '19', numeroEcheance: 2, montant: 85000, dateEcheance: '2024-09-20', statut: 'PAYEE', datePaiement: '2024-09-20', montantPaye: 85000 },
    { id: 'ne19-3', nanoCreditId: '19', numeroEcheance: 3, montant: 80000, dateEcheance: '2024-10-20', statut: 'EN_ATTENTE' },
  ],
};

// Mock versements for nano credits
const mockNanoVersements: Record<string, NanoCreditVersement[]> = {
  '14': [
    { id: 'nv14-1', nanoCreditId: '14', montant: 70000, dateVersement: '2024-05-10', modePaiement: 'ORANGE_MONEY', reference: 'OM-20240510-001' },
  ],
  '17': [
    { id: 'nv17-1', nanoCreditId: '17', montant: 25000, dateVersement: '2024-04-05', modePaiement: 'WAVE', reference: 'WV-20240405-001' },
    { id: 'nv17-2', nanoCreditId: '17', montant: 25000, dateVersement: '2024-05-06', modePaiement: 'ESPECES', reference: 'ESP-20240506-001' },
  ],
  '22': [
    { id: 'nv22-1', nanoCreditId: '22', montant: 17500, dateVersement: '2024-03-10', modePaiement: 'ORANGE_MONEY', reference: 'OM-20240310-002' },
  ],
  '20': [
    { id: 'nv20-1', nanoCreditId: '20', montant: 10500, dateVersement: '2024-02-20', modePaiement: 'WAVE', reference: 'WV-20240220-001' },
    { id: 'nv20-2', nanoCreditId: '20', montant: 10500, dateVersement: '2024-03-20', modePaiement: 'WAVE', reference: 'WV-20240320-001' },
    { id: 'nv20-3', nanoCreditId: '20', montant: 10500, dateVersement: '2024-04-20', modePaiement: 'ORANGE_MONEY', reference: 'OM-20240420-001' },
  ],
  '18': [
    { id: 'nv18-1', nanoCreditId: '18', montant: 12000, dateVersement: '2024-09-15', modePaiement: 'WAVE', reference: 'WV-20240915-001' },
  ],
  '19': [
    { id: 'nv19-1', nanoCreditId: '19', montant: 85000, dateVersement: '2024-08-20', modePaiement: 'ORANGE_MONEY', reference: 'OM-20240820-001' },
    { id: 'nv19-2', nanoCreditId: '19', montant: 85000, dateVersement: '2024-09-20', modePaiement: 'VIREMENT', reference: 'VIR-20240920-001' },
  ],
};

// --- Statut filter options ----------------------------------------------------

const STATUT_OPTIONS: { label: string; value: NanoCreditStatut }[] = [
  { label: 'Demande en attente', value: 'DEMANDE_EN_ATTENTE' },
  { label: 'En étude', value: 'EN_ETUDE' },
  { label: 'Accepté', value: 'ACCEPTE' },
  { label: 'Refusé', value: 'REFUSE' },
  { label: 'Déboursé', value: 'DEBOURSE' },
  { label: 'En remboursement', value: 'EN_REMBOURSEMENT' },
  { label: 'Remboursé', value: 'REMBOURSE' },
  { label: 'En retard', value: 'EN_RETARD' },
  { label: 'Annulé', value: 'ANNULE' },
];

// --- Table columns -----------------------------------------------------------

const columns: Column<CreditRow>[] = [
  {
    key: 'membreNom',
    header: 'Membre',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{item.membreNom as string}</span>
    ),
  },
  {
    key: 'palierNom',
    header: 'Palier',
    sortable: true,
    render: (item) => (
      <span className="text-sm">{item.palierNom as string}</span>
    ),
  },
  {
    key: 'montant',
    header: 'Montant',
    sortable: true,
    render: (item) => (
      <span className="font-medium">{formatCurrency(item.montant as number)}</span>
    ),
  },
  {
    key: 'statut',
    header: 'Statut',
    sortable: true,
    render: (item) => <StatusBadge statut={item.statut as string} />,
  },
  {
    key: 'scoreGlobal',
    header: 'Score Global',
    sortable: true,
    render: (item) => {
      const score = item.scoreGlobal as number | undefined;
      if (score == null) return <span>—</span>;
      const color =
        score >= 70
          ? 'text-emerald-600'
          : score >= 50
            ? 'text-amber-600'
            : 'text-red-600';
      return <span className={`font-medium ${color}`}>{score}/100</span>;
    },
  },
  {
    key: 'createdAt',
    header: 'Date',
    sortable: true,
    render: (item) => <span>{new Date(item.createdAt as string).toLocaleDateString('fr-FR')}</span>,
  },
];

// --- Component ---------------------------------------------------------------

export default function NanoCreditsPage() {
  const [credits, setCredits] = useState<CreditRow[]>(initialCredits);

  // Dialogs
  const [etudeOpen, setEtudeOpen] = useState(false);
  const [refuseOpen, setRefuseOpen] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [echeancesOpen, setEcheancesOpen] = useState(false);
  const [cancelOpen, setCancelOpen] = useState(false);

  const [selectedCredit, setSelectedCredit] = useState<CreditRow | null>(null);
  const [actionType, setActionType] = useState<string>('');
  const [motif, setMotif] = useState('');
  const [etudeData, setEtudeData] = useState({ scoreAi: 0, scoreHumain: 0, commentaire: '' });

  // --- Handlers ---

  const updateCreditStatut = (id: string, statut: NanoCreditStatut, extra?: Partial<NanoCredit>) => {
    setCredits(
      credits.map((c) =>
        c.id === id ? { ...c, statut, ...extra } : c
      )
    );
  };

  const handleEtude = () => {
    if (selectedCredit) {
      const scoreAi = etudeData.scoreAi;
      const scoreHumain = etudeData.scoreHumain;
      const scoreGlobal = Math.round((scoreAi + scoreHumain) / 2);
      updateCreditStatut(selectedCredit.id as string, 'EN_ETUDE', {
        scoreAi,
        scoreHumain,
        scoreGlobal,
      });
      setEtudeOpen(false);
      setSelectedCredit(null);
      setEtudeData({ scoreAi: 0, scoreHumain: 0, commentaire: '' });
    }
  };

  const handleRefuse = () => {
    if (selectedCredit) {
      updateCreditStatut(selectedCredit.id as string, 'REFUSE');
      setRefuseOpen(false);
      setSelectedCredit(null);
      setMotif('');
    }
  };

  const handleConfirmAction = () => {
    if (!selectedCredit) return;
    const id = selectedCredit.id as string;

    switch (actionType) {
      case 'accorder':
        updateCreditStatut(id, 'ACCEPTE');
        break;
      case 'debourser':
        updateCreditStatut(id, 'DEBOURSE', { dateOctroi: new Date().toISOString() });
        break;
      case 'annuler':
        updateCreditStatut(id, 'ANNULE');
        break;
    }

    setConfirmOpen(false);
    setSelectedCredit(null);
    setActionType('');
  };

  const handleCancel = () => {
    if (selectedCredit) {
      updateCreditStatut(selectedCredit.id as string, 'ANNULE');
      setCancelOpen(false);
      setSelectedCredit(null);
    }
  };

  const openEcheancesDialog = (item: CreditRow) => {
    setSelectedCredit(item);
    setEcheancesOpen(true);
  };

  // --- Actions builder ---

  const getActions = (item: CreditRow) => {
    const statut = item.statut as NanoCreditStatut;
    const actions: { label: string; onClick: () => void; variant?: 'default' | 'destructive' }[] =
      [];

    if (statut === 'DEMANDE_EN_ATTENTE') {
      actions.push({
        label: 'Étudier',
        onClick: () => {
          setSelectedCredit(item);
          setEtudeData({ scoreAi: 0, scoreHumain: 0, commentaire: '' });
          setEtudeOpen(true);
        },
      });
    }

    if (statut === 'EN_ETUDE') {
      actions.push({
        label: 'Accorder',
        onClick: () => {
          setSelectedCredit(item);
          setActionType('accorder');
          setConfirmOpen(true);
        },
      });
      actions.push({
        label: 'Refuser',
        onClick: () => {
          setSelectedCredit(item);
          setMotif('');
          setRefuseOpen(true);
        },
        variant: 'destructive',
      });
    }

    if (statut === 'ACCEPTE') {
      actions.push({
        label: 'Débourser',
        onClick: () => {
          setSelectedCredit(item);
          setActionType('debourser');
          setConfirmOpen(true);
        },
      });
    }

    if (statut === 'EN_REMBOURSEMENT' || statut === 'EN_RETARD') {
      actions.push({
        label: 'Voir échéances/versements',
        onClick: () => openEcheancesDialog(item),
      });
    }

    // Any statut can be cancelled if not already terminal
    if (!['REMBOURSE', 'ANNULE', 'REFUSE'].includes(statut)) {
      actions.push({
        label: 'Annuler',
        onClick: () => {
          setSelectedCredit(item);
          setCancelOpen(true);
        },
        variant: 'destructive',
      });
    }

    return actions;
  };

  // --- Echeances & versements data for dialog ---

  const currentEcheances = selectedCredit
    ? mockNanoEcheances[selectedCredit.id as string] || []
    : [];
  const currentVersements = selectedCredit
    ? mockNanoVersements[selectedCredit.id as string] || []
    : [];

  // --- Confirm labels ---

  const confirmTitle: Record<string, string> = {
    accorder: 'Accorder le nano-crédit',
    debourser: 'Débourser le crédit',
    annuler: 'Annuler le crédit',
  };
  const confirmDescription: Record<string, string> = {
    accorder: 'Êtes-vous sûr de vouloir accorder ce nano-crédit ?',
    debourser: 'Confirmer le décaissement de ce nano-crédit ?',
    annuler: 'Êtes-vous sûr de vouloir annuler ce nano-crédit ?',
  };
  const confirmLabel: Record<string, string> = {
    accorder: 'Accorder',
    debourser: 'Débourser',
    annuler: 'Annuler',
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Nano-Crédits"
        description="Gestion des demandes de nano-crédits"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={credits as CreditRow[]}
            columns={columns}
            keyExtractor={(item) => item.id as string}
            searchKeys={['membreNom']}
            searchPlaceholder="Rechercher par membre..."
            pageSize={10}
            selectable={true}
            exportable={true}
            exportFilename="nano-credits"
            filters={[
              {
                key: 'statut',
                label: 'Statut',
                options: STATUT_OPTIONS,
              },
            ]}
            actions={getActions}
          />
        </CardContent>
      </Card>

      {/* Étudier Dialog — scoreAi + scoreHumain */}
      <Dialog open={etudeOpen} onOpenChange={setEtudeOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Étudier le nano-crédit</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Membre :{' '}
              <span className="font-medium">
                {selectedCredit?.membreNom}
              </span>{' '}
              — Montant :{' '}
              <span className="font-medium">
                {selectedCredit ? formatCurrency(selectedCredit.montant as number) : ''}
              </span>
            </p>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="score-ai">Score IA</Label>
                <Input
                  id="score-ai"
                  type="number"
                  min={0}
                  max={100}
                  value={etudeData.scoreAi}
                  onChange={(e) =>
                    setEtudeData({ ...etudeData, scoreAi: Number(e.target.value) })
                  }
                  placeholder="0–100"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="score-humain">Score Humain</Label>
                <Input
                  id="score-humain"
                  type="number"
                  min={0}
                  max={100}
                  value={etudeData.scoreHumain}
                  onChange={(e) =>
                    setEtudeData({ ...etudeData, scoreHumain: Number(e.target.value) })
                  }
                  placeholder="0–100"
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="etude-commentaire">Commentaire</Label>
              <Textarea
                id="etude-commentaire"
                value={etudeData.commentaire}
                onChange={(e) =>
                  setEtudeData({ ...etudeData, commentaire: e.target.value })
                }
                placeholder="Commentaire sur l'étude..."
                rows={3}
              />
            </div>
            {etudeData.scoreAi > 0 || etudeData.scoreHumain > 0 ? (
              <div className="rounded-lg border p-3 bg-muted/50">
                <p className="text-sm">
                  Score global estimé :{' '}
                  <span className="font-bold">
                    {Math.round((etudeData.scoreAi + etudeData.scoreHumain) / 2)}/100
                  </span>
                </p>
              </div>
            ) : null}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEtudeOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handleEtude}>Lancer l&apos;étude</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Refuse Dialog with motif */}
      <Dialog open={refuseOpen} onOpenChange={setRefuseOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Refuser le nano-crédit</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Refuser le nano-crédit de{' '}
              <span className="font-medium">
                {selectedCredit?.membreNom}
              </span>
            </p>
            <div className="space-y-2">
              <Label htmlFor="refus-motif">Motif du refus</Label>
              <Textarea
                id="refus-motif"
                value={motif}
                onChange={(e) => setMotif(e.target.value)}
                placeholder="Expliquez la raison du refus..."
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setRefuseOpen(false)}>
              Annuler
            </Button>
            <Button variant="destructive" onClick={handleRefuse}>
              Refuser
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Generic Confirm Dialog (Accorder / Débourser) */}
      <ConfirmDialog
        open={confirmOpen}
        onOpenChange={setConfirmOpen}
        title={confirmTitle[actionType] || 'Confirmer'}
        description={confirmDescription[actionType] || 'Confirmez cette action ?'}
        confirmLabel={confirmLabel[actionType] || 'Confirmer'}
        variant={actionType === 'annuler' ? 'destructive' : 'default'}
        onConfirm={handleConfirmAction}
      />

      {/* Cancel Dialog */}
      <ConfirmDialog
        open={cancelOpen}
        onOpenChange={setCancelOpen}
        title="Annuler le nano-crédit"
        description={`Êtes-vous sûr de vouloir annuler le nano-crédit de ${selectedCredit?.membreNom || ''} ?`}
        confirmLabel="Annuler le crédit"
        variant="destructive"
        onConfirm={handleCancel}
      />

      {/* Échéances / Versements Dialog */}
      <Dialog open={echeancesOpen} onOpenChange={setEcheancesOpen}>
        <DialogContent className="max-w-4xl">
          <DialogHeader>
            <DialogTitle>
              Suivi — {selectedCredit?.membreNom || ''} —{' '}
              {selectedCredit ? formatCurrency(selectedCredit.montant as number) : ''}
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-6">
            {/* Échéances */}
            <div>
              <h3 className="mb-2 text-sm font-semibold text-muted-foreground">Échéances</h3>
              <div className="max-h-52 overflow-y-auto rounded-md border">
                {currentEcheances.length > 0 ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead className="w-[50px]">N°</TableHead>
                        <TableHead>Montant</TableHead>
                        <TableHead>Pénalité</TableHead>
                        <TableHead>Date échéance</TableHead>
                        <TableHead>Statut</TableHead>
                        <TableHead>Montant payé</TableHead>
                        <TableHead>Date paiement</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {currentEcheances.map((ech) => (
                        <TableRow key={ech.id}>
                          <TableCell className="font-medium">{ech.numeroEcheance}</TableCell>
                          <TableCell>{formatCurrency(ech.montant)}</TableCell>
                          <TableCell>
                            {ech.montantPenalite ? formatCurrency(ech.montantPenalite) : '—'}
                          </TableCell>
                          <TableCell>{ech.dateEcheance}</TableCell>
                          <TableCell>
                            <StatusBadge statut={ech.statut} />
                          </TableCell>
                          <TableCell>
                            {ech.montantPaye != null ? formatCurrency(ech.montantPaye) : '—'}
                          </TableCell>
                          <TableCell>{ech.datePaiement || '—'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <p className="py-6 text-center text-sm text-muted-foreground">
                    Aucune échéance trouvée
                  </p>
                )}
              </div>
            </div>

            {/* Versements */}
            <div>
              <h3 className="mb-2 text-sm font-semibold text-muted-foreground">Versements</h3>
              <div className="max-h-52 overflow-y-auto rounded-md border">
                {currentVersements.length > 0 ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Montant</TableHead>
                        <TableHead>Date versement</TableHead>
                        <TableHead>Mode paiement</TableHead>
                        <TableHead>Référence</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {currentVersements.map((v) => (
                        <TableRow key={v.id}>
                          <TableCell className="font-medium">
                            {formatCurrency(v.montant)}
                          </TableCell>
                          <TableCell>{v.dateVersement}</TableCell>
                          <TableCell>{v.modePaiement || '—'}</TableCell>
                          <TableCell>{v.reference || '—'}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <p className="py-6 text-center text-sm text-muted-foreground">
                    Aucun versement trouvé
                  </p>
                )}
              </div>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
