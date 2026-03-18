--liquibase formatted sql
--changeset saliou673:00009-migrate-enterprise-from-app-configuration

-- Migrate ENTERPRISE rows from app_configuration to app_enterprise_profile.
-- The old schema stored enterprise data as generic key/value pairs (code = field name, label = value).
-- We reconstruct a single profile row using MAX() aggregation to pick one value per field.
-- If no ENTERPRISE rows exist this insert is a no-op.
INSERT INTO app_enterprise_profile (company_name, creation_date, last_update_date, last_updated_by)
SELECT COALESCE(MAX(CASE WHEN UPPER(code) = 'COMPANY_NAME' THEN label END), 'Unknown'),
       MIN(creation_date),
       MAX(last_update_date),
       MAX(last_updated_by)
FROM app_configuration
WHERE category = 'ENTERPRISE'
HAVING COUNT(*) > 0;
