package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.subscription.UserSubscription;
import com.hadiasaas.domain.models.subscription.UserSubscriptionFilter;

/**
 * Input port for read-only subscription queries.
 */
public interface UserSubscriptionQueryUseCase {

    /**
     * Returns a paginated list of subscriptions matching the given filter.
     */
    PagedResult<UserSubscription> findAll(UserSubscriptionFilter filter, int page, int size);

    /**
     * Returns a paginated list of subscriptions belonging to the current authenticated user.
     */
    PagedResult<UserSubscription> findMySubscriptions(int page, int size);

    /**
     * Counts subscriptions matching the given filter.
     */
    long count(UserSubscriptionFilter filter);
}
