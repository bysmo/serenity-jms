-- ============================================================
-- Serenity-JMS Database Initialization Script
-- ============================================================
-- This script is for REFERENCE only.
-- In the current architecture, each microservice uses its own
-- dedicated PostgreSQL container with POSTGRES_DB set, so each
-- database is auto-created on container startup.
--
-- This script would be useful if switching to a shared
-- PostgreSQL instance approach.
-- ============================================================

-- Identity Service Database
SELECT 'CREATE DATABASE serenity_identity'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_identity')\gexec

-- Admin Service Database
SELECT 'CREATE DATABASE serenity_admin'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_admin')\gexec

-- Member Service Database
SELECT 'CREATE DATABASE serenity_member'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_member')\gexec

-- Account Service Database
SELECT 'CREATE DATABASE serenity_account'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_account')\gexec

-- Cotisation Service Database
SELECT 'CREATE DATABASE serenity_cotisation'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_cotisation')\gexec

-- Notification Service Database
SELECT 'CREATE DATABASE serenity_notification'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_notification')\gexec

-- Payment Service Database
SELECT 'CREATE DATABASE serenity_payment'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_payment')\gexec

-- Collector Service Database
SELECT 'CREATE DATABASE serenity_collector'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_collector')\gexec

-- NanoCredit Service Database
SELECT 'CREATE DATABASE serenity_nanocredit'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_nanocredit')\gexec

-- Epargne Service Database
SELECT 'CREATE DATABASE serenity_epargne'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_epargne')\gexec

-- Keycloak Database
SELECT 'CREATE DATABASE serenity_keycloak'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_keycloak')\gexec

-- ============================================================
-- Grant privileges to serenity user on all databases
-- ============================================================
-- Note: These GRANT statements need to be run per-database.
-- In the shared approach, you would connect to each database
-- and run: GRANT ALL PRIVILEGES ON DATABASE <dbname> TO serenity;
-- ============================================================

-- Alternative approach using DO block for standard psql:
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_identity') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_identity');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_admin') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_admin');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_member') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_member');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_account') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_account');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_cotisation') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_cotisation');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_notification') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_notification');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_payment') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_payment');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_collector') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_collector');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_nanocredit') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_nanocredit');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_epargne') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_epargne');
    END IF;
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'serenity_keycloak') THEN
        PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE serenity_keycloak');
    END IF;
END
$$;
