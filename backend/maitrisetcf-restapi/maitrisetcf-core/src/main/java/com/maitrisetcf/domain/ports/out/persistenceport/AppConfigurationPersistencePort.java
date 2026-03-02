package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.models.appconfiguration.AppConfiguration;

import java.util.Optional;

public interface AppConfigurationPersistencePort {

    AppConfiguration save(AppConfiguration appConfiguration);

    Optional<AppConfiguration> findById(Long id);

    boolean existsByCategoryAndCode(AppConfigurationCategory category, String code);

    boolean existsByCategoryAndCodeAndIdNot(AppConfigurationCategory category, String code, Long excludeId);

    boolean existsActiveByCategoryAndCode(AppConfigurationCategory category, String code);

    Optional<AppConfiguration> findByCategoryAndCode(AppConfigurationCategory category, String code);

    void remove(AppConfiguration appConfiguration);
}
