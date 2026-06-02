'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Eye, ShieldCheck } from 'lucide-react';
import type { AuditLog, ActorType } from '@/types';

const mockAuditLogs: (AuditLog & Record<string, unknown>)[] = [
  { id: '1', actorType: 'ADMIN', actorId: 'ADM-001', action: 'CREATE', model: 'Cotisation', modelId: 'COT-001', oldValues: null, newValues: { libelle: 'Cotisation mensuelle', montant: 5000 }, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-01 09:00' },
  { id: '2', actorType: 'MEMBRE', actorId: 'MEM-005', action: 'UPDATE', model: 'Membre', modelId: 'MEM-005', oldValues: { telephone: '+221 77 000 00 00' }, newValues: { telephone: '+221 77 123 45 67' }, ipAddress: '10.0.0.55', userAgent: 'Safari/17', createdAt: '2024-06-01 10:15' },
  { id: '3', actorType: 'SYSTEM', actorId: 'SYSTEM', action: 'AUTO_CONFIRM', model: 'Paiement', modelId: 'PAY-012', oldValues: { statut: 'EN_ATTENTE' }, newValues: { statut: 'CONFIRME' }, ipAddress: '127.0.0.1', userAgent: 'System', createdAt: '2024-06-01 10:30' },
  { id: '4', actorType: 'ADMIN', actorId: 'ADM-001', action: 'DELETE', model: 'Annonce', modelId: 'ANN-003', oldValues: { titre: 'Ancienne promo' }, newValues: null, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-02 08:00' },
  { id: '5', actorType: 'COLLECTEUR', actorId: 'COL-002', action: 'CREATE', model: 'Collecte', modelId: 'COL-CLS-001', oldValues: null, newValues: { montant: 5000, type: 'COTISATION' }, ipAddress: '10.0.1.22', userAgent: 'Android/14', createdAt: '2024-06-02 11:30' },
  { id: '6', actorType: 'MEMBRE', actorId: 'MEM-008', action: 'LOGIN', model: 'Session', modelId: 'SES-045', oldValues: null, newValues: { loginAt: '2024-06-03 07:15' }, ipAddress: '10.0.0.88', userAgent: 'Chrome/121', createdAt: '2024-06-03 07:15' },
  { id: '7', actorType: 'ADMIN', actorId: 'ADM-002', action: 'UPDATE', model: 'AppSetting', modelId: 'SET-004', oldValues: { valeur: '1000000' }, newValues: { valeur: '5000000' }, ipAddress: '192.168.1.15', userAgent: 'Firefox/122', createdAt: '2024-06-03 09:00' },
  { id: '8', actorType: 'SYSTEM', actorId: 'SYSTEM', action: 'PENALTY_APPLY', model: 'NanoCredit', modelId: 'NC-015', oldValues: { montantPenalite: 0 }, newValues: { montantPenalite: 500, joursRetard: 3 }, ipAddress: '127.0.0.1', userAgent: 'System', createdAt: '2024-06-04 00:00' },
  { id: '9', actorType: 'MEMBRE', actorId: 'MEM-003', action: 'CREATE', model: 'Engagement', modelId: 'ENG-022', oldValues: null, newValues: { montantEngage: 25000, cotisationId: 'COT-001' }, ipAddress: '10.0.0.33', userAgent: 'Chrome/121', createdAt: '2024-06-04 14:20' },
  { id: '10', actorType: 'ADMIN', actorId: 'ADM-001', action: 'ACTIVATE', model: 'PayDunyaConfig', modelId: 'PDC-002', oldValues: { isActive: false }, newValues: { isActive: true }, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-05 10:00' },
  { id: '11', actorType: 'MEMBRE', actorId: 'MEM-012', action: 'UPDATE', model: 'Membre', modelId: 'MEM-012', oldValues: { email: 'ancien@email.com' }, newValues: { email: 'nouveau@email.com' }, ipAddress: '10.0.0.102', userAgent: 'Chrome/121', createdAt: '2024-06-05 11:30' },
  { id: '12', actorType: 'ADMIN', actorId: 'ADM-001', action: 'CREATE', model: 'Cotisation', modelId: 'COT-005', oldValues: null, newValues: { libelle: 'Cotisation annuelle', montant: 60000 }, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-05 14:00' },
  { id: '13', actorType: 'SYSTEM', actorId: 'SYSTEM', action: 'SCHEDULED_REPORT', model: 'Rapport', modelId: 'RPT-042', oldValues: null, newValues: { type: 'quotidien', envoyeA: 'adm-001@serenity.sn' }, ipAddress: '127.0.0.1', userAgent: 'System', createdAt: '2024-06-06 06:00' },
  { id: '14', actorType: 'COLLECTEUR', actorId: 'COL-003', action: 'CREATE', model: 'Collecte', modelId: 'COL-CLS-045', oldValues: null, newValues: { montant: 15000, type: 'EPARGNE' }, ipAddress: '10.0.1.35', userAgent: 'Android/14', createdAt: '2024-06-06 09:15' },
  { id: '15', actorType: 'MEMBRE', actorId: 'MEM-015', action: 'CREATE', model: 'EpargneSouscription', modelId: 'EPG-033', oldValues: null, newValues: { montant: 20000, frequence: 'MENSUELLE' }, ipAddress: '10.0.0.115', userAgent: 'Safari/17', createdAt: '2024-06-06 10:45' },
  { id: '16', actorType: 'ADMIN', actorId: 'ADM-002', action: 'DELETE', model: 'Tag', modelId: 'TAG-008', oldValues: { nom: 'Obsolète' }, newValues: null, ipAddress: '192.168.1.15', userAgent: 'Firefox/122', createdAt: '2024-06-06 14:30' },
  { id: '17', actorType: 'MEMBRE', actorId: 'MEM-002', action: 'LOGIN', model: 'Session', modelId: 'SES-048', oldValues: null, newValues: { loginAt: '2024-06-07 07:30' }, ipAddress: '10.0.0.22', userAgent: 'Chrome/121', createdAt: '2024-06-07 07:30' },
  { id: '18', actorType: 'SYSTEM', actorId: 'SYSTEM', action: 'AUTO_CONFIRM', model: 'Paiement', modelId: 'PAY-025', oldValues: { statut: 'EN_ATTENTE' }, newValues: { statut: 'CONFIRME' }, ipAddress: '127.0.0.1', userAgent: 'System', createdAt: '2024-06-07 08:00' },
  { id: '19', actorType: 'ADMIN', actorId: 'ADM-001', action: 'UPDATE', model: 'Cotisation', modelId: 'COT-002', oldValues: { montant: 5000 }, newValues: { montant: 7500 }, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-07 10:00' },
  { id: '20', actorType: 'COLLECTEUR', actorId: 'COL-001', action: 'UPDATE', model: 'CollecteSession', modelId: 'CS-018', oldValues: { statut: 'OUVERT' }, newValues: { statut: 'FERME' }, ipAddress: '10.0.1.10', userAgent: 'Android/14', createdAt: '2024-06-07 16:30' },
  { id: '21', actorType: 'MEMBRE', actorId: 'MEM-009', action: 'CREATE', model: 'NanoCredit', modelId: 'NC-028', oldValues: null, newValues: { montant: 30000, duree: 30 }, ipAddress: '10.0.0.99', userAgent: 'Chrome/121', createdAt: '2024-06-08 08:45' },
  { id: '22', actorType: 'ADMIN', actorId: 'ADM-003', action: 'CREATE', model: 'Annonce', modelId: 'ANN-010', oldValues: null, newValues: { titre: 'Maintenance prévue', type: 'MAINTENANCE' }, ipAddress: '192.168.1.20', userAgent: 'Chrome/122', createdAt: '2024-06-08 09:30' },
  { id: '23', actorType: 'SYSTEM', actorId: 'SYSTEM', action: 'PENALTY_APPLY', model: 'NanoCredit', modelId: 'NC-008', oldValues: { montantPenalite: 200 }, newValues: { montantPenalite: 400, joursRetard: 7 }, ipAddress: '127.0.0.1', userAgent: 'System', createdAt: '2024-06-09 00:00' },
  { id: '24', actorType: 'MEMBRE', actorId: 'MEM-018', action: 'UPDATE', model: 'Membre', modelId: 'MEM-018', oldValues: { adresse: 'Ancienne adresse' }, newValues: { adresse: 'Nouvelle adresse, Dakar' }, ipAddress: '10.0.0.118', userAgent: 'Safari/17', createdAt: '2024-06-09 10:15' },
  { id: '25', actorType: 'ADMIN', actorId: 'ADM-001', action: 'ACTIVATE', model: 'PiSpiConfig', modelId: 'PSC-003', oldValues: { isActive: false }, newValues: { isActive: true }, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-09 11:00' },
  { id: '26', actorType: 'COLLECTEUR', actorId: 'COL-002', action: 'CREATE', model: 'Collecte', modelId: 'COL-CLS-048', oldValues: null, newValues: { montant: 10000, type: 'COTISATION' }, ipAddress: '10.0.1.22', userAgent: 'Android/14', createdAt: '2024-06-09 14:00' },
  { id: '27', actorType: 'MEMBRE', actorId: 'MEM-020', action: 'LOGIN', model: 'Session', modelId: 'SES-055', oldValues: null, newValues: { loginAt: '2024-06-10 07:00' }, ipAddress: '10.0.0.120', userAgent: 'Chrome/122', createdAt: '2024-06-10 07:00' },
  { id: '28', actorType: 'ADMIN', actorId: 'ADM-002', action: 'DELETE', model: 'SmtpConfiguration', modelId: 'SMTP-004', oldValues: { host: 'old.smtp.com' }, newValues: null, ipAddress: '192.168.1.15', userAgent: 'Firefox/123', createdAt: '2024-06-10 09:30' },
  { id: '29', actorType: 'SYSTEM', actorId: 'SYSTEM', action: 'SCHEDULED_REPORT', model: 'Rapport', modelId: 'RPT-049', oldValues: null, newValues: { type: 'hebdomadaire', envoyeA: 'adm-002@serenity.sn' }, ipAddress: '127.0.0.1', userAgent: 'System', createdAt: '2024-06-10 06:00' },
  { id: '30', actorType: 'MEMBRE', actorId: 'MEM-007', action: 'CREATE', model: 'Engagement', modelId: 'ENG-035', oldValues: null, newValues: { montantEngage: 50000, cotisationId: 'COT-003' }, ipAddress: '10.0.0.77', userAgent: 'Chrome/121', createdAt: '2024-06-10 11:00' },
  { id: '31', actorType: 'ADMIN', actorId: 'ADM-001', action: 'UPDATE', model: 'AppSetting', modelId: 'SET-010', oldValues: { valeur: '50000' }, newValues: { valeur: '100000' }, ipAddress: '192.168.1.10', userAgent: 'Chrome/120', createdAt: '2024-06-10 14:15' },
  { id: '32', actorType: 'COLLECTEUR', actorId: 'COL-001', action: 'UPDATE', model: 'CollecteSession', modelId: 'CS-020', oldValues: { statut: 'FERME' }, newValues: { statut: 'RECONCILIE' }, ipAddress: '10.0.1.10', userAgent: 'Android/14', createdAt: '2024-06-11 08:00' },
  { id: '33', actorType: 'MEMBRE', actorId: 'MEM-014', action: 'UPDATE', model: 'Membre', modelId: 'MEM-014', oldValues: { nom: 'Ancien nom' }, newValues: { nom: 'Nouveau nom' }, ipAddress: '10.0.0.114', userAgent: 'Safari/17', createdAt: '2024-06-11 10:30' },
];

const ACTOR_TYPE_LABELS: Record<string, string> = { SYSTEM: 'Système', ADMIN: 'Admin', MEMBRE: 'Membre', COLLECTEUR: 'Collecteur' };

export default function AuditLogsPage() {
  const [detailOpen, setDetailOpen] = useState(false);
  const [merkleOpen, setMerkleOpen] = useState(false);
  const [selectedLog, setSelectedLog] = useState<(AuditLog & Record<string, unknown>) | null>(null);
  const [merkleTable, setMerkleTable] = useState('audit_log');
  const [merkleResult, setMerkleResult] = useState<string | null>(null);
  const [filters, setFilters] = useState({ actorType: '', model: '', dateFrom: '', dateTo: '' });

  let filteredLogs = [...mockAuditLogs];
  if (filters.actorType) {
    filteredLogs = filteredLogs.filter((l) => l.actorType === filters.actorType);
  }
  if (filters.model) {
    filteredLogs = filteredLogs.filter((l) => l.model === filters.model);
  }
  if (filters.dateFrom) {
    filteredLogs = filteredLogs.filter((l) => (l.createdAt as string) >= filters.dateFrom);
  }
  if (filters.dateTo) {
    filteredLogs = filteredLogs.filter((l) => (l.createdAt as string) <= filters.dateTo + ' 23:59');
  }

  const columns: Column<AuditLog & Record<string, unknown>>[] = [
    {
      key: 'actorId', header: 'Acteur', sortable: true,
      render: (item) => <span className="font-medium">{item.actorId}</span>,
    },
    {
      key: 'actorType', header: 'Type Acteur', sortable: true,
      render: (item) => <Badge variant="outline">{ACTOR_TYPE_LABELS[item.actorType as string] || item.actorType}</Badge>,
    },
    {
      key: 'action', header: 'Action', sortable: true,
      render: (item) => <span className="font-mono text-sm">{item.action}</span>,
    },
    { key: 'model', header: 'Modèle', sortable: true },
    {
      key: 'modelId', header: 'ID Modèle',
      render: (item) => <span className="font-mono text-xs">{item.modelId}</span>,
    },
    { key: 'ipAddress', header: 'Adresse IP' },
    { key: 'createdAt', header: 'Date', sortable: true },
  ];

  const handleVerifyMerkle = () => {
    // Simulate Merkle integrity check
    const isValid = Math.random() > 0.3;
    setMerkleResult(isValid
      ? `✅ Intégrité vérifiée pour la table "${merkleTable}". Tous les hashes Merkle sont valides.`
      : `⚠️ Incohérence détectée dans la table "${merkleTable}". Veuillez consulter les logs pour plus de détails.`
    );
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Journal d'Audit"
        description="Traçabilité des actions effectuées sur le système"
        actions={
          <Button size="sm" variant="outline" className="gap-2" onClick={() => setMerkleOpen(true)}>
            <ShieldCheck className="h-4 w-4" />
            Vérifier intégrité Merkle
          </Button>
        }
      />

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-wrap gap-4 items-end">
            <div className="space-y-1">
              <Label className="text-xs">Type Acteur</Label>
              <Select value={filters.actorType || '__all__'} onValueChange={(v) => setFilters({ ...filters, actorType: v === '__all__' ? '' : v })}>
                <SelectTrigger className="w-[150px]"><SelectValue placeholder="Tous" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="__all__">Tous</SelectItem>
                  <SelectItem value="SYSTEM">Système</SelectItem>
                  <SelectItem value="ADMIN">Admin</SelectItem>
                  <SelectItem value="MEMBRE">Membre</SelectItem>
                  <SelectItem value="COLLECTEUR">Collecteur</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1">
              <Label className="text-xs">Modèle</Label>
              <Select value={filters.model || '__all__'} onValueChange={(v) => setFilters({ ...filters, model: v === '__all__' ? '' : v })}>
                <SelectTrigger className="w-[150px]"><SelectValue placeholder="Tous" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="__all__">Tous</SelectItem>
                  <SelectItem value="Cotisation">Cotisation</SelectItem>
                  <SelectItem value="Membre">Membre</SelectItem>
                  <SelectItem value="Paiement">Paiement</SelectItem>
                  <SelectItem value="Annonce">Annonce</SelectItem>
                  <SelectItem value="Collecte">Collecte</SelectItem>
                  <SelectItem value="AppSetting">AppSetting</SelectItem>
                  <SelectItem value="NanoCredit">NanoCredit</SelectItem>
                  <SelectItem value="Engagement">Engagement</SelectItem>
                  <SelectItem value="PayDunyaConfig">PayDunyaConfig</SelectItem>
                  <SelectItem value="PiSpiConfig">PiSpiConfig</SelectItem>
                  <SelectItem value="Session">Session</SelectItem>
                  <SelectItem value="Rapport">Rapport</SelectItem>
                  <SelectItem value="Tag">Tag</SelectItem>
                  <SelectItem value="SmtpConfiguration">SmtpConfiguration</SelectItem>
                  <SelectItem value="EpargneSouscription">EpargneSouscription</SelectItem>
                  <SelectItem value="CollecteSession">CollecteSession</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1">
              <Label className="text-xs">Du</Label>
              <Input type="date" value={filters.dateFrom} onChange={(e) => setFilters({ ...filters, dateFrom: e.target.value })} className="w-[150px]" />
            </div>
            <div className="space-y-1">
              <Label className="text-xs">Au</Label>
              <Input type="date" value={filters.dateTo} onChange={(e) => setFilters({ ...filters, dateTo: e.target.value })} className="w-[150px]" />
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={filteredLogs}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchable={false}
            pageSize={10}
            selectable={true}
            exportable={true}
            exportFilename="audit-logs"
            actions={(item) => [
              { label: 'Détails', onClick: () => { setSelectedLog(item); setDetailOpen(true); } },
            ]}
          />
        </CardContent>
      </Card>

      {/* Detail Dialog */}
      <Dialog open={detailOpen} onOpenChange={setDetailOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Eye className="h-5 w-5" />
              Détails de l&apos;Audit
            </DialogTitle>
          </DialogHeader>
          {selectedLog && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <p className="text-sm text-muted-foreground">Acteur</p>
                  <p className="font-medium">{selectedLog.actorId}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Type Acteur</p>
                  <Badge variant="outline">{ACTOR_TYPE_LABELS[selectedLog.actorType as string]}</Badge>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Action</p>
                  <p className="font-mono text-sm">{selectedLog.action}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Modèle</p>
                  <p className="font-medium">{selectedLog.model}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">ID Modèle</p>
                  <p className="font-mono text-xs">{selectedLog.modelId}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Adresse IP</p>
                  <p className="font-mono text-sm">{selectedLog.ipAddress}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-muted-foreground mb-2">Anciennes Valeurs</p>
                  <pre className="text-xs bg-red-50 dark:bg-red-950/20 p-3 rounded-md overflow-auto max-h-48 border border-red-200 dark:border-red-800">
                    {selectedLog.oldValues ? JSON.stringify(selectedLog.oldValues, null, 2) : '—'}
                  </pre>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground mb-2">Nouvelles Valeurs</p>
                  <pre className="text-xs bg-emerald-50 dark:bg-emerald-950/20 p-3 rounded-md overflow-auto max-h-48 border border-emerald-200 dark:border-emerald-800">
                    {selectedLog.newValues ? JSON.stringify(selectedLog.newValues, null, 2) : '—'}
                  </pre>
                </div>
              </div>

              <div>
                <p className="text-sm text-muted-foreground">User Agent</p>
                <p className="text-xs text-muted-foreground">{selectedLog.userAgent}</p>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Merkle Verification Dialog */}
      <Dialog open={merkleOpen} onOpenChange={setMerkleOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <ShieldCheck className="h-5 w-5" />
              Vérification d&apos;Intégrité Merkle
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Table à vérifier</Label>
              <Select value={merkleTable} onValueChange={setMerkleTable}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="audit_log">audit_log</SelectItem>
                  <SelectItem value="membre">membre</SelectItem>
                  <SelectItem value="cotisation">cotisation</SelectItem>
                  <SelectItem value="paiement">paiement</SelectItem>
                  <SelectItem value="nano_credit">nano_credit</SelectItem>
                  <SelectItem value="epargne_souscription">epargne_souscription</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <Button onClick={handleVerifyMerkle} className="w-full gap-2">
              <ShieldCheck className="h-4 w-4" />
              Vérifier l&apos;intégrité
            </Button>
            {merkleResult && (
              <div className={`p-4 rounded-md text-sm ${merkleResult.startsWith('✅') ? 'bg-emerald-50 dark:bg-emerald-950/20 border border-emerald-200 dark:border-emerald-800' : 'bg-amber-50 dark:bg-amber-950/20 border border-amber-200 dark:border-amber-800'}`}>
                {merkleResult}
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
