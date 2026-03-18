--liquibase formatted sql
--changeset saliou673:00010-cleanup-app-configuration-categories

-- Delete migrated rows from app_configuration
DELETE FROM app_configuration WHERE category IN ('STORAGE', 'TAX', 'ENTERPRISE');

-- Add check constraint to prevent STORAGE, TAX, ENTERPRISE from being re-inserted
ALTER TABLE app_configuration
    ADD CONSTRAINT chk_app_configuration_category
        CHECK (category IN ('CURRENCY', 'TWO_FACTOR', 'PAYMENT_MODE'));
