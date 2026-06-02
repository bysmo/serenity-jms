'use client';

import { useState } from 'react';
import { PageHeader } from '@/components/shared/page-header';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { Plus, Save } from 'lucide-react';
import type { AppSetting, SettingType } from '@/types';

const SETTING_TYPE_LABELS: Record<string, string> = {
  STRING: 'Texte',
  INTEGER: 'Entier',
  DECIMAL: 'Décimal',
  BOOLEAN: 'Booléen',
  JSON: 'JSON',
};

const mockSettings: AppSetting[] = [
  { id: '1', cle: 'app.name', valeur: 'Serenity JMS', type: 'STRING', groupe: 'General', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: '2', cle: 'app.default_language', valeur: 'fr', type: 'STRING', groupe: 'General', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: '3', cle: 'payment.auto_confirm', valeur: 'true', type: 'BOOLEAN', groupe: 'Paiement', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-02-15' },
  { id: '4', cle: 'payment.max_amount', valeur: '5000000', type: 'INTEGER', groupe: 'Paiement', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-03-01' },
  { id: '5', cle: 'notification.sms_enabled', valeur: 'true', type: 'BOOLEAN', groupe: 'Notification', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: '6', cle: 'notification.email_from', valeur: 'noreply@serenity.sn', type: 'STRING', groupe: 'Notification', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-01-01' },
  { id: '7', cle: 'security.max_login_attempts', valeur: '5', type: 'INTEGER', groupe: 'Securité', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-04-10' },
  { id: '8', cle: 'security.otp_expiry_minutes', valeur: '5', type: 'INTEGER', groupe: 'Securité', checksum: '', createdAt: '2024-01-01', updatedAt: '2024-04-10' },
];

const emptyForm = { cle: '', valeur: '', type: 'STRING' as SettingType, groupe: 'General' };

export default function SettingsPage() {
  const [settings, setSettings] = useState(mockSettings);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [editedValues, setEditedValues] = useState<Record<string, string>>({});

  const groups = [...new Set(settings.map((s) => s.groupe))];

  const handleSave = () => {
    const newSetting: AppSetting = {
      id: String(settings.length + 1),
      cle: form.cle,
      valeur: form.valeur,
      type: form.type,
      groupe: form.groupe,
      checksum: '',
      createdAt: new Date().toISOString().slice(0, 10),
      updatedAt: new Date().toISOString().slice(0, 10),
    };
    setSettings([...settings, newSetting]);
    setDialogOpen(false);
    setForm(emptyForm);
  };

  const handleValueChange = (id: string, value: string) => {
    setEditedValues((prev) => ({ ...prev, [id]: value }));
  };

  const handleSaveValue = (id: string) => {
    if (editedValues[id] !== undefined) {
      setSettings(settings.map((s) =>
        s.id === id ? { ...s, valeur: editedValues[id], updatedAt: new Date().toISOString().slice(0, 10) } : s
      ));
      setEditedValues((prev) => {
        const next = { ...prev };
        delete next[id];
        return next;
      });
    }
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="Paramètres Application"
        description="Configuration des paramètres de l'application"
        actions={
          <Button size="sm" className="gap-2" onClick={() => setDialogOpen(true)}>
            <Plus className="h-4 w-4" />
            Nouveau Paramètre
          </Button>
        }
      />

      <Tabs defaultValue={groups[0]}>
        <TabsList>
          {groups.map((g) => (
            <TabsTrigger key={g} value={g}>{g}</TabsTrigger>
          ))}
        </TabsList>
        {groups.map((g) => (
          <TabsContent key={g} value={g} className="space-y-4 mt-4">
            {settings
              .filter((s) => s.groupe === g)
              .map((setting) => (
                <Card key={setting.id}>
                  <CardHeader className="pb-3">
                    <div className="flex items-center justify-between">
                      <CardTitle className="text-sm font-mono">{setting.cle}</CardTitle>
                      <div className="flex items-center gap-2">
                        <Badge variant="outline" className="text-xs">{SETTING_TYPE_LABELS[setting.type]}</Badge>
                        <Badge variant="secondary" className="text-xs">{setting.groupe}</Badge>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="flex items-center gap-3">
                      <Input
                        value={editedValues[setting.id] !== undefined ? editedValues[setting.id] : setting.valeur}
                        onChange={(e) => handleValueChange(setting.id, e.target.value)}
                        className="flex-1"
                      />
                      <Button
                        size="sm"
                        variant="outline"
                        className="gap-1 shrink-0"
                        onClick={() => handleSaveValue(setting.id)}
                        disabled={editedValues[setting.id] === undefined}
                      >
                        <Save className="h-3 w-3" />
                        Sauvegarder
                      </Button>
                    </div>
                    <p className="text-xs text-muted-foreground mt-2">
                      Dernière modification : {setting.updatedAt}
                    </p>
                  </CardContent>
                </Card>
              ))}
          </TabsContent>
        ))}
      </Tabs>

      {/* Create Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Nouveau Paramètre</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label>Clé</Label>
              <Input value={form.cle} onChange={(e) => setForm({ ...form, cle: e.target.value })} placeholder="app.setting_key" />
            </div>
            <div className="space-y-2">
              <Label>Valeur</Label>
              <Input value={form.valeur} onChange={(e) => setForm({ ...form, valeur: e.target.value })} placeholder="Valeur du paramètre" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Type</Label>
                <Select value={form.type} onValueChange={(v) => setForm({ ...form, type: v as SettingType })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="STRING">Texte</SelectItem>
                    <SelectItem value="INTEGER">Entier</SelectItem>
                    <SelectItem value="DECIMAL">Décimal</SelectItem>
                    <SelectItem value="BOOLEAN">Booléen</SelectItem>
                    <SelectItem value="JSON">JSON</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Groupe</Label>
                <Select value={form.groupe} onValueChange={(v) => setForm({ ...form, groupe: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {groups.map((g) => (
                      <SelectItem key={g} value={g}>{g}</SelectItem>
                    ))}
                    <SelectItem value="Autre">Autre</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>Annuler</Button>
            <Button onClick={handleSave}>Créer</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
