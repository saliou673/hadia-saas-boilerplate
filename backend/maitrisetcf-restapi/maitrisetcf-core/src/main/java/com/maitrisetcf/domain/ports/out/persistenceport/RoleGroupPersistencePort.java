package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.rbac.RoleGroup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleGroupPersistencePort {

    List<RoleGroup> findAll();

    Set<RoleGroup> findByNames(Collection<String> names);

    Optional<RoleGroup> findById(Long id);

    boolean existsByName(String name);

    RoleGroup save(RoleGroup roleGroup);

    void deleteById(Long id);

    void assignToUser(Long userId, Long roleGroupId);

    void revokeFromUser(Long userId, Long roleGroupId);
}
