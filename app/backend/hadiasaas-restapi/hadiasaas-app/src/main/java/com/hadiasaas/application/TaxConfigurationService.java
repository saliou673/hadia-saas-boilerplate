package com.hadiasaas.application;

import com.hadiasaas.domain.exceptions.TaxConfigurationAlreadyExistsException;
import com.hadiasaas.domain.exceptions.TaxConfigurationNotFoundException;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.domain.ports.in.TaxConfigurationUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.TaxConfigurationPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link TaxConfigurationUseCase}: CRUD for tax configurations. */
public class TaxConfigurationService implements TaxConfigurationUseCase {

    private final TaxConfigurationPersistencePort taxConfigurationPersistencePort;

    @Override
    public TaxConfiguration create(String code, String name, BigDecimal rate, String description) {
        log.debug("Creating tax configuration: code={}", code);
        if (taxConfigurationPersistencePort.existsByCode(code)) {
            throw new TaxConfigurationAlreadyExistsException("Tax configuration with code " + code + " already exists");
        }
        TaxConfiguration taxConfiguration = TaxConfiguration.create(code, name, rate, description);
        return taxConfigurationPersistencePort.save(taxConfiguration);
    }

    @Override
    public TaxConfiguration update(Long id, String code, String name, BigDecimal rate, String description, boolean active) {
        log.debug("Updating tax configuration id={}", id);
        TaxConfiguration taxConfiguration = taxConfigurationPersistencePort.findById(id)
                .orElseThrow(() -> new TaxConfigurationNotFoundException("Tax configuration not found with id: " + id));

        if (taxConfigurationPersistencePort.existsByCodeAndIdNot(code, id)) {
            throw new TaxConfigurationAlreadyExistsException("Tax configuration with code " + code + " already exists");
        }

        taxConfiguration.update(code, name, rate, description, active);
        return taxConfigurationPersistencePort.save(taxConfiguration);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting tax configuration id={}", id);
        TaxConfiguration taxConfiguration = taxConfigurationPersistencePort.findById(id)
                .orElseThrow(() -> new TaxConfigurationNotFoundException("Tax configuration not found with id: " + id));
        taxConfigurationPersistencePort.remove(taxConfiguration);
    }

    @Override
    public TaxConfiguration getById(Long id) {
        return taxConfigurationPersistencePort.findById(id)
                .orElseThrow(() -> new TaxConfigurationNotFoundException("Tax configuration not found with id: " + id));
    }
}
