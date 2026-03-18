package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.domain.models.storagesettings.StorageSettingsFilter;

/**
 * Read-only query use case for storage settings entries.
 */
public interface StorageSettingsQueryUseCase {

    /**
     * Returns a page of storage settings entries matching the given filter.
     *
     * @param filter criteria to apply
     * @param page   zero-based page index
     * @param size   maximum items per page
     * @return a page of matching entries
     */
    PagedResult<StorageSettings> findAll(StorageSettingsFilter filter, int page, int size);

    /**
     * Counts storage settings entries matching the given filter.
     *
     * @param filter criteria to apply
     * @return number of matching entries
     */
    long count(StorageSettingsFilter filter);
}
