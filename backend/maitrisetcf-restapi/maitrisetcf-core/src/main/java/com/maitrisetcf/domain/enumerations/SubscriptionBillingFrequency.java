package com.maitrisetcf.domain.enumerations;

/**
 * Billing frequency for a subscription plan.
 */
public enum SubscriptionBillingFrequency {

    /**
     * Billed every month (30 days).
     */
    MONTHLY,

    /**
     * Billed every year (365 days).
     */
    YEARLY,

    /**
     * One-time purchase with lifetime access.
     */
    LIFETIME,

    /**
     * Custom duration and price defined on the plan itself.
     */
    CUSTOM
}
