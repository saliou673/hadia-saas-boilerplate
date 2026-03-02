package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.RoleGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Transactional(readOnly = true)
public interface RoleGroupRepository extends JpaRepository<RoleGroupEntity, Long> {

    boolean existsByName(String name);

    Set<RoleGroupEntity> findByNameIn(Collection<String> names);
}
