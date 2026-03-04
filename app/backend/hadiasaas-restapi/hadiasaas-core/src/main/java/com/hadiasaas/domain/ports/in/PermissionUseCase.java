package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.rbac.Permission;

import java.util.List;

/**
 * Use case for querying system permissions.
 */
public interface PermissionUseCase {

    /**
     * Returns all available permissions.
     *
     * @return list of all permissions
     */
    List<Permission> findAll();
}
