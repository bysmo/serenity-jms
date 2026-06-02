-- ============================================================
-- Serenity Notification Service - V1 Schema
-- ============================================================

-- SMTP Configurations table
CREATE TABLE smtp_configurations (
    id UUID PRIMARY KEY,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    auth_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    starttls_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ssl_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    from_email VARCHAR(255) NOT NULL,
    from_name VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- SMS Gateways table
CREATE TABLE sms_gateways (
    id UUID PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    provider_code VARCHAR(50) NOT NULL,
    api_url VARCHAR(500) NOT NULL,
    api_key VARCHAR(500) NOT NULL,
    sender_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ordre INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    timeout_seconds INTEGER NOT NULL DEFAULT 30,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Notification Logs table
CREATE TABLE notification_logs (
    id UUID PRIMARY KEY,
    type VARCHAR(30) NOT NULL,
    recipient_id UUID,
    recipient_type VARCHAR(20) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(500),
    content TEXT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    provider_reference VARCHAR(255),
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for notification_logs
CREATE INDEX idx_notification_logs_recipient_id ON notification_logs(recipient_id);
CREATE INDEX idx_notification_logs_status ON notification_logs(status);
CREATE INDEX idx_notification_logs_type ON notification_logs(type);
CREATE INDEX idx_notification_logs_created_at ON notification_logs(created_at);
CREATE INDEX idx_notification_logs_channel ON notification_logs(channel);

-- Email Templates table
CREATE TABLE email_templates (
    id UUID PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    sujet VARCHAR(500) NOT NULL,
    corps TEXT NOT NULL,
    type VARCHAR(50),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert default SMTP configuration
INSERT INTO smtp_configurations (id, host, port, username, password, auth_enabled, starttls_enabled, ssl_enabled, from_email, from_name, actif)
VALUES ('a0000000-0000-0000-0000-000000000001', 'localhost', 587, 'noreply@serenity.com', 'password', TRUE, TRUE, FALSE, 'noreply@serenity.com', 'Serenity', TRUE);

-- Insert default SMS gateways
INSERT INTO sms_gateways (id, nom, provider_code, api_url, api_key, sender_name, is_active, ordre, max_retries, timeout_seconds)
VALUES ('b0000000-0000-0000-0000-000000000001', 'Orange CI', 'orange', 'https://api.orange.com/sms/v1', '', 'SERENITY', TRUE, 1, 3, 30);

INSERT INTO sms_gateways (id, nom, provider_code, api_url, api_key, sender_name, is_active, ordre, max_retries, timeout_seconds)
VALUES ('b0000000-0000-0000-0000-000000000002', 'MTN CI', 'mtn', 'https://api.mtn.com/sms/v1', '', 'SERENITY', TRUE, 2, 3, 30);

-- Insert default email templates
INSERT INTO email_templates (id, nom, sujet, corps, type, actif) VALUES
('c0000000-0000-0000-0000-000000000001', 'welcome', 'Bienvenue sur Serenity - Votre numéro de membre', '<p>Bonjour {{prenom}},</p><p>Nous avons le plaisir de vous accueillir sur la plateforme Serenity.</p><p>Votre numéro de membre est : <strong>{{numeroMembre}}</strong></p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000002', 'kyc-validated', 'Vérification KYC validée', '<p>Bonjour,</p><p>Votre vérification d''identité (KYC) a été validée avec succès.</p><p>Vous avez désormais accès à l''ensemble des services de la plateforme.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000003', 'payment-completed', 'Accusé de réception de paiement', '<p>Bonjour,</p><p>Nous accusons réception de votre paiement de <strong>{{montant}}</strong> via <strong>{{modePaiement}}</strong>.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000004', 'payment-overdue', 'Rappel de paiement en retard', '<p>Bonjour,</p><p>Nous vous rappelons que votre paiement de <strong>{{montantDu}}</strong> est en retard de <strong>{{joursRetard}}</strong> jour(s).</p><p>Nous vous prions de bien vouloir régulariser votre situation dans les meilleurs délais.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000005', 'adhesion-requested', 'Demande d''adhésion reçue', '<p>Bonjour,</p><p>Votre demande d''adhésion a bien été enregistrée.</p><p>Elle sera traitée dans les plus brefs délais.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000006', 'low-balance-alert', 'Alerte : Solde bas de caisse', '<p>Bonjour,</p><p>La caisse <strong>{{caisseNumero}}</strong> a un solde de <strong>{{soldeActuel}}</strong>, inférieur au seuil d''alerte de <strong>{{seuilAlerte}}</strong>.</p><p>Veuillez prendre les mesures nécessaires.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'ADMIN', TRUE),
('c0000000-0000-0000-0000-000000000007', 'nano-credit-requested', 'Demande de nano-crédit reçue', '<p>Bonjour,</p><p>Votre demande de nano-crédit de <strong>{{montant}}</strong> a bien été enregistrée.</p><p>Elle est en cours de traitement.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000008', 'nano-credit-disbursed', 'Nano-crédit décaissé', '<p>Bonjour,</p><p>Votre nano-crédit de <strong>{{montant}}</strong> a été décaissé avec succès.</p><p>Le montant a été crédité sur votre compte.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000009', 'nano-credit-penalty', 'Pénalité de retard appliquée', '<p>Bonjour,</p><p>Une pénalité de <strong>{{montantPenalite}}</strong> a été appliquée suite à un retard de <strong>{{joursRetard}}</strong> jour(s) sur votre nano-crédit.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000010', 'epargne-subscribed', 'Souscription épargne confirmée', '<p>Bonjour,</p><p>Votre souscription au plan d''épargne a été confirmée.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE),
('c0000000-0000-0000-0000-000000000011', 'epargne-reminder', 'Rappel épargne - Échéance proche', '<p>Bonjour,</p><p>Nous vous rappelons que votre échéance d''épargne de <strong>{{montant}}</strong> est prévue pour le <strong>{{dateEcheance}}</strong>.</p><p>Veuillez prévoir le versement dans les délais.</p><p>Cordialement,<br>L''équipe Serenity</p>', 'MEMBER', TRUE);
