--liquibase formatted sql
--changeset saliou673:00004-insert-subscription-and-payment-config

-- Subscription management permissions
INSERT INTO permission (code, description)
VALUES ('subscription:read', 'View all user subscriptions (admin)'),
       ('subscription:manage', 'Cancel or manage user subscriptions (admin)');

-- Grant all subscription permissions to Sysadmin
INSERT INTO role_group_permission (role_group_id, permission_code)
SELECT rg.id, p.code
FROM role_group rg,
     permission p
WHERE rg.name = 'Sysadmin'
  AND p.code IN ('subscription:read', 'subscription:manage');

-- Grant subscription:read to Admin
INSERT INTO role_group_permission (role_group_id, permission_code)
SELECT rg.id, p.code
FROM role_group rg
         JOIN permission p ON p.code = 'subscription:read'
WHERE rg.name = 'Admin';

-- Seed supported payment modes
INSERT INTO app_configuration (category, code, label, description, active, creation_date, last_update_date,
                               last_updated_by)
VALUES ('PAYMENT_MODE', 'STRIPE', 'Stripe', 'Stripe online payment gateway', TRUE, NOW(), NOW(), 'system'),
       ('PAYMENT_MODE', 'PAYPAL', 'PayPal', 'PayPal online payment gateway', TRUE, NOW(), NOW(), 'system');

-- Seed supported currency
INSERT INTO app_configuration (category, code, label, description, active, creation_date, last_update_date,
                               last_updated_by)
VALUES ('CURRENCY', 'EUR', 'Euro', 'Euro currency (€)', TRUE, NOW(), NOW(), 'system');
