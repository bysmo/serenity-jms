-- V1__create_member_tables.sql
-- Member Service Database Schema

-- ========================================
-- SEGMENTS
-- ========================================
CREATE TABLE segments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    couleur VARCHAR(20),
    icone VARCHAR(50),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ========================================
-- MEMBRES
-- ========================================
CREATE TABLE membres (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    telephone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255),
    statut VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    segment_id UUID NOT NULL REFERENCES segments(id),

    -- Location fields
    adresse_ligne1 VARCHAR(255),
    adresse_ligne2 VARCHAR(255),
    ville VARCHAR(100),
    region VARCHAR(100),
    code_postal VARCHAR(20),
    pays VARCHAR(100) NOT NULL DEFAULT 'SN',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,

    -- PIN fields
    code_pin VARCHAR(255),
    pin_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    pin_attempts INTEGER NOT NULL DEFAULT 0,
    pin_locked_until TIMESTAMP,
    pin_mode VARCHAR(20) NOT NULL DEFAULT 'EACH_TIME',

    -- Nano-credit fields
    nano_credit_eligible BOOLEAN NOT NULL DEFAULT FALSE,
    nano_credit_limite DECIMAL(19, 2) NOT NULL DEFAULT 0,
    nano_credit_solde DECIMAL(19, 2) NOT NULL DEFAULT 0,

    -- Parrainage fields
    parrain_id UUID REFERENCES membres(id),
    code_parrainage VARCHAR(20) UNIQUE,
    parrainage_actif BOOLEAN NOT NULL DEFAULT FALSE,
    niveau_parrainage INTEGER NOT NULL DEFAULT 0,

    -- Verification fields
    email_verifie BOOLEAN NOT NULL DEFAULT FALSE,
    telephone_verifie BOOLEAN NOT NULL DEFAULT FALSE,
    kyc_niveau VARCHAR(20) NOT NULL DEFAULT 'NONE',

    -- Push notification fields
    push_token VARCHAR(500),
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,

    -- Checksum for data integrity
    checksum VARCHAR(64),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_membres_numero ON membres(numero);
CREATE INDEX idx_membres_telephone ON membres(telephone);
CREATE INDEX idx_membres_email ON membres(email);
CREATE INDEX idx_membres_statut ON membres(statut);
CREATE INDEX idx_membres_segment_id ON membres(segment_id);
CREATE INDEX idx_membres_parrain_id ON membres(parrain_id);
CREATE INDEX idx_membres_code_parrainage ON membres(code_parrainage);

-- ========================================
-- KYC VERIFICATIONS
-- ========================================
CREATE TABLE kyc_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id UUID NOT NULL REFERENCES membres(id),
    statut VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    niveau VARCHAR(30) NOT NULL DEFAULT 'LEVEL_1',
    validated_by UUID,
    validated_at TIMESTAMP,
    rejected_by UUID,
    rejected_at TIMESTAMP,
    motif_rejet TEXT,
    commentaire TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_kyc_verifications_membre_id ON kyc_verifications(membre_id);
CREATE INDEX idx_kyc_verifications_statut ON kyc_verifications(statut);

-- ========================================
-- KYC DOCUMENTS
-- ========================================
CREATE TABLE kyc_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kyc_verification_id UUID NOT NULL REFERENCES kyc_verifications(id),
    type_document VARCHAR(50) NOT NULL,
    nom_fichier VARCHAR(255) NOT NULL,
    url_fichier VARCHAR(500) NOT NULL,
    taille_fichier BIGINT,
    type_mime VARCHAR(100),
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_kyc_documents_kyc_verification_id ON kyc_documents(kyc_verification_id);

-- ========================================
-- MEMBRE COMPTES EXTERNES
-- ========================================
CREATE TABLE membre_comptes_externes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id UUID NOT NULL REFERENCES membres(id),
    type VARCHAR(30) NOT NULL,
    identifiant VARCHAR(255) NOT NULL,
    libelle VARCHAR(255),
    fournisseur VARCHAR(100),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_membre_comptes_externes_membre_id ON membre_comptes_externes(membre_id);
CREATE INDEX idx_membre_comptes_externes_identifiant ON membre_comptes_externes(identifiant);

-- ========================================
-- MEMBRE WALLET ALIASES
-- ========================================
CREATE TABLE membre_wallet_aliases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id UUID NOT NULL REFERENCES membres(id),
    alias VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL DEFAULT 'USERNAME',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_membre_wallet_aliases_membre_id ON membre_wallet_aliases(membre_id);
CREATE INDEX idx_membre_wallet_aliases_alias ON membre_wallet_aliases(alias);

-- ========================================
-- PARRAINAGE CONFIGS
-- ========================================
CREATE TABLE parrainage_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actif BOOLEAN NOT NULL DEFAULT FALSE,
    type_remuneration VARCHAR(30) NOT NULL DEFAULT 'FIXE',
    montant_fixe DECIMAL(19, 2) NOT NULL DEFAULT 0,
    pourcentage DECIMAL(5, 2) NOT NULL DEFAULT 0,
    declencheur VARCHAR(50) NOT NULL DEFAULT 'INSCRIPTION',
    niveau_max INTEGER NOT NULL DEFAULT 1,
    delai_disponibilite_jours INTEGER NOT NULL DEFAULT 30,
    plafond_mensuel DECIMAL(19, 2) NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ========================================
-- PARRAINAGE COMMISSIONS
-- ========================================
CREATE TABLE parrainage_commissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parrain_id UUID NOT NULL REFERENCES membres(id),
    filleul_id UUID NOT NULL REFERENCES membres(id),
    config_id UUID NOT NULL REFERENCES parrainage_configs(id),
    niveau INTEGER NOT NULL DEFAULT 1,
    declencheur VARCHAR(50) NOT NULL,
    montant DECIMAL(19, 2) NOT NULL,
    statut VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    disponible_le TIMESTAMP NOT NULL,
    reclame_le TIMESTAMP,
    paye_le TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_parrainage_commissions_parrain_id ON parrainage_commissions(parrain_id);
CREATE INDEX idx_parrainage_commissions_filleul_id ON parrainage_commissions(filleul_id);
CREATE INDEX idx_parrainage_commissions_statut ON parrainage_commissions(statut);
CREATE INDEX idx_parrainage_commissions_disponible_le ON parrainage_commissions(disponible_le);

-- ========================================
-- SEED DATA
-- ========================================
INSERT INTO segments (nom, slug, couleur, icone, is_default, actif) VALUES
('Standard', 'standard', '#3498db', 'user', true, true),
('Premium', 'premium', '#e67e22', 'star', false, true),
('VIP', 'vip', '#9b59b6', 'crown', false, true);
