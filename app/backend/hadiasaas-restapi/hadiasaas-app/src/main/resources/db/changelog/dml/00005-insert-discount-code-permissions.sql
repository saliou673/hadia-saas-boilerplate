--liquibase formatted sql
--changeset saliou673:00005-discount-code-permissions

INSERT INTO permission (code, description)
VALUES ('discount-code:read', 'Read discount codes'),
       ('discount-code:create', 'Create discount codes'),
       ('discount-code:update', 'Update discount codes'),
       ('discount-code:delete', 'Delete discount codes');

-- Grant all discount code permissions to Sysadmin
INSERT INTO role_group_permission (role_group_id, permission_code)
SELECT rg.id, p.code
FROM role_group rg,
     permission p
WHERE rg.name = 'Sysadmin'
  AND p.code IN ('discount-code:read', 'discount-code:create', 'discount-code:update', 'discount-code:delete');
