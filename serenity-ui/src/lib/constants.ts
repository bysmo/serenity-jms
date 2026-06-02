// =============================================================================
// Serenity-JMS Constants
// ============================================================================

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export const CURRENCY = 'XOF';

export const formatCurrency = (amount: number): string =>
  new Intl.NumberFormat('fr-SN', { style: 'currency', currency: CURRENCY }).format(amount);

export const PAGE_SIZES = [10, 20, 50, 100] as const;

export const STATUT_COLORS: Record<string, string> = {
  // Membre
  ACTIF: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  INACTIF: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  SUSPENDU: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  BLOQUE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',

  // KYC
  NON_INITIE: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  EN_COURS: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  VALIDE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  REJETE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',

  // Paiement / Engagement
  EN_ATTENTE: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  CONFIRME: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  ECHOUE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  ANNULE: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  REMBOURSE: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',
  TERMINE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  RESILIE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',

  // Adhesion
  ACCEPTEE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  REFUSEE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',

  // Versement demande
  APPROUVEE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  REJETEE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  TRAITEE: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',

  // Remboursement
  APPROUVE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  EFFECTUE: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',

  // Nano Credit
  DEMANDE: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  ETUDE: 'bg-violet-100 text-violet-800 dark:bg-violet-900 dark:text-violet-300',
  ACCEPTE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  REFUSE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  DEBOURSE: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',
  EN_RETARD: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  REMBOURSE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',

  // Souscription
  ACTIVE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  CLOSE: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  ANNULEE: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',

  // Transaction
  PENDING: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  SUCCESS: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  FAILED: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  CANCELLED: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  SENT: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  DELIVERED: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',

  // Commission
  VERSEE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',

  // Annonce
  BROUILLON: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  PUBLIEE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  EXPIREE: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  ARCHIVEE: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',

  // Session
  OUVERTE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  FERMEE: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',

  // Legacy/alt keys
  ECHEC: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  RADIÉ: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  VALIDÉ: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  REJETÉ: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
  DEMANDE_EN_ATTENTE: 'bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300',
  EN_ETUDE: 'bg-violet-100 text-violet-800 dark:bg-violet-900 dark:text-violet-300',
  EN_REMBOURSEMENT: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',
  OUVERT: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  FERME: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
  RECONCILIE: 'bg-sky-100 text-sky-800 dark:bg-sky-900 dark:text-sky-300',
  PAYE: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900 dark:text-emerald-300',
  INACTIVE: 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300',
};

export const SERVICE_PORTS = {
  gateway: 8080,
  identity: 8081,
  admin: 8082,
  member: 8083,
  account: 8084,
  cotisation: 8085,
  notification: 8086,
  payment: 8087,
  collector: 8088,
  nanoCredit: 8089,
  epargne: 8090,
  discovery: 8761,
  config: 8888,
  keycloak: 8180,
} as const;

export const SERVICE_NAMES: Record<keyof typeof SERVICE_PORTS, string> = {
  gateway: 'API Gateway',
  identity: 'Identity Service',
  admin: 'Admin Service',
  member: 'Member Service',
  account: 'Account Service',
  cotisation: 'Cotisation Service',
  notification: 'Notification Service',
  payment: 'Payment Gateway Service',
  collector: 'Collector Service',
  nanoCredit: 'NanoCredit Service',
  epargne: 'Epargne Service',
  discovery: 'Discovery Server',
  config: 'Config Server',
  keycloak: 'Keycloak',
};

export const MODE_PAIEMENT_LABELS: Record<string, string> = {
  ESPECES: 'Espèces',
  ORANGE_MONEY: 'Orange Money',
  WAVE: 'Wave',
  FREE_MONEY: 'Free Money',
  VIREMENT: 'Virement',
  CARTE: 'Carte',
  CHEQUE: 'Chèque',
  AUTRE: 'Autre',
};

export const COTISATION_TYPE_LABELS: Record<string, string> = {
  OBLIGATOIRE: 'Obligatoire',
  OPTIONNELLE: 'Optionnelle',
  EPARGNE: 'Épargne',
  ORDINAIRE: 'Ordinaire',
  EXTRAORDINAIRE: 'Extraordinaire',
  SOCIALE: 'Sociale',
  SPECIALE: 'Spéciale',
};

export const FREQUENCE_LABELS: Record<string, string> = {
  JOURNALIERE: 'Journalière',
  HEBDOMADAIRE: 'Hebdomadaire',
  MENSUELLE: 'Mensuelle',
  TRIMESTRIELLE: 'Trimestrielle',
  ANNUELLE: 'Annuelle',
  UNIQUE: 'Unique',
};

export const KYC_DOCUMENT_TYPE_LABELS: Record<string, string> = {
  CARTE_IDENTITE: "Carte d'identité",
  PASSEPORT: 'Passeport',
  PERMIS_CONDUIRE: 'Permis de conduire',
  JUSTIFICATIF_DOMICILE: 'Justificatif de domicile',
  AUTRE: 'Autre',
};

export const NOTIFICATION_TYPE_LABELS: Record<string, string> = {
  INFO: 'Information',
  ALERTE: 'Alerte',
  RAPPEL: 'Rappel',
  CONFIRMATION: 'Confirmation',
};

export const NOTIFICATION_CHANNEL_LABELS: Record<string, string> = {
  SMS: 'SMS',
  EMAIL: 'Email',
  PUSH: 'Push',
  IN_APP: 'In-App',
};

export const ANNONCE_TYPE_LABELS: Record<string, string> = {
  INFO: 'Information',
  PROMO: 'Promotion',
  MAINTENANCE: 'Maintenance',
  URGENT: 'Urgent',
};

export const ANNOUNCEMENT_TYPE_LABELS: Record<string, string> = {
  INFO: 'Information',
  ALERTE: 'Alerte',
  PROMOTION: 'Promotion',
  MAINTENANCE: 'Maintenance',
};

export const SETTING_TYPE_LABELS: Record<string, string> = {
  STRING: 'Texte',
  INTEGER: 'Entier',
  DECIMAL: 'Décimal',
  BOOLEAN: 'Booléen',
  JSON: 'JSON',
  NUMBER: 'Nombre',
};
