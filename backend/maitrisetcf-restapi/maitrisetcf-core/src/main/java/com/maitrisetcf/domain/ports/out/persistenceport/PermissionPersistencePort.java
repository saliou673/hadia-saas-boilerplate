package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.rbac.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Persistence port for permissions.
 */
public interface PermissionPersistencePort {

    /**
     * Returns all available permissions.
     *
     * @return list of all permissions
     */
    List<Permission> findAll();

    /**
     * Returns the permissions matching the given codes.
     *
     * @param codes collection of permission codes
     * @return set of matching permissions
     */
    Set<Permission> findByCodes(Collection<String> codes);
}
