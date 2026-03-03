package com.maitrisetcf.application;

import com.maitrisetcf.domain.exceptions.RoleGroupNameAlreadyExistsException;
import com.maitrisetcf.domain.exceptions.RoleGroupNotFoundException;
import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.models.rbac.RoleGroup;
import com.maitrisetcf.domain.ports.in.RoleGroupUseCase;
import com.maitrisetcf.domain.ports.out.persistenceport.PermissionPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.RoleGroupPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link RoleGroupUseCase}: CRUD and user assignment for role groups. */
public class RoleGroupService implements RoleGroupUseCase {

    private final RoleGroupPersistencePort roleGroupPersistencePort;
    private final PermissionPersistencePort permissionPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<RoleGroup> findAll() {
        return roleGroupPersistencePort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleGroup getById(Long id) {
        return roleGroupPersistencePort.findById(id)
                .orElseThrow(() -> new RoleGroupNotFoundException("Role group not found with id: " + id));
    }

    @Override
    public RoleGroup create(String name, String description, Set<String> permissionCodes) {
        log.debug("Creating role group: name={}", name);

        if (roleGroupPersistencePort.existsByName(name)) {
            throw new RoleGroupNameAlreadyExistsException("Role group with name '" + name + "' already exists");
        }

        Set<Permission> permissions = permissionPersistencePort.findByCodes(permissionCodes);
        RoleGroup roleGroup = RoleGroup.create(name, description, permissions);
        return roleGroupPersistencePort.save(roleGroup);
    }

    @Override
    public RoleGroup update(Long id, String name, String description, Set<String> permissionCodes) {
        log.debug("Updating role group id={}", id);

        RoleGroup roleGroup = getById(id);

        if (!roleGroup.getName().equals(name) && roleGroupPersistencePort.existsByName(name)) {
            throw new RoleGroupNameAlreadyExistsException("Role group with name '" + name + "' already exists");
        }

        Set<Permission> permissions = permissionPersistencePort.findByCodes(permissionCodes);
        roleGroup.update(name, description, permissions);
        return roleGroupPersistencePort.save(roleGroup);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting role group id={}", id);
        getById(id);
        roleGroupPersistencePort.deleteById(id);
    }

    @Override
    public void assignToUser(Long userId, Long roleGroupId) {
        log.debug("Assigning role group {} to user {}", roleGroupId, userId);
        getById(roleGroupId);
        roleGroupPersistencePort.assignToUser(userId, roleGroupId);
    }

    @Override
    public void revokeFromUser(Long userId, Long roleGroupId) {
        log.debug("Revoking role group {} from user {}", roleGroupId, userId);
        getById(roleGroupId);
        roleGroupPersistencePort.revokeFromUser(userId, roleGroupId);
    }
}
