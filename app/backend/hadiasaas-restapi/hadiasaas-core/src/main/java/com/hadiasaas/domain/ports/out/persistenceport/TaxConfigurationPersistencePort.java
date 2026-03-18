package com.hadiasaas.domain.ports.out.persistenceport;

import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;

import java.util.Optional;

/**
 * Persistence port for tax configuration entries.
 */
public interface TaxConfigurationPersistencePort {

    /**
     * Persists or updates a tax configuration entry.
     *
     * @param taxConfiguration the entry to save
     * @return the saved entry
     */
    TaxConfiguration save(TaxConfiguration taxConfiguration);

    /**
     * Finds a tax configuration entry by its identifier.
     *
     * @param id the entry identifier
     * @return the matching entry, or empty if not found
     */
    Optional<TaxConfiguration> findById(Long id);

    /**
     * Checks whether an entry with the given code exists.
     *
     * @param code the tax code
     * @return {@code true} if such an entry exists
     */
    boolean existsByCode(String code);

    /**
     * Checks whether an entry with the given code exists, excluding one specific entry.
     *
     * @param code      the tax code
     * @param excludeId the identifier to exclude from the check
     * @return {@code true} if a conflicting entry exists
     */
    boolean existsByCodeAndIdNot(String code, Long excludeId);

    /**
     * Returns the first active tax configuration entry, if any.
     *
     * @return the first active tax configuration, or empty if none is active
     */
    Optional<TaxConfiguration> findFirstActive();

    /**
     * Removes a tax configuration entry.
     *
     * @param taxConfiguration the entry to remove
     */
    void remove(TaxConfiguration taxConfiguration);
}
