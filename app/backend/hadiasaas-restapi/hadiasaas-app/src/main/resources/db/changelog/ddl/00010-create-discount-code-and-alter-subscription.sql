-- liquibase formatted sql
-- changeset saliou673:00010-discount-code

CREATE TABLE discount_code
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY,
    code             VARCHAR(50)    NOT NULL,
    discount_type    VARCHAR(30)    NOT NULL,
    discount_value   NUMERIC(19, 4) NOT NULL,
    currency_code    VARCHAR(10),
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    expiration_date  DATE,
    max_usages       INT,
    usage_count      INT            NOT NULL DEFAULT 0,
    creation_date    TIMESTAMP      NOT NULL,
    last_update_date TIMESTAMP      NOT NULL,
    last_updated_by  TEXT           NOT NULL,
    CONSTRAINT pk_discount_code PRIMARY KEY (id),
    CONSTRAINT un_discount_code_code UNIQUE (code)
);

ALTER TABLE user_subscription
    ADD COLUMN IF NOT EXISTS discount_code_used VARCHAR(50),
    ADD COLUMN IF NOT EXISTS discount_amount    NUMERIC(19, 4);
