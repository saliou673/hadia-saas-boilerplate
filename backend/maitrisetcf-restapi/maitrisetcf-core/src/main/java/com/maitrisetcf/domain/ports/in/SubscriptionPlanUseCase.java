package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;

import java.math.BigDecimal;
import java.util.List;

/**
 * Use case for managing subscription plans.
 */
public interface SubscriptionPlanUseCase {

    /**
     * Creates a new subscription plan.
     *
     * @param title         display title
     * @param description   optional longer description
     * @param monthlyPrice  price for a monthly billing cycle ({@code null} if not offered)
     * @param yearlyPrice   price for a yearly billing cycle ({@code null} if not offered)
     * @param lifetimePrice price for lifetime access ({@code null} if not offered)
     * @param price         price for a custom billing cycle ({@code null} if not offered); requires {@code durationDays}
     * @param durationDays  duration in days for the custom cycle ({@code null} if no custom cycle)
     * @param currencyCode  ISO currency code (must be an active CURRENCY entry)
     * @param features      ordered list of feature bullet points
     * @param active        whether the plan is immediately available
     * @param type          training delivery mode
     * @return the created plan
     */
    SubscriptionPlan create(String title, String description, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, List<String> features, boolean active, SubscriptionPlanType type);

    /**
     * Updates the subscription plan with the given identifier.
     *
     * @param id            the plan identifier
     * @param title         new title
     * @param description   new description
     * @param monthlyPrice  new monthly price ({@code null} if not offered)
     * @param yearlyPrice   new yearly price ({@code null} if not offered)
     * @param lifetimePrice new lifetime price ({@code null} if not offered)
     * @param price         new custom price ({@code null} if not offered); requires {@code durationDays}
     * @param durationDays  new duration in days for the custom cycle ({@code null} if no custom cycle)
     * @param currencyCode  new currency code
     * @param features      new feature list
     * @param active        new active flag
     * @param type          new delivery mode
     * @return the updated plan
     */
    SubscriptionPlan update(Long id, String title, String description, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, List<String> features, boolean active, SubscriptionPlanType type);

    /**
     * Deletes the subscription plan with the given identifier.
     *
     * @param id the plan identifier
     */
    void delete(Long id);

    /**
     * Returns the subscription plan with the given identifier.
     *
     * @param id the plan identifier
     * @return the plan
     */
    SubscriptionPlan getById(Long id);
}
