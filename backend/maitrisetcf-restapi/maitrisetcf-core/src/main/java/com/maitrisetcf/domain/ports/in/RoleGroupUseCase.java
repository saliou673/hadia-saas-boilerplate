package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.rbac.RoleGroup;

import java.util.List;
import java.util.Set;

public interface RoleGroupUseCase {

    List<RoleGroup> findAll();

    RoleGroup getById(Long id);

    RoleGroup create(String name, String description, Set<String> permissionCodes);

    RoleGroup update(Long id, String name, String description, Set<String> permissionCodes);

    void delete(Long id);

    void assignToUser(Long userId, Long roleGroupId);

    void revokeFromUser(Long userId, Long roleGroupId);
}
