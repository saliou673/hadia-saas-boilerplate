package com.hadiasaas.domain.ports.out.persistenceport;

import com.hadiasaas.domain.models.subscriptionplan.SubscriptionPlan;

import java.util.Optional;

/**
 * Persistence port for subscription plans.
 */
public interface SubscriptionPlanPersistencePort {

    /**
     * Persists or updates a subscription plan.
     *
     * @param plan the plan to save
     * @return the saved plan
     */
    SubscriptionPlan save(SubscriptionPlan plan);

    /**
     * Finds a subscription plan by its identifier.
     *
     * @param id the plan identifier
     * @return the matching plan, or empty if not found
     */
    Optional<SubscriptionPlan> findById(Long id);


    /**
     * Removes a subscription plan.
     *
     * @param plan the plan to remove
     */
    void remove(SubscriptionPlan plan);
}
