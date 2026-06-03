'use client';

import { useState, useEffect } from 'react';
import { apiClient } from '@/lib/api-client';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Shield, Users, Key, Layers, Loader2, Plus, Trash2 } from 'lucide-react';
import { toast } from 'sonner';

export default function AdminPage() {
  const [activeTab, setActiveTab] = useState('realms');
  const [loading, setLoading] = useState(false);
  const [realms, setRealms] = useState<any[]>([]);
  const [clients, setClients] = useState<any[]>([]);
  const [groups, setGroups] = useState<any[]>([]);
  const [roles, setRoles] = useState<any[]>([]);

  // New item form states
  const [newRealmName, setNewRealmName] = useState('');
  const [newClientName, setNewClientName] = useState('');
  const [newGroupName, setNewGroupName] = useState('');
  const [newRoleName, setNewRoleName] = useState('');

  const loadRealms = async () => {
    setLoading(true);
    try {
      const res = await apiClient.keycloak.realms.list();
      setRealms(res?.data || res || []);
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de charger les realms.' });
    } finally {
      setLoading(false);
    }
  };

  const loadClients = async () => {
    setLoading(true);
    try {
      const res = await apiClient.keycloak.clients.list();
      setClients(res?.data || res || []);
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de charger les clients.' });
    } finally {
      setLoading(false);
    }
  };

  const loadGroups = async () => {
    setLoading(true);
    try {
      const res = await apiClient.keycloak.groups.list();
      setGroups(res?.data || res || []);
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de charger les groupes.' });
    } finally {
      setLoading(false);
    }
  };

  const loadRoles = async () => {
    setLoading(true);
    try {
      const res = await apiClient.keycloak.roles.list();
      setRoles(res?.data || res || []);
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de charger les rôles.' });
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (tab: string) => {
    setActiveTab(tab);
    if (tab === 'realms') loadRealms();
    else if (tab === 'clients') loadClients();
    else if (tab === 'groups') loadGroups();
    else if (tab === 'roles') loadRoles();
  };

  useEffect(() => {
    loadRealms();
  }, []);

  const createRealm = async () => {
    if (!newRealmName.trim()) return;
    try {
      await apiClient.keycloak.realms.create({ realmName: newRealmName, enabled: true });
      toast.success('Realm créé', { description: `Le realm "${newRealmName}" a été créé.` });
      setNewRealmName('');
      loadRealms();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de créer le realm.' });
    }
  };

  const deleteRealm = async (name: string) => {
    try {
      await apiClient.keycloak.realms.delete(name);
      toast.success('Realm supprimé', { description: `Le realm "${name}" a été supprimé.` });
      loadRealms();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de supprimer le realm.' });
    }
  };

  const createClient = async () => {
    if (!newClientName.trim()) return;
    try {
      await apiClient.keycloak.clients.create({ clientId: newClientName, enabled: true, publicClient: false });
      toast.success('Client créé', { description: `Le client "${newClientName}" a été créé.` });
      setNewClientName('');
      loadClients();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de créer le client.' });
    }
  };

  const deleteClient = async (id: string) => {
    try {
      await apiClient.keycloak.clients.delete(id);
      toast.success('Client supprimé');
      loadClients();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de supprimer le client.' });
    }
  };

  const createGroup = async () => {
    if (!newGroupName.trim()) return;
    try {
      await apiClient.keycloak.groups.create({ name: newGroupName });
      toast.success('Groupe créé', { description: `Le groupe "${newGroupName}" a été créé.` });
      setNewGroupName('');
      loadGroups();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de créer le groupe.' });
    }
  };

  const deleteGroup = async (id: string) => {
    try {
      await apiClient.keycloak.groups.delete(id);
      toast.success('Groupe supprimé');
      loadGroups();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de supprimer le groupe.' });
    }
  };

  const createRole = async () => {
    if (!newRoleName.trim()) return;
    try {
      await apiClient.keycloak.roles.create({ name: newRoleName });
      toast.success('Rôle créé', { description: `Le rôle "${newRoleName}" a été créé.` });
      setNewRoleName('');
      loadRoles();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de créer le rôle.' });
    }
  };

  const deleteRole = async (name: string) => {
    try {
      await apiClient.keycloak.roles.delete(name);
      toast.success('Rôle supprimé');
      loadRoles();
    } catch (err: any) {
      toast.error('Erreur', { description: 'Impossible de supprimer le rôle.' });
    }
  };

  return (
    <div className="p-4 md:p-6 space-y-6">
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <Shield className="h-6 w-6 text-destructive" />
          <h1 className="text-2xl font-bold">Administration Keycloak</h1>
        </div>
        <p className="text-muted-foreground">
          Gérez les realms, clients, groupes et rôles de votre instance Keycloak.
        </p>
      </div>

      <Tabs value={activeTab} onValueChange={handleTabChange}>
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="realms" className="flex items-center gap-1">
            <Layers className="h-4 w-4" /> Realms
          </TabsTrigger>
          <TabsTrigger value="clients" className="flex items-center gap-1">
            <Key className="h-4 w-4" /> Clients
          </TabsTrigger>
          <TabsTrigger value="groups" className="flex items-center gap-1">
            <Users className="h-4 w-4" /> Groupes
          </TabsTrigger>
          <TabsTrigger value="roles" className="flex items-center gap-1">
            <Shield className="h-4 w-4" /> Rôles
          </TabsTrigger>
        </TabsList>

        {/* Realms Tab */}
        <TabsContent value="realms" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Créer un realm</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2">
                <Input
                  placeholder="Nom du realm"
                  value={newRealmName}
                  onChange={(e) => setNewRealmName(e.target.value)}
                />
                <Button onClick={createRealm} disabled={!newRealmName.trim()}>
                  <Plus className="h-4 w-4 mr-1" /> Créer
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Realms existants</CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="flex items-center justify-center py-8">
                  <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nom</TableHead>
                      <TableHead>Statut</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {realms.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={3} className="text-center text-muted-foreground">
                          Aucun realm trouvé
                        </TableCell>
                      </TableRow>
                    ) : (
                      realms.map((realm: any) => (
                        <TableRow key={realm.realmName || realm.realm}>
                          <TableCell className="font-medium">
                            {realm.realmName || realm.realm}
                          </TableCell>
                          <TableCell>
                            <Badge variant={realm.enabled ? 'default' : 'secondary'}>
                              {realm.enabled ? 'Actif' : 'Inactif'}
                            </Badge>
                          </TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => deleteRealm(realm.realmName || realm.realm)}
                            >
                              <Trash2 className="h-4 w-4 text-destructive" />
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* Clients Tab */}
        <TabsContent value="clients" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Créer un client</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2">
                <Input
                  placeholder="Client ID"
                  value={newClientName}
                  onChange={(e) => setNewClientName(e.target.value)}
                />
                <Button onClick={createClient} disabled={!newClientName.trim()}>
                  <Plus className="h-4 w-4 mr-1" /> Créer
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Clients existants</CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="flex items-center justify-center py-8">
                  <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Client ID</TableHead>
                      <TableHead>Statut</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {clients.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={3} className="text-center text-muted-foreground">
                          Aucun client trouvé
                        </TableCell>
                      </TableRow>
                    ) : (
                      clients.map((client: any) => (
                        <TableRow key={client.clientId || client.id}>
                          <TableCell className="font-medium">{client.clientId}</TableCell>
                          <TableCell>
                            <Badge variant={client.enabled ? 'default' : 'secondary'}>
                              {client.enabled ? 'Actif' : 'Inactif'}
                            </Badge>
                          </TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => deleteClient(client.id)}
                            >
                              <Trash2 className="h-4 w-4 text-destructive" />
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* Groups Tab */}
        <TabsContent value="groups" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Créer un groupe</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2">
                <Input
                  placeholder="Nom du groupe"
                  value={newGroupName}
                  onChange={(e) => setNewGroupName(e.target.value)}
                />
                <Button onClick={createGroup} disabled={!newGroupName.trim()}>
                  <Plus className="h-4 w-4 mr-1" /> Créer
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Groupes existants</CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="flex items-center justify-center py-8">
                  <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nom</TableHead>
                      <TableHead>Chemin</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {groups.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={3} className="text-center text-muted-foreground">
                          Aucun groupe trouvé
                        </TableCell>
                      </TableRow>
                    ) : (
                      groups.map((group: any, idx: number) => (
                        <TableRow key={group.id || idx}>
                          <TableCell className="font-medium">{group.name}</TableCell>
                          <TableCell className="text-muted-foreground">{group.path}</TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => deleteGroup(group.id)}
                            >
                              <Trash2 className="h-4 w-4 text-destructive" />
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* Roles Tab */}
        <TabsContent value="roles" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Créer un rôle</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2">
                <Input
                  placeholder="Nom du rôle"
                  value={newRoleName}
                  onChange={(e) => setNewRoleName(e.target.value)}
                />
                <Button onClick={createRole} disabled={!newRoleName.trim()}>
                  <Plus className="h-4 w-4 mr-1" /> Créer
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Rôles existants</CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="flex items-center justify-center py-8">
                  <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nom</TableHead>
                      <TableHead>Description</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {roles.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={3} className="text-center text-muted-foreground">
                          Aucun rôle trouvé
                        </TableCell>
                      </TableRow>
                    ) : (
                      roles.map((role: any) => (
                        <TableRow key={role.name}>
                          <TableCell className="font-medium">{role.name}</TableCell>
                          <TableCell className="text-muted-foreground">
                            {role.description || '-'}
                          </TableCell>
                          <TableCell className="text-right">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => deleteRole(role.name)}
                            >
                              <Trash2 className="h-4 w-4 text-destructive" />
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
