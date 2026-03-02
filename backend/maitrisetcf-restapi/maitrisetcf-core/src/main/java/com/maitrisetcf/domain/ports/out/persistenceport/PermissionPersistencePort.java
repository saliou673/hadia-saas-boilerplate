package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.rbac.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PermissionPersistencePort {

    List<Permission> findAll();

    Set<Permission> findByCodes(Collection<String> codes);
}
