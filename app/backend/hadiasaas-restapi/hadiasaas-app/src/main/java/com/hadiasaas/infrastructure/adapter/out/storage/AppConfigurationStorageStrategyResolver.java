package com.hadiasaas.infrastructure.adapter.out.storage;

import com.hadiasaas.domain.ports.out.StorageStrategyResolverPort;
import com.hadiasaas.domain.ports.out.persistenceport.StorageSettingsPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves the storage strategy from the active {@link com.hadiasaas.domain.models.storagesettings.StorageSettings}.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppConfigurationStorageStrategyResolver implements StorageStrategyResolverPort {

    private static final String DEFAULT_STRATEGY = StorageStrategy.LOCAL_CODE;

    private final StorageSettingsPersistencePort storageSettingsPersistencePort;

    @Override
    public String resolveStorageStrategyCode() {
        return storageSettingsPersistencePort.findActive()
                .map(s -> s.getProvider().getStrategyCode())
                .orElse(DEFAULT_STRATEGY);
    }
}
