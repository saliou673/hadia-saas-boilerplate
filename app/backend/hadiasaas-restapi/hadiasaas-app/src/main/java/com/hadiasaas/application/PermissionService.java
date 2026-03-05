package com.hadiasaas.application;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.domain.ports.in.PermissionUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.PermissionPersistencePort;
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

    @Override
    public PagedResult<Permission> findAll(int page, int size) {
        return permissionPersistencePort.findAll(page, size);
    }
}
