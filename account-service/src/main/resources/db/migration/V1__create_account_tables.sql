-- V1__create_account_tables.sql
-- Account Service Database Schema - Caisses, Double-Entry Accounting, Transfers

-- ========================================
-- CAISSES (Cash Boxes)
-- ========================================
CREATE TABLE caisses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero VARCHAR(30) NOT NULL UNIQUE,
    nom VARCHAR(200) NOT NULL,
    type VARCHAR(30) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    solde_initial DECIMAL(19, 2) NOT NULL DEFAULT 0,
    membre_id UUID,
    user_id UUID,
    numero_core_banking VARCHAR(50),
    seuil_alerte DECIMAL(19, 2) NOT NULL DEFAULT 0,
    details JSONB,
    checksum VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_caisses_membre_id ON caisses(membre_id);
CREATE INDEX idx_caisses_type ON caisses(type);
CREATE INDEX idx_caisses_numero_core_banking ON caisses(numero_core_banking);
CREATE INDEX idx_caisses_statut ON caisses(statut);

-- ========================================
-- MOUVEMENTS CAISSE (Cash Movements)
-- ========================================
CREATE TABLE mouvements_caisse (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    caisse_id UUID NOT NULL REFERENCES caisses(id),
    type VARCHAR(50) NOT NULL,
    sens VARCHAR(10) NOT NULL,
    montant DECIMAL(19, 2) NOT NULL,
    solde_avant DECIMAL(19, 2),
    solde_apres DECIMAL(19, 2),
    date_operation TIMESTAMP NOT NULL,
    description TEXT,
    reference_type VARCHAR(50),
    reference_id UUID,
    operateur_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mouvements_caisse_caisse_id_date ON mouvements_caisse(caisse_id, date_operation);
CREATE INDEX idx_mouvements_caisse_type_sens ON mouvements_caisse(type, sens);
CREATE INDEX idx_mouvements_caisse_reference ON mouvements_caisse(reference_type, reference_id);
CREATE INDEX idx_mouvements_caisse_caisse_id_desc ON mouvements_caisse(caisse_id, date_operation DESC);

-- ========================================
-- TRANSFERTS
-- ========================================
CREATE TABLE transferts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    caisse_source_id UUID NOT NULL REFERENCES caisses(id),
    caisse_destination_id UUID NOT NULL REFERENCES caisses(id),
    montant DECIMAL(19, 2) NOT NULL,
    motif TEXT,
    statut VARCHAR(20) NOT NULL DEFAULT 'EFFECTUE',
    operateur_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transferts_caisse_source ON transferts(caisse_source_id);
CREATE INDEX idx_transferts_caisse_destination ON transferts(caisse_destination_id);
CREATE INDEX idx_transferts_statut ON transferts(statut);

-- ========================================
-- APPROVISIONNEMENTS (Cash Replenishments)
-- ========================================
CREATE TABLE approvisionnements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    caisse_id UUID NOT NULL REFERENCES caisses(id),
    montant DECIMAL(19, 2) NOT NULL,
    motif TEXT,
    mode_approvisionnement VARCHAR(30) NOT NULL,
    reference_externe VARCHAR(100),
    operateur_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_approvisionnements_caisse_id ON approvisionnements(caisse_id);
CREATE INDEX idx_approvisionnements_mode ON approvisionnements(mode_approvisionnement);

-- ========================================
-- SORTIES CAISSE (Cash Withdrawals/Exits)
-- ========================================
CREATE TABLE sorties_caisse (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    caisse_id UUID NOT NULL REFERENCES caisses(id),
    montant DECIMAL(19, 2) NOT NULL,
    motif TEXT NOT NULL,
    type_sortie VARCHAR(30) NOT NULL,
    beneficiaire VARCHAR(255),
    reference_externe VARCHAR(100),
    operateur_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sorties_caisse_caisse_id ON sorties_caisse(caisse_id);
CREATE INDEX idx_sorties_caisse_type ON sorties_caisse(type_sortie);

-- ========================================
-- SEED DATA - System Caisses
-- ========================================
INSERT INTO caisses (id, numero, nom, type, statut, solde_initial) VALUES
('a0000000-0000-0000-0000-000000000001', 'SYS-CAG-PUB', 'Caisse Publique Système', 'SYSTEME', 'ACTIVE', 0),
('a0000000-0000-0000-0000-000000000002', 'SYS-CAG-PRV', 'Caisse Privée Système', 'SYSTEME', 'ACTIVE', 0),
('a0000000-0000-0000-0000-000000000003', 'SYS-COT-PUB', 'Caisse Cotisations Publique', 'SYSTEME', 'ACTIVE', 0),
('a0000000-0000-0000-0000-000000000004', 'SYS-COT-PRV', 'Caisse Cotisations Privée', 'SYSTEME', 'ACTIVE', 0),
('a0000000-0000-0000-0000-000000000005', 'SYS-NC-CREDIT', 'Caisse Nano-Crédit', 'CREDIT', 'ACTIVE', 0),
('a0000000-0000-0000-0000-000000000006', 'SYS-NC-IMPAYE', 'Caisse Nano-Crédit Impayés', 'IMPAYES', 'ACTIVE', 0),
('a0000000-0000-0000-0000-000000000007', 'SYS-EPS-TONTINE', 'Caisse Épargne Tontine', 'TONTINE', 'ACTIVE', 0);
