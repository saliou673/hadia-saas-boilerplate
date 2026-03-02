package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Transactional(readOnly = true)
public interface PermissionRepository extends JpaRepository<PermissionEntity, String> {

    Set<PermissionEntity> findByCodeIn(Collection<String> codes);
}
