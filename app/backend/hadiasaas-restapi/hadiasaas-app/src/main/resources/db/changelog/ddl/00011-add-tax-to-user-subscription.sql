--liquibase formatted sql
--changeset saliou673:00011-add-tax-to-user-subscription

ALTER TABLE user_subscription
    ADD COLUMN IF NOT EXISTS tax_rate NUMERIC(8, 4),
    ADD COLUMN IF NOT EXISTS tax_amount NUMERIC(19, 4);
