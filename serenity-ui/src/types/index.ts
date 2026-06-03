// =============================================================================
// Serenity-JMS Central Types
// Comprehensive TypeScript types for ALL microservices
// =============================================================================

// -----------------------------------------------------------------------------
// Common / Shared Types
// -----------------------------------------------------------------------------

export interface ApiResponse<T> {
  data: T;
  message: string;
  status: number;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ErrorResponse {
  status: number;
  message: string;
  errors?: FieldError[];
  timestamp: string;
}

export interface SearchParams {
  query?: string;
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'ASC' | 'DESC';
}

// -----------------------------------------------------------------------------
// Identity Service Types
// -----------------------------------------------------------------------------

export interface LoginRequest {
  username: string;
  password: string;
  clientId?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

export interface UserInfoResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

// -----------------------------------------------------------------------------
// Member Service Types
// -----------------------------------------------------------------------------

export interface Membre {
  id: string;
  numero: string;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  statut: MembreStatut;
  segmentId: string;
  segmentNom?: string;
  adresseLigne1?: string;
  adresseLigne2?: string;
  ville?: string;
  region?: string;
  codePostal?: string;
  pays: string;
  pinEnabled: boolean;
  pinMode?: PinMode;
  nanoCreditEligible: boolean;
  nanoCreditLimite?: number;
  nanoCreditSolde?: number;
  parrainId?: string;
  codeParrainage?: string;
  parrainageActif: boolean;
  niveauParrainage?: number;
  emailVerifie: boolean;
  telephoneVerifie: boolean;
  kycNiveau: string;
  checksum?: string;
  createdAt: string;
  updatedAt: string;
}

export type MembreStatut = 'EN_ATTENTE' | 'ACTIF' | 'SUSPENDU' | 'RADIÉ';

export type PinMode = 'CODE_PIN' | 'OTP' | 'BIOMETRIQUE';

export interface MembreRequest {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  segmentId?: string;
  adresseLigne1?: string;
  adresseLigne2?: string;
  ville?: string;
  region?: string;
  codePostal?: string;
  pays?: string;
}

export interface MembreRegistrationRequest extends MembreRequest {
  password: string;
}

export interface MembreDashboardResponse {
  membreId: string;
  soldeTotal: number;
  epargneTotal: number;
  nanoCreditSolde: number;
  cotisationsActives: number;
  engagementsEnCours: number;
  dernierPaiement?: {
    date: string;
    montant: number;
    cotisation: string;
  };
}

// Segment

export interface Segment {
  id: string;
  nom: string;
  slug: string;
  description?: string;
  couleur?: string;
  icone?: string;
  isDefault: boolean;
  actif: boolean;
  createdAt: string;
}

export interface SegmentRequest {
  nom: string;
  description?: string;
  couleur?: string;
  icone?: string;
  isDefault?: boolean;
  actif?: boolean;
}

// KYC Verification

export interface KycVerification {
  id: string;
  membreId: string;
  statut: KycStatut;
  niveau: string;
  validatedBy?: string;
  validatedAt?: string;
  rejectedBy?: string;
  rejectedAt?: string;
  motifRejet?: string;
  documents: KycDocument[];
  createdAt: string;
}

export type KycStatut = 'EN_ATTENTE' | 'EN_COURS' | 'VALIDÉ' | 'REJETÉ';

export interface KycDocument {
  id: string;
  typeDocument: KycDocumentType;
  nomFichier: string;
  urlFichier: string;
  tailleFichier: number;
  uploadedAt: string;
}

export type KycDocumentType =
  | 'CARTE_IDENTITE'
  | 'PASSEPORT'
  | 'PERMIS_CONDUIRE'
  | 'JUSTIFICATIF_DOMICILE'
  | 'AUTRE';

export interface KycVerificationRequest {
  typeDocument: KycDocumentType;
  documentBase64: string;
  nomFichier: string;
}

// Parrainage (Referral)

export interface ParrainageConfig {
  id: string;
  actif: boolean;
  typeRemuneration: TypeRemuneration;
  montantFixe?: number;
  pourcentage?: number;
  declencheur: DeclencheurParrainage;
  niveauMax: number;
  delaiDisponibiliteJours: number;
  plafondMensuel?: number;
}

export type TypeRemuneration = 'FIXE' | 'POURCENTAGE';

export type DeclencheurParrainage =
  | 'INSCRIPTION'
  | 'PREMIER_PAIEMENT'
  | 'PREMIERE_COTISATION';

export interface ParrainageCommission {
  id: string;
  parrainId: string;
  filleulId: string;
  montant: number;
  statut: CommissionStatut;
  dateDisponibilite: string;
  dateReclamation?: string;
  createdAt: string;
}

export type CommissionStatut = 'DISPONIBLE' | 'RECLAMEE' | 'PAYEE';

export interface ApplyParrainageRequest {
  codeParrainage: string;
}

// Compte Externe (External Account)

export interface CompteExterne {
  id: string;
  membreId: string;
  type: CompteExterneType;
  identifiant: string;
  libelle: string;
  fournisseur?: string;
  isDefault: boolean;
  actif: boolean;
}

export type CompteExterneType =
  | 'ORANGE_MONEY'
  | 'WAVE'
  | 'FREE_MONEY'
  | 'SKRILL'
  | 'BANK_ACCOUNT'
  | 'AUTRE';

export interface CompteExterneRequest {
  type: CompteExterneType;
  identifiant: string;
  libelle: string;
  fournisseur?: string;
  isDefault?: boolean;
  actif?: boolean;
}

// PIN Management

export interface PinSetupRequest {
  pin: string;
  confirmPin: string;
}

export interface PinVerifyRequest {
  pin: string;
}

export interface PinEnableRequest {
  pinMode: PinMode;
}

// -----------------------------------------------------------------------------
// Cotisation Service Types
// -----------------------------------------------------------------------------

export interface Cotisation {
  id: string;
  libelle: string;
  description?: string;
  type: CotisationType;
  frequence: Frequence;
  typeMontant: TypeMontant;
  montant: number;
  caisseId?: string;
  createdByMembreId?: string;
  adminMembreId?: string;
  visibilite: Visibilite;
  tag?: string;
  actif: boolean;
  dateDebut?: string;
  dateFin?: string;
  checksum?: string;
  createdAt: string;
}

export type CotisationType = 'ORDINAIRE' | 'EXTRAORDINAIRE' | 'SOCIALE' | 'SPECIALE';

export type Frequence =
  | 'JOURNALIERE'
  | 'HEBDOMADAIRE'
  | 'MENSUELLE'
  | 'TRIMESTRIELLE'
  | 'ANNUELLE'
  | 'UNIQUE';

export type TypeMontant = 'FIXE' | 'LIBRE' | 'MINIMUM';

export type Visibilite = 'PUBLIQUE' | 'PRIVEE' | 'GROUPE';

export interface CotisationRequest {
  libelle: string;
  description?: string;
  type: CotisationType;
  frequence: Frequence;
  typeMontant: TypeMontant;
  montant: number;
  visibilite?: Visibilite;
  tag?: string;
  actif?: boolean;
  dateDebut?: string;
  dateFin?: string;
}

// Engagement

export interface Engagement {
  id: string;
  cotisationId: string;
  cotisationLibelle?: string;
  membreId: string;
  montantEngage: number;
  montantPaye: number;
  periodicite: Frequence;
  periodeDebut: string;
  periodeFin: string;
  statut: EngagementStatut;
  tag?: string;
  createdAt: string;
}

export type EngagementStatut = 'EN_COURS' | 'TERMINE' | 'ANNULE' | 'SUSPENDU';

export interface EngagementRequest {
  cotisationId: string;
  membreId: string;
  montantEngage: number;
  periodicite: Frequence;
  periodeDebut: string;
  periodeFin: string;
}

// Paiement

export interface Paiement {
  id: string;
  cotisationId: string;
  membreId: string;
  montant: number;
  modePaiement: ModePaiement;
  statut: PaiementStatut;
  reference?: string;
  datePaiement?: string;
  traitePar?: string;
  dateTraitement?: string;
  checksum?: string;
  createdAt: string;
}

export type ModePaiement =
  | 'ESPECES'
  | 'ORANGE_MONEY'
  | 'WAVE'
  | 'FREE_MONEY'
  | 'VIREMENT'
  | 'CHEQUE'
  | 'AUTRE';

export type PaiementStatut = 'EN_ATTENTE' | 'CONFIRME' | 'ECHEC' | 'ANNULE';

export interface PaiementRequest {
  cotisationId: string;
  membreId: string;
  montant: number;
  modePaiement: ModePaiement;
}

// Adhesion

export interface Adhesion {
  id: string;
  cotisationId: string;
  membreId: string;
  statut: AdhesionStatut;
  traitePar?: string;
  dateTraitement?: string;
  motifRefus?: string;
  createdAt: string;
}

export type AdhesionStatut = 'EN_ATTENTE' | 'ACCEPTEE' | 'REFUSEE';

export interface AdhesionRequest {
  cotisationId: string;
  membreId: string;
}

// Versement Demande

export interface VersementDemande {
  id: string;
  cotisationId: string;
  membreId: string;
  montantDemande: number;
  statut: VersementDemandeStatut;
  traitePar?: string;
  dateTraitement?: string;
  motifRejet?: string;
  createdAt: string;
}

export type VersementDemandeStatut = 'EN_ATTENTE' | 'TRAITEE' | 'REJETEE';

export interface VersementDemandeRequest {
  cotisationId: string;
  membreId: string;
  montantDemande: number;
}

// Remboursement

export interface Remboursement {
  id: string;
  cotisationId: string;
  membreId: string;
  montant: number;
  motif?: string;
  statut: RemboursementStatut;
  traitePar?: string;
  dateTraitement?: string;
  commentaire?: string;
  createdAt: string;
}

export type RemboursementStatut = 'EN_ATTENTE' | 'APPROUVE' | 'REJETE' | 'PAYE';

export interface RemboursementRequest {
  cotisationId: string;
  membreId: string;
  montant: number;
  motif?: string;
}

// -----------------------------------------------------------------------------
// Epargne Service Types
// -----------------------------------------------------------------------------

export interface EpargnePlan {
  id: string;
  nom: string;
  description?: string;
  montantMin: number;
  montantMax: number;
  frequence: EpargneFrequence;
  tauxRemuneration: number;
  dureeMois: number;
  caisseId?: string;
  heureLimitePaiement?: string;
  delaiRappelHeures?: number;
  intervalleRappelMinutes?: number;
  actif: boolean;
  checksum?: string;
  createdAt: string;
}

export type EpargneFrequence = 'JOURNALIERE' | 'HEBDOMADAIRE' | 'MENSUELLE' | 'TRIMESTRIELLE';

export interface EpargnePlanRequest {
  nom: string;
  description?: string;
  montantMin: number;
  montantMax: number;
  frequence: EpargneFrequence;
  tauxRemuneration: number;
  dureeMois: number;
  actif?: boolean;
}

// Epargne Souscription

export interface EpargneSouscription {
  id: string;
  membreId: string;
  planId: string;
  plan?: EpargnePlan;
  montant: number;
  statut: SouscriptionStatut;
  dateSouscription: string;
  dateFin?: string;
}

export type SouscriptionStatut = 'ACTIVE' | 'TERMINEE' | 'ANNULEE';

export interface SouscriptionRequest {
  membreId: string;
  planId: string;
  montant: number;
}

// Epargne Echeance

export interface EpargneEcheance {
  id: string;
  souscriptionId: string;
  numeroEcheance: number;
  montant: number;
  dateEcheance: string;
  statut: EcheanceStatut;
  datePaiement?: string;
  montantPaye?: number;
  montantPenalite?: number;
}

export type EcheanceStatut = 'EN_ATTENTE' | 'PAYEE' | 'EN_RETARD' | 'ANNULEE';

// Epargne Versement

export interface EpargneVersement {
  id: string;
  souscriptionId: string;
  echeanceId?: string;
  montant: number;
  dateVersement: string;
  modePaiement?: string;
  reference?: string;
}

export interface VersementRequest {
  souscriptionId: string;
  montant: number;
  modePaiement?: string;
}

// -----------------------------------------------------------------------------
// NanoCredit Service Types
// -----------------------------------------------------------------------------

export interface NanoCredit {
  id: string;
  membreId: string;
  palierId: string;
  palier?: NanoCreditPalier;
  montant: number;
  statut: NanoCreditStatut;
  withdrawMode?: string;
  scoreAi?: number;
  scoreHumain?: number;
  scoreGlobal?: number;
  dateOctroi?: string;
  dateFinRemboursement?: string;
  montantPenalite?: number;
  joursRetard?: number;
  createdBy?: string;
  createdAt: string;
}

export type NanoCreditStatut =
  | 'DEMANDE_EN_ATTENTE'
  | 'EN_ETUDE'
  | 'ACCEPTE'
  | 'REFUSE'
  | 'DEBOURSE'
  | 'EN_REMBOURSEMENT'
  | 'REMBOURSE'
  | 'EN_RETARD'
  | 'ANNULE';

export interface NanoCreditRequest {
  membreId: string;
  palierId: string;
  montant: number;
  withdrawMode?: string;
}

export interface EtudeRequest {
  scoreAi: number;
  scoreHumain: number;
  commentaire?: string;
}

// NanoCredit Palier

export interface NanoCreditPalier {
  id: string;
  numero: string;
  nom: string;
  montantPlafond: number;
  dureeJours: number;
  frequenceRemboursement: FrequenceRemboursement;
  tauxInteret: number;
  penaliteParJour: number;
  minMontantTotalRembourse: number;
  minEpargneCumulee: number;
  minEpargnePercent: number;
  minGarantQualite: number;
  pourcentagePartageGarant: number;
  actif: boolean;
}

export type FrequenceRemboursement = 'JOURNALIERE' | 'HEBDOMADAIRE' | 'MENSUELLE';

export interface NanoCreditPalierRequest {
  numero: string;
  nom: string;
  montantPlafond: number;
  dureeJours: number;
  frequenceRemboursement: FrequenceRemboursement;
  tauxInteret: number;
  penaliteParJour: number;
  actif?: boolean;
}

// NanoCredit Echeance

export interface NanoCreditEcheance {
  id: string;
  nanoCreditId: string;
  numeroEcheance: number;
  montant: number;
  montantPenalite?: number;
  dateEcheance: string;
  statut: EcheanceStatut;
  datePaiement?: string;
  montantPaye?: number;
}

// NanoCredit Versement

export interface NanoCreditVersement {
  id: string;
  nanoCreditId: string;
  echeanceId?: string;
  montant: number;
  dateVersement: string;
  modePaiement?: string;
  reference?: string;
}

// NanoCredit Garant

export interface NanoCreditGarant {
  id: string;
  nanoCreditId: string;
  garantMembreId: string;
  qualite: number;
  soldeGarantie: number;
  pourcentagePartage: number;
  statut: GarantStatut;
}

export type GarantStatut = 'ACTIF' | 'LIBERE' | 'DEFAILLANT';

// -----------------------------------------------------------------------------
// Admin Service Types
// -----------------------------------------------------------------------------

export interface AppSetting {
  id: string;
  cle: string;
  valeur: string;
  type: SettingType;
  groupe: string;
  checksum?: string;
  createdAt: string;
  updatedAt: string;
}

export type SettingType = 'STRING' | 'INTEGER' | 'DECIMAL' | 'BOOLEAN' | 'JSON';

export interface AppSettingRequest {
  cle: string;
  valeur: string;
  type: SettingType;
  groupe: string;
}

// Tag

export interface Tag {
  id: string;
  nom: string;
  type: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TagRequest {
  nom: string;
  type: string;
  description?: string;
}

// Annonce (Announcement)

export interface Annonce {
  id: string;
  titre: string;
  contenu: string;
  dateDebut: string;
  dateFin: string;
  statut: AnnonceStatut;
  type: AnnonceType;
  ordre: number;
  segment?: string;
  createdAt: string;
}

export type AnnonceStatut = 'ACTIVE' | 'INACTIVE' | 'EXPIREE';

export type AnnonceType = 'INFO' | 'ALERTE' | 'PROMOTION' | 'MAINTENANCE';

export interface AnnonceRequest {
  titre: string;
  contenu: string;
  dateDebut: string;
  dateFin: string;
  type: AnnonceType;
  ordre?: number;
  segment?: string;
}

// Audit Log

export interface AuditLog {
  id: string;
  actorType: ActorType;
  actorId: string;
  action: string;
  model: string;
  modelId: string;
  oldValues?: Record<string, unknown> | null;
  newValues?: Record<string, unknown> | null;
  ipAddress?: string;
  userAgent?: string;
  createdAt: string;
}

export type ActorType = 'SYSTEM' | 'ADMIN' | 'MEMBRE' | 'COLLECTEUR';

// Auto Numbering Config

export interface AutoNumberingConfig {
  id: string;
  objectType: string;
  definition: Record<string, unknown>;
  currentValue: number;
  isActive: boolean;
  checksum?: string;
}

export interface AutoNumberingConfigRequest {
  objectType: string;
  definition: Record<string, unknown>;
}

// Email Template

export interface EmailTemplate {
  id: string;
  nom: string;
  sujet: string;
  corps: string;
  type: string;
  actif: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface EmailTemplateRequest {
  nom: string;
  sujet: string;
  corps: string;
  type: string;
  actif?: boolean;
}

// -----------------------------------------------------------------------------
// Payment Gateway Service Types
// -----------------------------------------------------------------------------

export interface PaymentTransaction {
  id: string;
  reference: string;
  externalReference?: string;
  gateway: PaymentGateway;
  transactionType: TransactionType;
  statut: TransactionStatut;
  telephone?: string;
  montant: number;
  fees?: number;
  netAmount?: number;
  currency: string;
  description?: string;
  errorMessage?: string;
  membreId?: string;
  createdBy?: string;
  confirmedAt?: string;
  failedAt?: string;
  createdAt: string;
}

export type PaymentGateway = 'PAYDUNYA' | 'PISPI';

export type TransactionType = 'COLLECTION' | 'DISBURSEMENT';

export type TransactionStatut = 'PENDING' | 'SUCCESS' | 'FAILED' | 'CANCELLED';

// PayDunya Config

export interface PayDunyaConfig {
  id: string;
  organisationId: string;
  masterKey: string;
  privateKey: string;
  publicKey: string;
  token: string;
  mode: string;
  ipnUrl?: string;
  isActive: boolean;
}

export interface PayDunyaConfigRequest {
  masterKey: string;
  privateKey: string;
  publicKey: string;
  token: string;
  mode?: string;
  ipnUrl?: string;
}

// PiSpi Config

export interface PiSpiConfig {
  id: string;
  organisationId: string;
  clientId: string;
  clientSecret: string;
  apiKey: string;
  payeAlias: string;
  mode: string;
  callbackUrl?: string;
  isActive: boolean;
}

export interface PiSpiConfigRequest {
  clientId: string;
  clientSecret: string;
  apiKey: string;
  payeAlias: string;
  mode?: string;
  callbackUrl?: string;
}

// Payment Method

export interface PaymentMethod {
  id: string;
  code: string;
  name: string;
  description?: string;
  gateway: PaymentGateway;
  isActive: boolean;
  minAmount?: number;
  maxAmount?: number;
  feesPercentage?: number;
  feesFixed?: number;
}

export interface PaymentMethodRequest {
  code: string;
  name: string;
  description?: string;
  gateway: PaymentGateway;
  isActive?: boolean;
  minAmount?: number;
  maxAmount?: number;
  feesPercentage?: number;
  feesFixed?: number;
}

// -----------------------------------------------------------------------------
// Notification Service Types
// -----------------------------------------------------------------------------

export interface NotificationLog {
  id: string;
  type: NotificationType;
  recipientId: string;
  recipientType: RecipientType;
  channel: NotificationChannel;
  subject?: string;
  content: string;
  status: NotificationStatus;
  errorMessage?: string;
  providerReference?: string;
  sentAt?: string;
  createdAt: string;
}

export type NotificationType = 'EMAIL' | 'SMS' | 'PUSH' | 'IN_APP';

export type NotificationChannel = 'EMAIL' | 'SMS' | 'PUSH' | 'IN_APP';

export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED' | 'DELIVERED';

export type RecipientType = 'MEMBRE' | 'ADMIN' | 'COLLECTEUR';

// SMS Gateway

export interface SmsGateway {
  id: string;
  nom: string;
  providerCode: string;
  apiUrl: string;
  apiKey: string;
  senderName: string;
  isActive: boolean;
  ordre: number;
  maxRetries: number;
  timeoutSeconds: number;
}

export interface SmsGatewayRequest {
  nom: string;
  providerCode: string;
  apiUrl: string;
  apiKey: string;
  senderName: string;
  isActive?: boolean;
  ordre?: number;
  maxRetries?: number;
  timeoutSeconds?: number;
}

// SMTP Configuration

export interface SmtpConfiguration {
  id: string;
  host: string;
  port: number;
  username: string;
  password: string;
  authEnabled: boolean;
  starttlsEnabled: boolean;
  sslEnabled: boolean;
  fromEmail: string;
  fromName: string;
  actif: boolean;
}

export interface SmtpConfigurationRequest {
  host: string;
  port: number;
  username: string;
  password: string;
  authEnabled?: boolean;
  starttlsEnabled?: boolean;
  sslEnabled?: boolean;
  fromEmail: string;
  fromName: string;
  actif?: boolean;
}

// -----------------------------------------------------------------------------
// Collector Service Types
// -----------------------------------------------------------------------------

export interface CollecteSession {
  id: string;
  userId: string;
  dateSession: string;
  statut: SessionStatut;
  montantOuverture?: number;
  montantFermeture?: number;
  openedAt: string;
  closedAt?: string;
}

export type SessionStatut = 'OUVERT' | 'FERME' | 'RECONCILIE';

export interface Collecte {
  id: string;
  collecteSessionId: string;
  membreId: string;
  typeCollecte: TypeCollecte;
  montant: number;
  echeanceType?: string;
  echeanceId?: string;
  otpCode?: string;
  isConfirmed: boolean;
  confirmedAt?: string;
  referenceTransaction?: string;
}

export type TypeCollecte = 'COTISATION' | 'EPARGNE' | 'NANO_CREDIT';

export interface CollecteRequest {
  membreId: string;
  typeCollecte: TypeCollecte;
  montant: number;
  echeanceType?: string;
  echeanceId?: string;
  otpCode?: string;
}

export interface CollecteSessionSummary {
  sessionId: string;
  totalCollectes: number;
  totalMontant: number;
  byType: Record<string, { count: number; montant: number }>;
  confirmedCount: number;
  unconfirmedCount: number;
}
