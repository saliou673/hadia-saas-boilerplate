package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.appconfiguration.AppConfiguration;
import com.maitrisetcf.domain.models.appconfiguration.AppConfigurationFilter;
import com.maitrisetcf.domain.models.query.PagedResult;

/**
 * Read-only query use case for application configuration entries.
 */
public interface AppConfigurationQueryUseCase {

    /**
     * Returns a page of configuration entries matching the given filter.
     *
     * @param filter criteria to apply
     * @param page   zero-based page index
     * @param size   maximum items per page
     * @return a page of matching entries
     */
    PagedResult<AppConfiguration> findAll(AppConfigurationFilter filter, int page, int size);

    /**
     * Counts configuration entries matching the given filter.
     *
     * @param filter criteria to apply
     * @return number of matching entries
     */
    long count(AppConfigurationFilter filter);
}
