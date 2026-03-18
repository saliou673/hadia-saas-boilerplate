package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfigurationFilter;

/**
 * Read-only query use case for tax configuration entries.
 */
public interface TaxConfigurationQueryUseCase {

    /**
     * Returns a page of tax configuration entries matching the given filter.
     *
     * @param filter criteria to apply
     * @param page   zero-based page index
     * @param size   maximum items per page
     * @return a page of matching entries
     */
    PagedResult<TaxConfiguration> findAll(TaxConfigurationFilter filter, int page, int size);

    /**
     * Counts tax configuration entries matching the given filter.
     *
     * @param filter criteria to apply
     * @return number of matching entries
     */
    long count(TaxConfigurationFilter filter);
}
