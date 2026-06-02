-- V1__create_payment_tables.sql
-- Payment Gateway Service Database Schema - Payment Methods, Gateway Configurations, Transactions

-- ========================================
-- PAYMENT METHODS
-- ========================================
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    gateway VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    min_amount DECIMAL(19, 2),
    max_amount DECIMAL(19, 2),
    fees_percentage DECIMAL(5, 2),
    fees_fixed DECIMAL(19, 2),
    config JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_methods_code ON payment_methods(code);
CREATE INDEX idx_payment_methods_gateway ON payment_methods(gateway);
CREATE INDEX idx_payment_methods_is_active ON payment_methods(is_active);

-- ========================================
-- PAYDUNYA CONFIGURATIONS
-- ========================================
CREATE TABLE paydunya_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organisation_id UUID,
    master_key VARCHAR(255) NOT NULL,
    private_key VARCHAR(255) NOT NULL,
    public_key VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    mode VARCHAR(20) NOT NULL DEFAULT 'test',
    ipn_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_paydunya_config_org ON paydunya_configurations(organisation_id);
CREATE INDEX idx_paydunya_config_active ON paydunya_configurations(is_active);

-- ========================================
-- PISPI CONFIGURATIONS
-- ========================================
CREATE TABLE pispi_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organisation_id UUID,
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL,
    paye_alias VARCHAR(255) NOT NULL,
    mode VARCHAR(20) NOT NULL DEFAULT 'test',
    callback_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pispi_config_org ON pispi_configurations(organisation_id);
CREATE INDEX idx_pispi_config_active ON pispi_configurations(is_active);

-- ========================================
-- PAYMENT TRANSACTIONS
-- ========================================
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference VARCHAR(100) NOT NULL UNIQUE,
    external_reference VARCHAR(255),
    gateway VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    telephone VARCHAR(30),
    montant DECIMAL(19, 2) NOT NULL,
    fees DECIMAL(19, 2),
    net_amount DECIMAL(19, 2),
    currency VARCHAR(10) NOT NULL DEFAULT 'XOF',
    withdraw_mode VARCHAR(50),
    description TEXT,
    internal_reference VARCHAR(100),
    gateway_response JSONB,
    callback_data JSONB,
    error_message TEXT,
    organisation_id UUID,
    membre_id UUID,
    created_by UUID,
    confirmed_at TIMESTAMP,
    failed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_transactions_reference ON payment_transactions(reference);
CREATE INDEX idx_payment_transactions_external_ref ON payment_transactions(external_reference);
CREATE INDEX idx_payment_transactions_gateway ON payment_transactions(gateway);
CREATE INDEX idx_payment_transactions_type ON payment_transactions(transaction_type);
CREATE INDEX idx_payment_transactions_statut ON payment_transactions(statut);
CREATE INDEX idx_payment_transactions_internal_ref ON payment_transactions(internal_reference);
CREATE INDEX idx_payment_transactions_organisation ON payment_transactions(organisation_id);
CREATE INDEX idx_payment_transactions_membre ON payment_transactions(membre_id);
CREATE INDEX idx_payment_transactions_created_at ON payment_transactions(created_at);
CREATE INDEX idx_payment_transactions_statut_created ON payment_transactions(statut, created_at);
