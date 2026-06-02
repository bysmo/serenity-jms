-- ============================================================
-- Serenity-JMS Collector Service - Database Schema
-- ============================================================

-- Collecte sessions table
CREATE TABLE collecte_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    date_session DATE NOT NULL DEFAULT CURRENT_DATE,
    statut VARCHAR(20) NOT NULL DEFAULT 'OUVERT',
    montant_ouverture DECIMAL(19, 2) NOT NULL DEFAULT 0,
    montant_fermeture DECIMAL(19, 2),
    opened_at TIMESTAMP NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Collectes (individual collections) table
CREATE TABLE collectes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    collecte_session_id UUID NOT NULL REFERENCES collecte_sessions(id),
    membre_id UUID NOT NULL,
    type_collecte VARCHAR(30) NOT NULL,
    montant DECIMAL(19, 2) NOT NULL,
    echeance_type VARCHAR(50),
    echeance_id UUID,
    otp_code VARCHAR(6),
    is_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_at TIMESTAMP,
    reference_transaction VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_collecte_sessions_user_id ON collecte_sessions(user_id);
CREATE INDEX idx_collecte_sessions_statut ON collecte_sessions(statut);
CREATE INDEX idx_collecte_sessions_user_statut ON collecte_sessions(user_id, statut);
CREATE INDEX idx_collectes_session_id ON collectes(collecte_session_id);
CREATE INDEX idx_collectes_membre_id ON collectes(membre_id);
CREATE INDEX idx_collectes_is_confirmed ON collectes(is_confirmed);
CREATE INDEX idx_collectes_reference_transaction ON collectes(reference_transaction);
