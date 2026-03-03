package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a user attempts to subscribe to an inactive subscription plan.
 */
public class SubscriptionPlanNotActiveException extends FunctionalException {
    public SubscriptionPlanNotActiveException(String message) {
        super(message);
    }
}
