package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlanFilter;

import java.util.List;

/**
 * Read-only query use case for subscription plans.
 */
public interface SubscriptionPlanQueryUseCase {

    /**
     * Returns a page of subscription plans matching the given filter.
     *
     * @param filter criteria to apply
     * @param page   zero-based page index
     * @param size   maximum items per page
     * @return a page of matching plans
     */
    PagedResult<SubscriptionPlan> findAll(SubscriptionPlanFilter filter, int page, int size);

    /**
     * Returns all active plans sorted by price ascending (used for the public listing).
     *
     * @return list of active plans
     */
    List<SubscriptionPlan> findAllActive();

    /**
     * Counts subscription plans matching the given filter.
     *
     * @param filter criteria to apply
     * @return number of matching plans
     */
    long count(SubscriptionPlanFilter filter);
}
