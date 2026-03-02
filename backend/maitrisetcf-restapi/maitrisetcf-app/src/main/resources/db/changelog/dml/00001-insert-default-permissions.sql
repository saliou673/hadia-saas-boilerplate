--liquibase formatted sql
--changeset saliou673:00001-insert-default-permissions

INSERT INTO permission (code, description)
VALUES
    -- User management
    ('user:read', 'View user details'),
    ('user:create', 'Create new users'),
    ('user:update', 'Update user information'),
    ('user:deactivate', 'Deactivate user accounts'),
    ('user:invite', 'Invite users by email'),

    -- Configuration management
    ('config:read', 'View application configurations'),
    ('config:manage', 'Create, update and delete application configurations'),


    -- Role group management
    ('role-group:read', 'View role groups and their permissions'),
    ('role-group:manage', 'Create, update and delete role groups');
