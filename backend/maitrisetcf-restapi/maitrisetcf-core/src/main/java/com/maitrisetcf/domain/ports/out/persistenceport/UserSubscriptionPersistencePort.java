package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import com.maitrisetcf.domain.models.subscription.UserSubscription;

import java.util.List;
import java.util.Optional;

/**
 * Persistence port for user subscriptions.
 */
public interface UserSubscriptionPersistencePort {

    UserSubscription save(UserSubscription subscription);

    Optional<UserSubscription> findById(Long id);

    List<UserSubscription> findByUserId(Long userId);

    boolean existsByUserIdAndPlanIdAndStatus(Long userId, Long planId, UserSubscriptionStatus status);

    void remove(UserSubscription subscription);
}
