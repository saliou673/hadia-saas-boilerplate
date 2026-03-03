package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.domain.models.subscription.UserSubscriptionFilter;

import java.util.List;

/**
 * Input port for read-only subscription queries.
 */
public interface UserSubscriptionQueryUseCase {

    /**
     * Returns a paginated list of subscriptions matching the given filter.
     */
    PagedResult<UserSubscription> findAll(UserSubscriptionFilter filter, int page, int size);

    /**
     * Returns all subscriptions belonging to the current authenticated user.
     */
    List<UserSubscription> findMySubscriptions();

    /**
     * Counts subscriptions matching the given filter.
     */
    long count(UserSubscriptionFilter filter);
}
