package com.maitrisetcf.application;

import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.ports.in.PermissionUseCase;
import com.maitrisetcf.domain.ports.out.persistenceport.PermissionPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
/** Application service implementing {@link PermissionUseCase}: read-only permission queries. */
public class PermissionService implements PermissionUseCase {

    private final PermissionPersistencePort permissionPersistencePort;

    @Override
    public List<Permission> findAll() {
        return permissionPersistencePort.findAll();
    }
}
