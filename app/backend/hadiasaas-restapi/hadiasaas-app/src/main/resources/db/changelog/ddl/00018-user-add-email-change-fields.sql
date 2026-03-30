--liquibase formatted sql
--changeset saliou673:00018-user-add-email-change-fields

ALTER TABLE IF EXISTS app_user
    ADD COLUMN pending_email TEXT,
    ADD COLUMN email_change_code TEXT,
    ADD COLUMN email_change_code_date TIMESTAMP WITH TIME ZONE;

ALTER TABLE app_user ADD CONSTRAINT un_app_user_email_change_code UNIQUE (email_change_code);
ALTER TABLE app_user ADD CONSTRAINT un_app_user_pending_email UNIQUE (pending_email);
