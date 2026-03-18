package com.hadiasaas.domain.ports.out.persistenceport;

import com.hadiasaas.domain.models.storagesettings.StorageSettings;

import java.util.Optional;

/**
 * Persistence port for storage settings entries.
 */
public interface StorageSettingsPersistencePort {

    /**
     * Persists or updates a storage settings entry.
     *
     * @param storageSettings the entry to save
     * @return the saved entry
     */
    StorageSettings save(StorageSettings storageSettings);

    /**
     * Finds a storage settings entry by its identifier.
     *
     * @param id the entry identifier
     * @return the matching entry, or empty if not found
     */
    Optional<StorageSettings> findById(Long id);

    /**
     * Returns the currently active storage settings entry, if any.
     *
     * @return the active entry, or empty if none is active
     */
    Optional<StorageSettings> findActive();

    /**
     * Checks whether an active storage settings entry exists, optionally excluding one entry.
     *
     * @param excludeId the identifier to exclude, or {@code null} to check all entries
     * @return {@code true} if another active entry exists
     */
    boolean existsActiveExcluding(Long excludeId);

    /**
     * Removes a storage settings entry.
     *
     * @param storageSettings the entry to remove
     */
    void remove(StorageSettings storageSettings);
}
