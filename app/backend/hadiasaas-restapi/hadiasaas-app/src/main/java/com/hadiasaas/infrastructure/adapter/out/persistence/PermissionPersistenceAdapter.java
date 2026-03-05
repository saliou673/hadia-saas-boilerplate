package com.hadiasaas.infrastructure.adapter.out.persistence;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.domain.ports.out.persistenceport.PermissionPersistencePort;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.PermissionMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public PagedResult<Permission> findAll(int page, int size) {
        return AdapterPersistenceUtils.executeDbOperation(() -> {
            Page<PermissionEntity> entityPage = permissionRepository.findAll(
                    PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "code"))
            );
            List<Permission> items = permissionMapper.toDomain(entityPage.getContent());
            return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
        }, "Error fetching paginated permissions");
    }

    @Override
    public Set<Permission> findByCodes(Collection<String> codes) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> permissionMapper.toDomain(permissionRepository.findByCodeIn(codes)),
                "Error fetching permissions by codes"
        );
    }
}
