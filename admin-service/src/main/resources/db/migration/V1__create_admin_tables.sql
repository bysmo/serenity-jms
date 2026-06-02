-- app_settings
CREATE TABLE app_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cle VARCHAR(255) NOT NULL UNIQUE,
    valeur TEXT,
    type VARCHAR(20) NOT NULL DEFAULT 'string',
    groupe VARCHAR(100),
    checksum VARCHAR(64),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- audit_logs
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_type VARCHAR(50),
    actor_id UUID,
    action VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    model_id UUID,
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT now()
);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_type, actor_id);
CREATE INDEX idx_audit_logs_model ON audit_logs(model, model_id);

-- system_merkle_ledger
CREATE TABLE system_merkle_ledger (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_name VARCHAR(100) NOT NULL,
    record_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL,
    record_checksum VARCHAR(64) NOT NULL,
    hash_chain VARCHAR(64) NOT NULL,
    hmac_signature VARCHAR(128),
    created_at TIMESTAMP DEFAULT now()
);
CREATE INDEX idx_merkle_table_record ON system_merkle_ledger(table_name, record_id);

-- auto_numbering_configs
CREATE TABLE auto_numbering_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_type VARCHAR(50) NOT NULL,
    definition JSONB NOT NULL,
    current_value BIGINT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    checksum VARCHAR(64),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- annonces
CREATE TABLE annonces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titre VARCHAR(255) NOT NULL,
    contenu TEXT,
    date_debut DATE,
    date_fin DATE,
    statut VARCHAR(20) DEFAULT 'active',
    type VARCHAR(50),
    ordre INTEGER DEFAULT 0,
    segment VARCHAR(100),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- email_templates
CREATE TABLE email_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(100) NOT NULL,
    sujet VARCHAR(255) NOT NULL,
    corps TEXT NOT NULL,
    type VARCHAR(50),
    actif BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- tags
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- Seed data for system settings
INSERT INTO app_settings (cle, valeur, type, groupe) VALUES
('app.name', 'Serenity-JMS', 'string', 'general'),
('app.version', '1.0.0', 'string', 'general'),
('default_currency', 'XOF', 'string', 'finance'),
('low_balance_threshold', '10000', 'integer', 'finance'),
('pin_max_attempts', '5', 'integer', 'security'),
('pin_lockout_minutes', '30', 'integer', 'security');

-- Seed auto-numbering configs
INSERT INTO auto_numbering_configs (object_type, definition, current_value, is_active) VALUES
('membre', '{"prefix": "MBR", "pad_length": 6}', 0, true),
('cotisation', '{"prefix": "COT", "pad_length": 6}', 0, true),
('paiement', '{"prefix": "PAY", "pad_length": 6}', 0, true),
('engagement', '{"prefix": "ENG", "pad_length": 6}', 0, true),
('caisse', '{"prefix": "CAI", "pad_length": 6}', 0, true),
('transfert', '{"prefix": "TRF", "pad_length": 6}', 0, true),
('nano_credit', '{"prefix": "NCR", "pad_length": 6}', 0, true),
('epargne', '{"prefix": "EPS", "pad_length": 6}', 0, true),
('collecte', '{"prefix": "COL", "pad_length": 6}', 0, true),
('remboursement', '{"prefix": "RMB", "pad_length": 6}', 0, true);
