package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link AppConfigurationEntity}.
 */
@Repository
public interface AppConfigurationRepository extends JpaRepository<AppConfigurationEntity, Long>, JpaSpecificationExecutor<AppConfigurationEntity> {

    Optional<AppConfigurationEntity> findByCategoryAndCode(AppConfigurationCategory category, String code);

    boolean existsByCategoryAndCode(AppConfigurationCategory category, String code);

    boolean existsByCategoryAndCodeAndIdNot(AppConfigurationCategory category, String code, Long id);

    boolean existsByCategoryAndCodeAndActiveTrue(AppConfigurationCategory category, String code);

    boolean existsByCategoryAndActiveTrue(AppConfigurationCategory category);

    boolean existsByCategoryAndActiveTrueAndIdNot(AppConfigurationCategory category, Long id);

    List<AppConfigurationEntity> findAllByCategoryAndActiveTrue(AppConfigurationCategory category);
}
