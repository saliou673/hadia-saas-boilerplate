package com.hadiasaas.infrastructure.adapter.out.persistence;

import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.domain.ports.out.persistenceport.StorageSettingsPersistencePort;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.StorageSettingsMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.StorageSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter implementing {@link StorageSettingsPersistencePort}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class StorageSettingsPersistenceAdapter implements StorageSettingsPersistencePort {

    private final StorageSettingsRepository storageSettingsRepository;
    private final StorageSettingsMapper storageSettingsMapper;

    @Override
    public StorageSettings save(StorageSettings storageSettings) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> storageSettingsMapper.toDomain(storageSettingsRepository.save(storageSettingsMapper.toEntity(storageSettings))),
                "Error saving storage settings"
        );
    }

    @Override
    public Optional<StorageSettings> findById(Long id) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> storageSettingsRepository.findById(id).map(storageSettingsMapper::toDomain),
                "Error fetching storage settings by id: " + id
        );
    }

    @Override
    public Optional<StorageSettings> findActive() {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> storageSettingsRepository.findByActiveTrue().map(storageSettingsMapper::toDomain),
                "Error fetching active storage settings"
        );
    }

    @Override
    public boolean existsActiveExcluding(Long excludeId) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> excludeId == null
                        ? storageSettingsRepository.existsByActiveTrue()
                        : storageSettingsRepository.existsByActiveTrueAndIdNot(excludeId),
                "Error checking active storage settings existence"
        );
    }

    @Override
    public void remove(StorageSettings storageSettings) {
        AdapterPersistenceUtils.executeDbOperation(
                () -> storageSettingsRepository.delete(storageSettingsMapper.toEntity(storageSettings)),
                "Error removing storage settings with id: " + storageSettings.getId()
        );
    }
}
