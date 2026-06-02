-- =============================================
-- Nano-Credit Service - Database Schema V1
-- =============================================

CREATE TABLE nano_credit_paliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero VARCHAR(50) UNIQUE,
    nom VARCHAR(100) NOT NULL,
    montant_plafond NUMERIC(19,4),
    duree_jours INTEGER,
    frequence_remboursement VARCHAR(20),
    taux_interet NUMERIC(5,2),
    penalite_par_jour NUMERIC(19,4),
    min_montant_total_rembourse NUMERIC(19,4),
    min_epargne_cumulee NUMERIC(19,4),
    min_epargne_percent NUMERIC(5,2),
    min_garant_qualite INTEGER,
    pourcentage_partage_garant NUMERIC(5,2),
    actif BOOLEAN DEFAULT true,
    checksum VARCHAR(64),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE nano_credits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id UUID NOT NULL,
    palier_id UUID REFERENCES nano_credit_paliers(id),
    montant NUMERIC(19,4) NOT NULL,
    statut VARCHAR(30) NOT NULL DEFAULT 'demande_en_attente',
    withdraw_mode VARCHAR(50),
    score_ai NUMERIC(5,2),
    score_humain NUMERIC(5,2),
    score_global NUMERIC(5,2),
    compte_remboursement_id UUID,
    compte_credit_id UUID,
    compte_impaye_id UUID,
    date_octroi TIMESTAMP,
    date_fin_remboursement DATE,
    montant_penalite NUMERIC(19,4) DEFAULT 0,
    jours_retard INTEGER DEFAULT 0,
    date_dernier_calcul_penalite TIMESTAMP,
    created_by UUID,
    checksum VARCHAR(64),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE nano_credit_echeances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nano_credit_id UUID NOT NULL REFERENCES nano_credits(id) ON DELETE CASCADE,
    numero_echeance INTEGER NOT NULL,
    montant NUMERIC(19,4) NOT NULL,
    montant_penalite NUMERIC(19,4) DEFAULT 0,
    date_echeance DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'en_attente',
    date_paiement TIMESTAMP,
    montant_paye NUMERIC(19,4) DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE nano_credit_versements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nano_credit_id UUID NOT NULL REFERENCES nano_credits(id),
    echeance_id UUID REFERENCES nano_credit_echeances(id),
    montant NUMERIC(19,4) NOT NULL,
    date_versement TIMESTAMP NOT NULL DEFAULT now(),
    mode_paiement VARCHAR(20),
    reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE nano_credit_garants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nano_credit_id UUID NOT NULL REFERENCES nano_credits(id),
    garant_membre_id UUID NOT NULL,
    qualite INTEGER DEFAULT 0,
    solde_garantie NUMERIC(19,4) DEFAULT 0,
    pourcentage_partage NUMERIC(5,2),
    statut VARCHAR(20) DEFAULT 'actif',
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- Indexes
CREATE INDEX idx_nano_credits_membre ON nano_credits(membre_id);
CREATE INDEX idx_nano_credits_statut ON nano_credits(statut);
CREATE INDEX idx_nano_echeances_credit ON nano_credit_echeances(nano_credit_id);
CREATE INDEX idx_nano_versements_credit ON nano_credit_versements(nano_credit_id);
CREATE INDEX idx_nano_garants_credit ON nano_credit_garants(nano_credit_id);
CREATE INDEX idx_nano_garants_membre ON nano_credit_garants(garant_membre_id);

-- Seed paliers
INSERT INTO nano_credit_paliers (numero, nom, montant_plafond, duree_jours, frequence_remboursement, taux_interet, penalite_par_jour, min_epargne_cumulee, min_epargne_percent, min_garant_qualite, pourcentage_partage_garant, actif) VALUES
('PAL-001', 'Palier Bronze', 50000, 30, 'quotidienne', 5.00, 500, 10000, 10.00, 1, 20.00, true),
('PAL-002', 'Palier Argent', 150000, 60, 'hebdomadaire', 4.50, 1000, 50000, 15.00, 2, 25.00, true),
('PAL-003', 'Palier Or', 300000, 90, 'hebdomadaire', 4.00, 1500, 100000, 20.00, 3, 30.00, true);
