CREATE TABLE epargne_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    montant_min NUMERIC(19,4),
    montant_max NUMERIC(19,4),
    frequence VARCHAR(20),
    taux_remuneration NUMERIC(5,4),
    duree_mois INTEGER,
    caisse_id UUID,
    heure_limite_paiement TIME,
    delai_rappel_heures INTEGER,
    intervalle_rappel_minutes INTEGER,
    actif BOOLEAN DEFAULT true,
    checksum VARCHAR(64),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE epargne_souscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    membre_id UUID NOT NULL,
    plan_id UUID NOT NULL REFERENCES epargne_plans(id),
    montant NUMERIC(19,4) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'active',
    date_souscription TIMESTAMP NOT NULL DEFAULT now(),
    date_fin DATE,
    caisse_id UUID,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE epargne_echeances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    souscription_id UUID NOT NULL REFERENCES epargne_souscriptions(id) ON DELETE CASCADE,
    numero_echeance INTEGER NOT NULL,
    montant NUMERIC(19,4) NOT NULL,
    date_echeance DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'en_attente',
    date_paiement TIMESTAMP,
    montant_paye NUMERIC(19,4) DEFAULT 0,
    montant_penalite NUMERIC(19,4) DEFAULT 0,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE epargne_versements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    souscription_id UUID NOT NULL REFERENCES epargne_souscriptions(id),
    echeance_id UUID REFERENCES epargne_echeances(id),
    montant NUMERIC(19,4) NOT NULL,
    date_versement TIMESTAMP NOT NULL DEFAULT now(),
    mode_paiement VARCHAR(20),
    reference VARCHAR(100),
    collecteur_id UUID,
    created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_epargne_souscriptions_membre ON epargne_souscriptions(membre_id);
CREATE INDEX idx_epargne_souscriptions_plan ON epargne_souscriptions(plan_id);
CREATE INDEX idx_epargne_echeances_souscription ON epargne_echeances(souscription_id);
CREATE INDEX idx_epargne_echeances_date ON epargne_echeances(date_echeance);
CREATE INDEX idx_epargne_versements_souscription ON epargne_versements(souscription_id);

-- Seed plans
INSERT INTO epargne_plans (nom, description, montant_min, montant_max, frequence, taux_remuneration, duree_mois, heure_limite_paiement, delai_rappel_heures, intervalle_rappel_minutes, actif) VALUES
('Epargne Mensuelle', 'Plan d épargne mensuel avec rémunération', 5000, 500000, 'mensuelle', 0.0350, 12, '23:59:00', 24, 60, true),
('Tontine Hebdomadaire', 'Tontine hebdomadaire pour petits épargnants', 1000, 100000, 'hebdomadaire', 0.0200, 6, '23:59:00', 24, 30, true),
('Epargne Liberté', 'Épargne sans contrainte de fréquence', 1000, 1000000, 'libre', 0.0150, NULL, NULL, NULL, NULL, true);
