package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.models.appconfiguration.AppConfiguration;

import java.util.Optional;

public interface AppConfigurationUseCase {

    AppConfiguration create(AppConfigurationCategory category, String code, String label, String description);

    AppConfiguration update(Long id, String code, String label, String description, boolean active);

    AppConfiguration updateByCategoryAndCode(AppConfigurationCategory category, String code, String newCode, String label, String description, boolean active);

    void delete(Long id);

    AppConfiguration getById(Long id);

    Optional<AppConfiguration> getByCategoryAndCode(AppConfigurationCategory category, String code);
}
