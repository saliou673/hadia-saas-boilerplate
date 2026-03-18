package com.hadiasaas.application;

import com.hadiasaas.domain.enumerations.StorageProvider;
import com.hadiasaas.domain.exceptions.StorageSettingsAlreadyActiveException;
import com.hadiasaas.domain.exceptions.StorageSettingsNotFoundException;
import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.domain.ports.in.StorageSettingsUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.StorageSettingsPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementing {@link StorageSettingsUseCase}: CRUD for storage settings.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StorageSettingsService implements StorageSettingsUseCase {

    private final StorageSettingsPersistencePort storageSettingsPersistencePort;

    @Override
    public StorageSettings create(StorageProvider provider,
                                  String bucketName,
                                  String region,
                                  String endpoint,
                                  boolean active) {
        log.debug("Creating storage settings: provider={}", provider);
        if (active && storageSettingsPersistencePort.existsActiveExcluding(null)) {
            throw new StorageSettingsAlreadyActiveException("Only one active storage configuration is allowed");
        }
        StorageSettings storageSettings = StorageSettings.create(provider, bucketName, region, endpoint, active);
        return storageSettingsPersistencePort.save(storageSettings);
    }

    @Override
    public StorageSettings update(Long id,
                                  StorageProvider provider,
                                  String bucketName,
                                  String region,
                                  String endpoint,
                                  boolean active) {
        log.debug("Updating storage settings id={}", id);
        StorageSettings storageSettings = storageSettingsPersistencePort.findById(id)
                .orElseThrow(() -> new StorageSettingsNotFoundException("Storage settings not found with id: " + id));

        if (active && storageSettingsPersistencePort.existsActiveExcluding(id)) {
            throw new StorageSettingsAlreadyActiveException("Only one active storage configuration is allowed");
        }

        storageSettings.update(provider, bucketName, region, endpoint, active);
        return storageSettingsPersistencePort.save(storageSettings);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting storage settings id={}", id);
        StorageSettings storageSettings = storageSettingsPersistencePort.findById(id)
                .orElseThrow(() -> new StorageSettingsNotFoundException("Storage settings not found with id: " + id));
        storageSettingsPersistencePort.remove(storageSettings);
    }

    @Override
    public StorageSettings getById(Long id) {
        return storageSettingsPersistencePort.findById(id)
                .orElseThrow(() -> new StorageSettingsNotFoundException("Storage settings not found with id: " + id));
    }
}
