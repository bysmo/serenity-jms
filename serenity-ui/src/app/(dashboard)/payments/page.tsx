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
import { Card, CardContent } from '@/components/ui/card';
import { ArrowDownCircle, ArrowUpCircle, Eye } from 'lucide-react';
import type { PaymentTransaction } from '@/types';

const formatFCFA = (amount: number) => `${amount.toLocaleString('fr-FR')} FCFA`;

const mockTransactions: (PaymentTransaction & Record<string, unknown>)[] = [
  { id: '1', reference: 'TXN-20240601-001', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 25000, statut: 'SUCCESS', telephone: '+221 77 123 45 67', createdAt: '2024-06-01 09:30', currency: 'XOF' },
  { id: '2', reference: 'TXN-20240601-002', gateway: 'PISPI', transactionType: 'DISBURSEMENT', montant: 50000, statut: 'PENDING', telephone: '+221 78 234 56 78', createdAt: '2024-06-01 10:15', currency: 'XOF' },
  { id: '3', reference: 'TXN-20240601-003', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 10000, statut: 'FAILED', telephone: '+221 76 345 67 89', createdAt: '2024-06-01 11:00', currency: 'XOF' },
  { id: '4', reference: 'TXN-20240602-001', gateway: 'PISPI', transactionType: 'DISBURSEMENT', montant: 75000, statut: 'SUCCESS', telephone: '+221 77 456 78 90', createdAt: '2024-06-02 08:45', currency: 'XOF' },
  { id: '5', reference: 'TXN-20240602-002', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 15000, statut: 'CANCELLED', telephone: '+221 78 567 89 01', createdAt: '2024-06-02 09:20', currency: 'XOF' },
  { id: '6', reference: 'TXN-20240602-003', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 30000, statut: 'SUCCESS', telephone: '+221 76 678 90 12', createdAt: '2024-06-02 10:30', currency: 'XOF' },
  { id: '7', reference: 'TXN-20240603-001', gateway: 'PAYDUNYA', transactionType: 'DISBURSEMENT', montant: 100000, statut: 'PENDING', telephone: '+221 77 789 01 23', createdAt: '2024-06-03 07:15', currency: 'XOF' },
  { id: '8', reference: 'TXN-20240603-002', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 5000, statut: 'SUCCESS', telephone: '+221 78 890 12 34', createdAt: '2024-06-03 11:45', currency: 'XOF' },
  { id: '9', reference: 'TXN-20240603-003', gateway: 'PAYDUNYA', transactionType: 'DISBURSEMENT', montant: 20000, statut: 'FAILED', telephone: '+221 76 901 23 45', createdAt: '2024-06-03 14:00', currency: 'XOF' },
  { id: '10', reference: 'TXN-20240604-001', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 45000, statut: 'SUCCESS', telephone: '+221 77 012 34 56', createdAt: '2024-06-04 08:30', currency: 'XOF' },
  { id: '11', reference: 'TXN-20240604-002', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 12500, statut: 'SUCCESS', telephone: '+221 78 111 22 33', createdAt: '2024-06-04 09:15', currency: 'XOF' },
  { id: '12', reference: 'TXN-20240604-003', gateway: 'PISPI', transactionType: 'DISBURSEMENT', montant: 85000, statut: 'SUCCESS', telephone: '+221 76 222 33 44', createdAt: '2024-06-04 10:00', currency: 'XOF' },
  { id: '13', reference: 'TXN-20240605-001', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 35000, statut: 'PENDING', telephone: '+221 77 333 44 55', createdAt: '2024-06-05 08:00', currency: 'XOF' },
  { id: '14', reference: 'TXN-20240605-002', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 18000, statut: 'FAILED', telephone: '+221 78 444 55 66', createdAt: '2024-06-05 09:30', currency: 'XOF' },
  { id: '15', reference: 'TXN-20240605-003', gateway: 'PAYDUNYA', transactionType: 'DISBURSEMENT', montant: 60000, statut: 'SUCCESS', telephone: '+221 76 555 66 77', createdAt: '2024-06-05 11:00', currency: 'XOF' },
  { id: '16', reference: 'TXN-20240606-001', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 22000, statut: 'SUCCESS', telephone: '+221 77 666 77 88', createdAt: '2024-06-06 07:45', currency: 'XOF' },
  { id: '17', reference: 'TXN-20240606-002', gateway: 'PAYDUNYA', transactionType: 'DISBURSEMENT', montant: 150000, statut: 'CANCELLED', telephone: '+221 78 777 88 99', createdAt: '2024-06-06 10:20', currency: 'XOF' },
  { id: '18', reference: 'TXN-20240606-003', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 8000, statut: 'SUCCESS', telephone: '+221 76 888 99 00', createdAt: '2024-06-06 14:15', currency: 'XOF' },
  { id: '19', reference: 'TXN-20240607-001', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 67500, statut: 'SUCCESS', telephone: '+221 77 999 00 11', createdAt: '2024-06-07 08:30', currency: 'XOF' },
  { id: '20', reference: 'TXN-20240607-002', gateway: 'PISPI', transactionType: 'DISBURSEMENT', montant: 40000, statut: 'PENDING', telephone: '+221 78 100 11 22', createdAt: '2024-06-07 09:00', currency: 'XOF' },
  { id: '21', reference: 'TXN-20240607-003', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 27500, statut: 'SUCCESS', telephone: '+221 76 200 22 33', createdAt: '2024-06-07 11:30', currency: 'XOF' },
  { id: '22', reference: 'TXN-20240608-001', gateway: 'PISPI', transactionType: 'DISBURSEMENT', montant: 95000, statut: 'FAILED', telephone: '+221 77 300 33 44', createdAt: '2024-06-08 08:00', currency: 'XOF' },
  { id: '23', reference: 'TXN-20240608-002', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 55000, statut: 'SUCCESS', telephone: '+221 78 400 44 55', createdAt: '2024-06-08 10:45', currency: 'XOF' },
  { id: '24', reference: 'TXN-20240608-003', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 12000, statut: 'SUCCESS', telephone: '+221 76 500 55 66', createdAt: '2024-06-08 13:00', currency: 'XOF' },
  { id: '25', reference: 'TXN-20240609-001', gateway: 'PAYDUNYA', transactionType: 'DISBURSEMENT', montant: 200000, statut: 'SUCCESS', telephone: '+221 77 600 66 77', createdAt: '2024-06-09 07:30', currency: 'XOF' },
  { id: '26', reference: 'TXN-20240609-002', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 33000, statut: 'PENDING', telephone: '+221 78 700 77 88', createdAt: '2024-06-09 09:15', currency: 'XOF' },
  { id: '27', reference: 'TXN-20240609-003', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 7500, statut: 'SUCCESS', telephone: '+221 76 800 88 99', createdAt: '2024-06-09 11:00', currency: 'XOF' },
  { id: '28', reference: 'TXN-20240610-001', gateway: 'PISPI', transactionType: 'DISBURSEMENT', montant: 110000, statut: 'CANCELLED', telephone: '+221 77 900 99 00', createdAt: '2024-06-10 08:30', currency: 'XOF' },
  { id: '29', reference: 'TXN-20240610-002', gateway: 'PAYDUNYA', transactionType: 'COLLECTION', montant: 42500, statut: 'SUCCESS', telephone: '+221 78 321 54 76', createdAt: '2024-06-10 10:00', currency: 'XOF' },
  { id: '30', reference: 'TXN-20240610-003', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 19500, statut: 'FAILED', telephone: '+221 76 654 87 09', createdAt: '2024-06-10 14:30', currency: 'XOF' },
  { id: '31', reference: 'TXN-20240611-001', gateway: 'PAYDUNYA', transactionType: 'DISBURSEMENT', montant: 130000, statut: 'SUCCESS', telephone: '+221 77 987 65 43', createdAt: '2024-06-11 09:00', currency: 'XOF' },
  { id: '32', reference: 'TXN-20240611-002', gateway: 'PISPI', transactionType: 'COLLECTION', montant: 28000, statut: 'SUCCESS', telephone: '+221 78 135 79 24', createdAt: '2024-06-11 11:45', currency: 'XOF' },
];

const GATEWAY_LABELS: Record<string, string> = { PAYDUNYA: 'PayDunya', PISPI: 'Pi-SPI' };
const TYPE_LABELS: Record<string, string> = { COLLECTION: 'Encaissement', DISBURSEMENT: 'Décaissement' };

export default function PaymentsPage() {
  const [transactions, setTransactions] = useState(mockTransactions);
  const [disbursementOpen, setDisbursementOpen] = useState(false);
  const [collectionOpen, setCollectionOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [selectedTxn, setSelectedTxn] = useState<(PaymentTransaction & Record<string, unknown>) | null>(null);
  const [disbursementForm, setDisbursementForm] = useState({ telephone: '', montant: 0, gateway: 'PAYDUNYA' as string });
  const [collectionForm, setCollectionForm] = useState({ telephone: '', montant: 0, gateway: 'PAYDUNYA' as string });

  const columns: Column<PaymentTransaction & Record<string, unknown>>[] = [
    { key: 'reference', header: 'Référence', sortable: true },
    {
      key: 'gateway', header: 'Gateway', sortable: true,
      render: (item) => <span className="text-sm font-medium">{GATEWAY_LABELS[item.gateway as string] || item.gateway}</span>,
    },
    {
      key: 'transactionType', header: 'Type', sortable: true,
      render: (item) => (
        <span className="inline-flex items-center gap-1 text-sm">
          {item.transactionType === 'DISBURSEMENT' ? (
            <ArrowDownCircle className="h-4 w-4 text-red-500" />
          ) : (
            <ArrowUpCircle className="h-4 w-4 text-emerald-500" />
          )}
          {TYPE_LABELS[item.transactionType as string] || item.transactionType}
        </span>
      ),
    },
    {
      key: 'montant', header: 'Montant', sortable: true,
      render: (item) => <span className="font-medium">{formatFCFA(item.montant as number)}</span>,
    },
    {
      key: 'statut', header: 'Statut', sortable: true,
      render: (item) => <StatusBadge statut={item.statut as string} />,
    },
    { key: 'telephone', header: 'Téléphone' },
    { key: 'createdAt', header: 'Date', sortable: true },
  ];

  const handleDisbursement = () => {
    const newTxn: PaymentTransaction & Record<string, unknown> = {
      id: String(transactions.length + 1),
      reference: `TXN-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${String(transactions.length + 1).padStart(3, '0')}`,
      gateway: disbursementForm.gateway as 'PAYDUNYA' | 'PISPI',
      transactionType: 'DISBURSEMENT',
      montant: disbursementForm.montant,
      statut: 'PENDING',
      telephone: disbursementForm.telephone,
      createdAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
      currency: 'XOF',
    };
    setTransactions([newTxn, ...transactions]);
    setDisbursementOpen(false);
    setDisbursementForm({ telephone: '', montant: 0, gateway: 'PAYDUNYA' });
  };

  const handleCollection = () => {
    const newTxn: PaymentTransaction & Record<string, unknown> = {
      id: String(transactions.length + 1),
      reference: `TXN-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${String(transactions.length + 1).padStart(3, '0')}`,
      gateway: collectionForm.gateway as 'PAYDUNYA' | 'PISPI',
      transactionType: 'COLLECTION',
      montant: collectionForm.montant,
      statut: 'PENDING',
      telephone: collectionForm.telephone,
      createdAt: new Date().toISOString().slice(0, 16).replace('T', ' '),
      currency: 'XOF',
    };
    setTransactions([newTxn, ...transactions]);
    setCollectionOpen(false);
    setCollectionForm({ telephone: '', montant: 0, gateway: 'PAYDUNYA' });
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Transactions de Paiement"
        description="Historique et gestion des transactions de paiement"
        actions={
          <div className="flex gap-2">
            <Button variant="outline" size="sm" className="gap-2" onClick={() => setDisbursementOpen(true)}>
              <ArrowDownCircle className="h-4 w-4" />
              Décaissement
            </Button>
            <Button size="sm" className="gap-2" onClick={() => setCollectionOpen(true)}>
              <ArrowUpCircle className="h-4 w-4" />
              Encaissement
            </Button>
          </div>
        }
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={transactions}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchKeys={['reference', 'telephone']}
            searchPlaceholder="Rechercher une transaction..."
            pageSize={10}
            selectable={true}
            exportable={true}
            exportFilename="transactions-paiement"
            filters={[
              {
                key: 'gateway',
                label: 'Gateway',
                options: [
                  { label: 'PayDunya', value: 'PAYDUNYA' },
                  { label: 'Pi-SPI', value: 'PISPI' },
                ],
              },
              {
                key: 'statut',
                label: 'Statut',
                options: [
                  { label: 'En attente', value: 'PENDING' },
                  { label: 'Succès', value: 'SUCCESS' },
                  { label: 'Échoué', value: 'FAILED' },
                  { label: 'Annulé', value: 'CANCELLED' },
                ],
              },
            ]}
            actions={(item) => [
              { label: 'Détails', onClick: () => { setSelectedTxn(item); setDetailOpen(true); } },
            ]}
          />
        </CardContent>
      </Card>

      {/* Disbursement Dialog */}
      <Dialog open={disbursementOpen} onOpenChange={setDisbursementOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Décaissement</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Téléphone</Label>
              <Input
                placeholder="+221 77 000 00 00"
                value={disbursementForm.telephone}
                onChange={(e) => setDisbursementForm({ ...disbursementForm, telephone: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label>Montant (FCFA)</Label>
              <Input
                type="number"
                value={disbursementForm.montant || ''}
                onChange={(e) => setDisbursementForm({ ...disbursementForm, montant: Number(e.target.value) })}
              />
            </div>
            <div className="space-y-2">
              <Label>Gateway</Label>
              <Select value={disbursementForm.gateway} onValueChange={(v) => setDisbursementForm({ ...disbursementForm, gateway: v })}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="PAYDUNYA">PayDunya</SelectItem>
                  <SelectItem value="PISPI">Pi-SPI</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDisbursementOpen(false)}>Annuler</Button>
            <Button onClick={handleDisbursement}>Envoyer</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Collection Dialog */}
      <Dialog open={collectionOpen} onOpenChange={setCollectionOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Encaissement</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Téléphone</Label>
              <Input
                placeholder="+221 77 000 00 00"
                value={collectionForm.telephone}
                onChange={(e) => setCollectionForm({ ...collectionForm, telephone: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label>Montant (FCFA)</Label>
              <Input
                type="number"
                value={collectionForm.montant || ''}
                onChange={(e) => setCollectionForm({ ...collectionForm, montant: Number(e.target.value) })}
              />
            </div>
            <div className="space-y-2">
              <Label>Gateway</Label>
              <Select value={collectionForm.gateway} onValueChange={(v) => setCollectionForm({ ...collectionForm, gateway: v })}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="PAYDUNYA">PayDunya</SelectItem>
                  <SelectItem value="PISPI">Pi-SPI</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCollectionOpen(false)}>Annuler</Button>
            <Button onClick={handleCollection}>Encaisser</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Detail Dialog */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Eye className="h-5 w-5" />
              Détails de la Transaction
            </DialogTitle>
          </DialogHeader>
          {selectedTxn && (
            <div className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <p className="text-sm text-muted-foreground">Référence</p>
                  <p className="font-medium">{selectedTxn.reference}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Gateway</p>
                  <p className="font-medium">{GATEWAY_LABELS[selectedTxn.gateway as string]}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Type</p>
                  <p className="font-medium">{TYPE_LABELS[selectedTxn.transactionType as string]}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Montant</p>
                  <p className="font-medium">{formatFCFA(selectedTxn.montant as number)}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Statut</p>
                  <StatusBadge statut={selectedTxn.statut as string} />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Téléphone</p>
                  <p className="font-medium">{selectedTxn.telephone}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Date</p>
                  <p className="font-medium">{selectedTxn.createdAt}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Devise</p>
                  <p className="font-medium">{selectedTxn.currency}</p>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
