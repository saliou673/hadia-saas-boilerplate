package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.enumerations.StorageProvider;
import com.hadiasaas.domain.models.storagesettings.StorageSettings;

/**
 * Use case for managing storage settings entries.
 */
public interface StorageSettingsUseCase {

    /**
     * Creates a new storage settings entry.
     *
     * @param provider   the storage provider
     * @param bucketName optional bucket name
     * @param region     optional region
     * @param endpoint   optional custom endpoint
     * @param active     whether to activate this entry (only one can be active)
     * @return the created entry
     */
    StorageSettings create(StorageProvider provider, String bucketName, String region, String endpoint, boolean active);

    /**
     * Updates the storage settings entry with the given identifier.
     *
     * @param id         the entry identifier
     * @param provider   the storage provider
     * @param bucketName optional bucket name
     * @param region     optional region
     * @param endpoint   optional custom endpoint
     * @param active     whether to activate this entry (only one can be active)
     * @return the updated entry
     */
    StorageSettings update(Long id, StorageProvider provider, String bucketName, String region, String endpoint, boolean active);

    /**
     * Deletes the storage settings entry with the given identifier.
     *
     * @param id the entry identifier
     */
    void delete(Long id);

    /**
     * Returns the storage settings entry with the given identifier.
     *
     * @param id the entry identifier
     * @return the entry
     */
    StorageSettings getById(Long id);
}
