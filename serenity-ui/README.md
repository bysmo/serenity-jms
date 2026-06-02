# Serenity UI — Microservices Management Dashboard

Application web de gestion pour la suite de microservices **Serenity-JMS** (Java Microservices for Cooperative Financial Management).

## Architecture

Cette application Next.js 16 se connecte au backend Serenity-JMS via l'API Gateway (port 8080) et fournit une interface de gestion complète pour les 10 microservices :

| Domaine | Microservice | Routes |
|---------|-------------|--------|
| **Authentification** | identity-service | `/login` |
| **Tableau de bord** | — | `/dashboard` |
| **Membres** | member-service | `/members`, `/members/segments`, `/members/kyc`, `/members/parrainage` |
| **Cotisations** | cotisation-service | `/cotisations`, `/cotisations/engagements`, `/cotisations/paiements`, `/cotisations/adhesions`, `/cotisations/versement-demandes`, `/cotisations/remboursements` |
| **Épargne** | epargne-service | `/epargne`, `/epargne/souscriptions` |
| **Nano-Crédit** | nano-credit-service | `/nano-credits`, `/nano-credits/paliers` |
| **Paiements** | payment-gateway-service | `/payments`, `/payments/methods`, `/payments/paydunya`, `/payments/pispi` |
| **Notifications** | notification-service | `/notifications`, `/notifications/templates`, `/notifications/sms-gateways`, `/notifications/smtp` |
| **Administration** | admin-service | `/admin/settings`, `/admin/tags`, `/admin/announcements`, `/admin/audit-logs`, `/admin/auto-numbering` |
| **Collecteur** | collector-service | `/collector` |

## Stack Technique

- **Framework** : Next.js 16 (App Router)
- **Langage** : TypeScript 5
- **Styling** : Tailwind CSS 4 + shadcn/ui
- **State** : Zustand (auth) + React hooks
- **Tables** : TanStack React Table
- **Graphiques** : Recharts
- **Icônes** : Lucide React
- **Thème** : Clair/Sombre (next-themes)

## Fonctionnalités du DataTable

Le composant DataTable partagé offre des fonctionnalités avancées :

- **Pagination complète** : Navigation première/précédente/suivante/dernière page
- **Sélection du nombre d'éléments** : 5, 10, 25, 50 ou 100 éléments par page
- **Tri sur les colonnes** : Cliquez sur l'en-tête pour trier (ascendant/descendant)
- **Recherche** : Filtrage en temps réel par mots-clés
- **Filtres** : Filtrer par statut, type, catégorie, etc.
- **Sélection multiple** : Cochez des lignes pour des actions groupées
- **Export CSV** : Exportez les données filtrées en CSV
- **État de chargement** : Spinner pendant le chargement
- **État vide** : Message quand aucune donnée ne correspond

## Installation

```bash
# Installer les dépendances
cd serenity-ui
npm install

# Configurer l'environnement
cp .env.example .env.local
# Modifier NEXT_PUBLIC_API_BASE_URL si nécessaire
```

## Développement

```bash
npm run dev
```

L'application est accessible sur http://localhost:3000

## Connexion au Backend

Assurez-vous que les services suivants sont démarrés :

1. **Serenity-JMS Backend** — Suivre les instructions du dépôt principal
2. **API Gateway** — Port 8080 (point d'entrée unique)
3. **Keycloak** — Port 8180 (authentification OAuth2)
4. **PostgreSQL** — Bases de données par microservice
5. **Kafka** — Messagerie asynchrone

### Démarrage rapide du backend

```bash
cd serenity-jms/docker
docker-compose up -d
```

## Structure du Projet

```
src/
├── app/
│   ├── (auth)/              # Pages d'authentification
│   ├── (dashboard)/         # Pages protégées
│   │   ├── admin/           # Administration
│   │   ├── collector/       # Gestion collecteur
│   │   ├── cotisations/     # Gestion cotisations
│   │   ├── epargne/         # Gestion épargne
│   │   ├── members/         # Gestion membres
│   │   ├── nano-credits/    # Gestion nano-crédits
│   │   ├── notifications/   # Gestion notifications
│   │   └── payments/        # Gestion paiements
│   ├── globals.css
│   ├── layout.tsx
│   └── page.tsx
├── components/
│   ├── layout/              # Sidebar, Header
│   ├── shared/              # Composants réutilisables
│   │   ├── confirm-dialog.tsx
│   │   ├── data-table.tsx    # ← DataTable avec pagination
│   │   ├── page-header.tsx
│   │   ├── stat-card.tsx
│   │   └── status-badge.tsx
│   └── ui/                  # shadcn/ui components
├── lib/
│   ├── api-client.ts        # Client API complet
│   ├── constants.ts         # Constantes et mappings
│   └── utils.ts
├── store/
│   └── auth-store.ts        # Store d'authentification
└── types/
    └── index.ts             # Types TypeScript
```

## Authentification

L'application utilise Keycloak via OAuth2/OpenID Connect. Les rôles supportés :

- `SUPER_ADMIN` — Accès complet
- `GESTION_ADMIN` — Gestion administrative
- `GESTION_MEMBRES` — Gestion des membres
- `GESTION_EPARGNE` — Gestion épargne
- `SCOPE_GESTION_NANO_CREDITS` — Gestion nano-crédits
- `GESTIONNAIRE` — Opérations courantes
- `COLLECTEUR` — Gestion des collectes
- `MEMBRE` — Accès membre

## Licence

Propriétaire — © BYSMO
