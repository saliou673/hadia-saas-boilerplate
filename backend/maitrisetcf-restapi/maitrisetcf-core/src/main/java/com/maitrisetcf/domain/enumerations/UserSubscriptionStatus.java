package com.maitrisetcf.domain.enumerations;

/**
 * Lifecycle status of a user subscription.
 */
public enum UserSubscriptionStatus {
    /**
     * Payment is pending or being processed.
     */
    PENDING,
    /**
     * Subscription is active and valid.
     */
    ACTIVE,
    /**
     * Subscription has passed its end date.
     */
    EXPIRED,
    /**
     * Subscription was cancelled by the user or an admin.
     */
    CANCELLED,
    /**
     * Payment processing failed.
     */
    FAILED
}
