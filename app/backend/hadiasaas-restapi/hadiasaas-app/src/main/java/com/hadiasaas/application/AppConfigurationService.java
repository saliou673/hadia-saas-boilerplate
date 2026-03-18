package com.hadiasaas.application;

import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.domain.exceptions.AppConfigurationAlreadyExistsException;
import com.hadiasaas.domain.exceptions.AppConfigurationNotFoundException;
import com.hadiasaas.domain.models.appconfiguration.AppConfiguration;
import com.hadiasaas.domain.ports.in.AppConfigurationUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.AppConfigurationPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link AppConfigurationUseCase}: CRUD for configuration reference data. */
public class AppConfigurationService implements AppConfigurationUseCase {

    private final AppConfigurationPersistencePort appConfigurationPersistencePort;

    @Override
    public AppConfiguration create(AppConfigurationCategory category, String code, String label, String description) {
        log.debug("Creating reference data: category={}, code={}", category, code);
        if (appConfigurationPersistencePort.existsByCategoryAndCode(category, code)) {
            throw new AppConfigurationAlreadyExistsException(
                    "Reference data with category " + category + " and code " + code + " already exists"
            );
        }
        AppConfiguration appConfiguration = AppConfiguration.create(category, code, label, description);
        return appConfigurationPersistencePort.save(appConfiguration);
    }

    @Override
    public AppConfiguration update(Long id, String code, String label, String description, boolean active) {
        log.debug("Updating reference data id={}", id);
        AppConfiguration appConfiguration = appConfigurationPersistencePort.findById(id)
                .orElseThrow(() -> new AppConfigurationNotFoundException("Reference data not found with id: " + id));

        if (appConfigurationPersistencePort.existsByCategoryAndCodeAndIdNot(appConfiguration.getCategory(), code, id)) {
            throw new AppConfigurationAlreadyExistsException(
                    "Reference data with category " + appConfiguration.getCategory() + " and code " + code + " already exists"
            );
        }

        appConfiguration.update(code, label, description, active);
        return appConfigurationPersistencePort.save(appConfiguration);
    }

    @Override
    public AppConfiguration updateByCategoryAndCode(AppConfigurationCategory category, String code, String newCode, String label, String description, boolean active) {
        log.debug("Updating reference data: category={}, code={}", category, code);
        AppConfiguration appConfiguration = appConfigurationPersistencePort.findByCategoryAndCode(category, code)
                .orElseThrow(() -> new AppConfigurationNotFoundException(
                        "Reference data not found for category " + category + " and code: " + code
                ));

        if (!code.equals(newCode) && appConfigurationPersistencePort.existsByCategoryAndCodeAndIdNot(category, newCode, appConfiguration.getId())) {
            throw new AppConfigurationAlreadyExistsException(
                    "Reference data with category " + category + " and code " + newCode + " already exists"
            );
        }

        appConfiguration.update(newCode, label, description, active);
        return appConfigurationPersistencePort.save(appConfiguration);
    }

    @Override
    public Optional<AppConfiguration> getByCategoryAndCode(AppConfigurationCategory category, String code) {
        return appConfigurationPersistencePort.findByCategoryAndCode(category, code);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting reference data id={}", id);
        AppConfiguration appConfiguration = appConfigurationPersistencePort.findById(id)
                .orElseThrow(() -> new AppConfigurationNotFoundException("Reference data not found with id: " + id));
        appConfigurationPersistencePort.remove(appConfiguration);
    }

    @Override
    public AppConfiguration getById(Long id) {
        return appConfigurationPersistencePort.findById(id)
                .orElseThrow(() -> new AppConfigurationNotFoundException("Reference data not found with id: " + id));
    }
}
