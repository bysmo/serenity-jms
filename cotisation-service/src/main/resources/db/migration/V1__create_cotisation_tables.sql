-- ============================================================
-- V1__create_cotisation_tables.sql
-- Cotisation Service - Database Schema
-- ============================================================

-- =============================================
-- COTISATIONS
-- =============================================
CREATE TABLE cotisations (
    id              UUID PRIMARY KEY,
    libelle         VARCHAR(255) NOT NULL,
    description     TEXT,
    type            VARCHAR(30) NOT NULL,
    frequence       VARCHAR(30) NOT NULL,
    type_montant    VARCHAR(30) NOT NULL,
    montant         DECIMAL(19,2),
    caisse_id       UUID NOT NULL,
    created_by_membre_id UUID,
    admin_membre_id UUID,
    visibilite      VARCHAR(20) NOT NULL DEFAULT 'PUBLIQUE',
    tag             VARCHAR(100),
    actif           BOOLEAN NOT NULL DEFAULT TRUE,
    checksum        VARCHAR(64),
    date_debut      DATE,
    date_fin        DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cotisations_caisse_id ON cotisations(caisse_id);
CREATE INDEX idx_cotisations_created_by ON cotisations(created_by_membre_id);
CREATE INDEX idx_cotisations_admin ON cotisations(admin_membre_id);
CREATE INDEX idx_cotisations_type ON cotisations(type);
CREATE INDEX idx_cotisations_actif ON cotisations(actif);
CREATE INDEX idx_cotisations_visibilite ON cotisations(visibilite);

-- =============================================
-- PAIEMENTS
-- =============================================
CREATE TABLE paiements (
    id                  UUID PRIMARY KEY,
    cotisation_id       UUID NOT NULL REFERENCES cotisations(id),
    membre_id           UUID NOT NULL,
    montant             DECIMAL(19,2) NOT NULL,
    mode_paiement       VARCHAR(30) NOT NULL,
    statut              VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    reference           VARCHAR(255),
    wallet_alias_id     UUID,
    compte_externe_id   UUID,
    metadata            JSONB,
    date_paiement       TIMESTAMP,
    traite_par          UUID,
    date_traitement     TIMESTAMP,
    checksum            VARCHAR(64),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_paiements_cotisation_id ON paiements(cotisation_id);
CREATE INDEX idx_paiements_membre_id ON paiements(membre_id);
CREATE INDEX idx_paiements_statut ON paiements(statut);
CREATE INDEX idx_paiements_date_paiement ON paiements(date_paiement);
CREATE INDEX idx_paiements_mode_paiement ON paiements(mode_paiement);
CREATE INDEX idx_paiements_wallet_alias ON paiements(wallet_alias_id);

-- =============================================
-- ENGAGEMENTS
-- =============================================
CREATE TABLE engagements (
    id                  UUID PRIMARY KEY,
    cotisation_id       UUID NOT NULL REFERENCES cotisations(id),
    membre_id           UUID NOT NULL,
    montant_engage      DECIMAL(19,2) NOT NULL,
    montant_paye        DECIMAL(19,2) NOT NULL DEFAULT 0,
    periodicite         VARCHAR(30) NOT NULL,
    periode_debut       DATE NOT NULL,
    periode_fin         DATE NOT NULL,
    statut              VARCHAR(30) NOT NULL DEFAULT 'EN_COURS',
    tag                 VARCHAR(100),
    checksum            VARCHAR(64),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_engagements_cotisation_id ON engagements(cotisation_id);
CREATE INDEX idx_engagements_membre_id ON engagements(membre_id);
CREATE INDEX idx_engagements_statut ON engagements(statut);
CREATE INDEX idx_engagements_periode_debut ON engagements(periode_debut);
CREATE INDEX idx_engagements_periode_fin ON engagements(periode_fin);
CREATE INDEX idx_engagements_periodicite ON engagements(periodicite);

-- =============================================
-- COTISATION_ADHESIONS
-- =============================================
CREATE TABLE cotisation_adhesions (
    id                  UUID PRIMARY KEY,
    cotisation_id       UUID NOT NULL REFERENCES cotisations(id),
    membre_id           UUID NOT NULL,
    statut              VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    traite_par          UUID,
    date_traitement     TIMESTAMP,
    motif_refus         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_adhesion_membre_cotisation UNIQUE (membre_id, cotisation_id)
);

CREATE INDEX idx_adhesions_cotisation_id ON cotisation_adhesions(cotisation_id);
CREATE INDEX idx_adhesions_membre_id ON cotisation_adhesions(membre_id);
CREATE INDEX idx_adhesions_statut ON cotisation_adhesions(statut);

-- =============================================
-- COTISATION_VERSEMENT_DEMANDES
-- =============================================
CREATE TABLE cotisation_versement_demandes (
    id                  UUID PRIMARY KEY,
    cotisation_id       UUID NOT NULL REFERENCES cotisations(id),
    membre_id           UUID NOT NULL,
    montant_demande     DECIMAL(19,2) NOT NULL,
    statut              VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    traite_par          UUID,
    date_traitement     TIMESTAMP,
    motif_rejet         TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_versement_demandes_cotisation_id ON cotisation_versement_demandes(cotisation_id);
CREATE INDEX idx_versement_demandes_membre_id ON cotisation_versement_demandes(membre_id);
CREATE INDEX idx_versement_demandes_statut ON cotisation_versement_demandes(statut);

-- =============================================
-- REMBOURSEMENTS
-- =============================================
CREATE TABLE remboursements (
    id                  UUID PRIMARY KEY,
    cotisation_id       UUID NOT NULL REFERENCES cotisations(id),
    membre_id           UUID NOT NULL,
    montant             DECIMAL(19,2) NOT NULL,
    motif               TEXT,
    statut              VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    traite_par          UUID,
    date_traitement     TIMESTAMP,
    commentaire         TEXT,
    checksum            VARCHAR(64),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_remboursements_cotisation_id ON remboursements(cotisation_id);
CREATE INDEX idx_remboursements_membre_id ON remboursements(membre_id);
CREATE INDEX idx_remboursements_statut ON remboursements(statut);
