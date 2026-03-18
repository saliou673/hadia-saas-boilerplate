package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;

import java.math.BigDecimal;

/**
 * Use case for managing tax configuration entries.
 */
public interface TaxConfigurationUseCase {

    /**
     * Creates a new tax configuration entry.
     *
     * @param code        unique code
     * @param name        display name
     * @param rate        tax rate as a decimal fraction (e.g. 0.20 for 20%)
     * @param description optional description
     * @return the created entry
     */
    TaxConfiguration create(String code, String name, BigDecimal rate, String description);

    /**
     * Updates the tax configuration entry with the given identifier.
     *
     * @param id          the entry identifier
     * @param code        new code
     * @param name        new name
     * @param rate        new rate
     * @param description new description
     * @param active      new active flag
     * @return the updated entry
     */
    TaxConfiguration update(Long id, String code, String name, BigDecimal rate, String description, boolean active);

    /**
     * Deletes the tax configuration entry with the given identifier.
     *
     * @param id the entry identifier
     */
    void delete(Long id);

    /**
     * Returns the tax configuration entry with the given identifier.
     *
     * @param id the entry identifier
     * @return the entry
     */
    TaxConfiguration getById(Long id);
}
