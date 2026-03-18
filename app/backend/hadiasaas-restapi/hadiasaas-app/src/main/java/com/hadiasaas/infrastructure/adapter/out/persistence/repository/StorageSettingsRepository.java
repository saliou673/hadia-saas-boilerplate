package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.infrastructure.adapter.out.persistence.entity.StorageSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link StorageSettingsEntity}.
 */
@Repository
public interface StorageSettingsRepository extends JpaRepository<StorageSettingsEntity, Long>, JpaSpecificationExecutor<StorageSettingsEntity> {

    Optional<StorageSettingsEntity> findByActiveTrue();

    boolean existsByActiveTrue();

    boolean existsByActiveTrueAndIdNot(Long id);
}
