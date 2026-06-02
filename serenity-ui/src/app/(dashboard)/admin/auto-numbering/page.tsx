'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { DataTable, type Column } from '@/components/shared/data-table';
import { StatusBadge } from '@/components/shared/status-badge';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Card, CardContent } from '@/components/ui/card';
import { Hash, Play } from 'lucide-react';
import type { AutoNumberingConfig } from '@/types';

const mockConfigs: (AutoNumberingConfig & Record<string, unknown>)[] = [
  { id: '1', objectType: 'MEMBRE', definition: { prefix: 'MEM', separator: '-', padding: 5, suffix: '' }, currentValue: 142, isActive: true, checksum: '' },
  { id: '2', objectType: 'COTISATION', definition: { prefix: 'COT', separator: '-', padding: 4, suffix: '' }, currentValue: 89, isActive: true, checksum: '' },
  { id: '3', objectType: 'ENGAGEMENT', definition: { prefix: 'ENG', separator: '-', padding: 4, suffix: '' }, currentValue: 56, isActive: true, checksum: '' },
  { id: '4', objectType: 'NANOCREDIT', definition: { prefix: 'NC', separator: '-', padding: 5, suffix: '' }, currentValue: 23, isActive: true, checksum: '' },
  { id: '5', objectType: 'EPARGNE', definition: { prefix: 'EPG', separator: '-', padding: 4, suffix: '' }, currentValue: 67, isActive: false, checksum: '' },
  { id: '6', objectType: 'PAIEMENT', definition: { prefix: 'PAY', separator: '-', padding: 6, suffix: '' }, currentValue: 203, isActive: true, checksum: '' },
  { id: '7', objectType: 'COLLECTE', definition: { prefix: 'CLS', separator: '-', padding: 4, suffix: '' }, currentValue: 34, isActive: true, checksum: '' },
  { id: '8', objectType: 'SESSION', definition: { prefix: 'SES', separator: '-', padding: 4, suffix: '' }, currentValue: 48, isActive: true, checksum: '' },
  { id: '9', objectType: 'RAPPORT', definition: { prefix: 'RPT', separator: '-', padding: 4, suffix: '' }, currentValue: 15, isActive: false, checksum: '' },
];

export default function AutoNumberingPage() {
  const [configs, setConfigs] = useState(mockConfigs);
  const [generateOpen, setGenerateOpen] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState<(AutoNumberingConfig & Record<string, unknown>) | null>(null);
  const [generatedNumber, setGeneratedNumber] = useState<string | null>(null);

  const buildNumber = (config: AutoNumberingConfig & Record<string, unknown>) => {
    const def = config.definition as Record<string, unknown>;
    const prefix = (def.prefix as string) || '';
    const separator = (def.separator as string) || '-';
    const padding = (def.padding as number) || 4;
    const suffix = (def.suffix as string) || '';
    const nextVal = (config.currentValue as number) + 1;
    return `${prefix}${separator}${String(nextVal).padStart(padding, '0')}${suffix ? separator + suffix : ''}`;
  };

  const columns: Column<AutoNumberingConfig & Record<string, unknown>>[] = [
    {
      key: 'objectType', header: 'Type Objet', sortable: true,
      render: (item) => <span className="font-medium">{item.objectType}</span>,
    },
    {
      key: 'definition', header: 'Définition',
      render: (item) => {
        const def = item.definition as Record<string, unknown>;
        return (
          <span className="text-sm font-mono">
            {def.prefix}{def.separator}{'*'.repeat(def.padding as number)}
            {def.suffix ? `${def.separator}${def.suffix}` : ''}
          </span>
        );
      },
    },
    {
      key: 'currentValue', header: 'Valeur Actuelle', sortable: true,
      render: (item) => (
        <span className="font-mono font-medium">
          {(() => {
            const def = item.definition as Record<string, unknown>;
            const prefix = (def.prefix as string) || '';
            const separator = (def.separator as string) || '-';
            const padding = (def.padding as number) || 4;
            const suffix = (def.suffix as string) || '';
            return `${prefix}${separator}${String(item.currentValue).padStart(padding, '0')}${suffix ? separator + suffix : ''}`;
          })()}
        </span>
      ),
    },
    {
      key: 'isActive', header: 'Actif', sortable: true,
      render: (item) => <StatusBadge statut={item.isActive ? 'ACTIF' : 'INACTIF'} label={item.isActive ? 'Actif' : 'Inactif'} />,
    },
  ];

  const handleToggleActive = (item: AutoNumberingConfig & Record<string, unknown>) => {
    setConfigs(configs.map((c) =>
      c.id === item.id ? { ...c, isActive: !c.isActive } : c
    ));
  };

  const handleGenerate = () => {
    if (selectedConfig) {
      const number = buildNumber(selectedConfig);
      setGeneratedNumber(number);
      setConfigs(configs.map((c) =>
        c.id === selectedConfig.id ? { ...c, currentValue: (c.currentValue as number) + 1 } : c
      ));
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Numérotation Automatique"
        description="Configuration de la numérotation automatique des objets"
      />

      <Card>
        <CardContent className="p-6">
          <DataTable
            data={configs}
            columns={columns}
            keyExtractor={(item) => item.id}
            searchable={false}
            pageSize={10}
            exportable={true}
            exportFilename="numerotation-auto"
            actions={(item) => [
              {
                label: 'Générer',
                onClick: () => {
                  setSelectedConfig(item);
                  setGeneratedNumber(null);
                  setGenerateOpen(true);
                },
              },
              {
                label: item.isActive ? 'Désactiver' : 'Activer',
                onClick: () => handleToggleActive(item),
              },
            ]}
          />
        </CardContent>
      </Card>

      {/* Generate Number Dialog */}
      <Dialog open={generateOpen} onOpenChange={setGenerateOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Hash className="h-5 w-5" />
              Générer un Numéro
            </DialogTitle>
          </DialogHeader>
          {selectedConfig && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <p className="text-sm text-muted-foreground">Type Objet</p>
                  <p className="font-medium">{selectedConfig.objectType}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Valeur Actuelle</p>
                  <p className="font-mono">{selectedConfig.currentValue}</p>
                </div>
              </div>

              {!generatedNumber ? (
                <Button onClick={handleGenerate} className="w-full gap-2">
                  <Play className="h-4 w-4" />
                  Générer le numéro suivant
                </Button>
              ) : (
                <div className="p-4 bg-emerald-50 dark:bg-emerald-950/20 border border-emerald-200 dark:border-emerald-800 rounded-md text-center">
                  <p className="text-sm text-muted-foreground mb-1">Numéro généré</p>
                  <p className="text-2xl font-mono font-bold text-emerald-700 dark:text-emerald-400">{generatedNumber}</p>
                </div>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
