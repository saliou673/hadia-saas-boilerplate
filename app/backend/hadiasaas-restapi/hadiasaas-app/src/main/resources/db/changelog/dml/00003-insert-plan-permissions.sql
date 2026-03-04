--liquibase formatted sql
--changeset saliou673:00003-insert-plan-permissions

INSERT INTO permission (code, description)
VALUES ('plan:read', 'View subscription plans (admin)'),
       ('plan:create', 'Create subscription plans'),
       ('plan:update', 'Update subscription plans'),
       ('plan:delete', 'Delete subscription plans');

-- Grant all plan permissions to Sysadmin
INSERT INTO role_group_permission (role_group_id, permission_code)
SELECT rg.id, p.code
FROM role_group rg,
     permission p
WHERE rg.name = 'Sysadmin'
  AND p.code IN ('plan:read', 'plan:create', 'plan:update', 'plan:delete');

-- Grant plan:read to Admin
INSERT INTO role_group_permission (role_group_id, permission_code)
SELECT rg.id, p.code
FROM role_group rg
         JOIN permission p ON p.code = 'plan:read'
WHERE rg.name = 'Admin';
