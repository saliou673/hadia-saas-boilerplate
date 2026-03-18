package com.hadiasaas.infrastructure.adapter.out.persistence;

import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.domain.ports.out.persistenceport.TaxConfigurationPersistencePort;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.TaxConfigurationMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.TaxConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter implementing {@link TaxConfigurationPersistencePort}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TaxConfigurationPersistenceAdapter implements TaxConfigurationPersistencePort {

    private final TaxConfigurationRepository taxConfigurationRepository;
    private final TaxConfigurationMapper taxConfigurationMapper;

    @Override
    public TaxConfiguration save(TaxConfiguration taxConfiguration) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> taxConfigurationMapper.toDomain(taxConfigurationRepository.save(taxConfigurationMapper.toEntity(taxConfiguration))),
                "Error saving tax configuration with code: " + taxConfiguration.getCode()
        );
    }

    @Override
    public Optional<TaxConfiguration> findById(Long id) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> taxConfigurationRepository.findById(id).map(taxConfigurationMapper::toDomain),
                "Error fetching tax configuration by id: " + id
        );
    }

    @Override
    public boolean existsByCode(String code) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> taxConfigurationRepository.existsByCode(code),
                "Error checking tax configuration existence for code: " + code
        );
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long excludeId) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> taxConfigurationRepository.existsByCodeAndIdNot(code, excludeId),
                "Error checking tax configuration existence for code: " + code
        );
    }

    @Override
    public Optional<TaxConfiguration> findFirstActive() {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> taxConfigurationRepository.findFirstByActiveTrue().map(taxConfigurationMapper::toDomain),
                "Error fetching first active tax configuration"
        );
    }

    @Override
    public void remove(TaxConfiguration taxConfiguration) {
        AdapterPersistenceUtils.executeDbOperation(
                () -> taxConfigurationRepository.delete(taxConfigurationMapper.toEntity(taxConfiguration)),
                "Error removing tax configuration with id: " + taxConfiguration.getId()
        );
    }
}
