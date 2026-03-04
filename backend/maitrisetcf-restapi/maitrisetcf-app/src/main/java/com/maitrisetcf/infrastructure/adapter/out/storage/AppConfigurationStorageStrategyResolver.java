package com.maitrisetcf.infrastructure.adapter.out.storage;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.exceptions.TechnicalException;
import com.maitrisetcf.domain.ports.out.StorageStrategyResolverPort;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Resolves the storage strategy from persisted app configuration.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppConfigurationStorageStrategyResolver implements StorageStrategyResolverPort {

    private static final String DEFAULT_STRATEGY = StorageStrategy.LOCAL_CODE;

    private final AppConfigurationRepository appConfigurationRepository;

    @Override
    public String resolveStorageStrategyCode() {
        List<String> activeStrategies = appConfigurationRepository
                .findAllByCategoryAndActiveTrue(AppConfigurationCategory.STORAGE)
                .stream()
                .map(AppConfigurationEntity::getCode)
                .toList();

        if (activeStrategies.isEmpty()) {
            return DEFAULT_STRATEGY;
        }

        if (activeStrategies.size() > 1) {
            throw new TechnicalException("Multiple active storage strategies found");
        }

        return activeStrategies.getFirst();
    }
}
