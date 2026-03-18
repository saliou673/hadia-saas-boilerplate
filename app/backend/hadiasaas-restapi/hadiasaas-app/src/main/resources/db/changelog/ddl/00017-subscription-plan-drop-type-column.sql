--liquibase formatted sql
--changeset saliou673:00017-subscription-plan-drop-type-column

ALTER TABLE subscription_plan DROP COLUMN IF EXISTS type;
