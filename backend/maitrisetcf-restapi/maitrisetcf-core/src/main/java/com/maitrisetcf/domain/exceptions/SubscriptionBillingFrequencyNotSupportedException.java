package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when the chosen billing cycle has no corresponding price on the subscription plan.
 */
public class SubscriptionBillingFrequencyNotSupportedException extends FunctionalException {
    public SubscriptionBillingFrequencyNotSupportedException(String message) {
        super(message);
    }
}
