package com.hadiasaas.infrastructure.adapter.out.persistence;

import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.domain.ports.out.persistenceport.PermissionPersistencePort;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.PermissionMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * JPA adapter implementing {@link PermissionPersistencePort}.
 */
@Service
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionPersistencePort {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAll() {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> permissionMapper.toDomain(permissionRepository.findAll()),
                "Error fetching all permissions"
        );
    }

    @Override
    public Set<Permission> findByCodes(Collection<String> codes) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> permissionMapper.toDomain(permissionRepository.findByCodeIn(codes)),
                "Error fetching permissions by codes"
        );
    }
}
