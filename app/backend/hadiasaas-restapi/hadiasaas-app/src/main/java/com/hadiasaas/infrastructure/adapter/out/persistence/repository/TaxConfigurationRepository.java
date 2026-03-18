package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TaxConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TaxConfigurationEntity}.
 */
@Repository
public interface TaxConfigurationRepository extends JpaRepository<TaxConfigurationEntity, Long>, JpaSpecificationExecutor<TaxConfigurationEntity> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Optional<TaxConfigurationEntity> findFirstByActiveTrue();
}
