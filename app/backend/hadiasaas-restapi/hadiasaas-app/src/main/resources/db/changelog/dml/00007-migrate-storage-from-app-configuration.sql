--liquibase formatted sql
--changeset saliou673:00007-migrate-storage-from-app-configuration

-- Migrate STORAGE rows from app_configuration to app_storage_settings.
-- The 'code' column stored the provider name (e.g. LOCAL, AWS).
-- Map known codes to StorageProvider enum values; unknown codes fall back to LOCAL.
INSERT INTO app_storage_settings (provider, active, creation_date, last_update_date, last_updated_by)
SELECT CASE
           WHEN UPPER(code) = 'LOCAL' THEN 'LOCAL'
           WHEN UPPER(code) IN ('AWS', 'AWS_S3') THEN 'AWS_S3'
           WHEN UPPER(code) IN ('AZURE', 'AZURE_BLOB') THEN 'AZURE_BLOB'
           WHEN UPPER(code) IN ('GCS', 'GOOGLE') THEN 'GCS'
           ELSE 'LOCAL'
           END,
       active,
       creation_date,
       last_update_date,
       last_updated_by
FROM app_configuration
WHERE category = 'STORAGE';
