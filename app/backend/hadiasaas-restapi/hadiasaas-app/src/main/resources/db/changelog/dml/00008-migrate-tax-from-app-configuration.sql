--liquibase formatted sql
--changeset saliou673:00008-migrate-tax-from-app-configuration

-- Migrate TAX rows from app_configuration to app_tax_configuration.
-- The old schema had no numeric rate; default to 0 so the row is preserved.
-- Operators should update the rate via the admin UI after migration.
INSERT INTO app_tax_configuration (code, name, rate, description, active, creation_date, last_update_date, last_updated_by)
SELECT code,
       label,
       0.00,
       description,
       active,
       creation_date,
       last_update_date,
       last_updated_by
FROM app_configuration
WHERE category = 'TAX';
