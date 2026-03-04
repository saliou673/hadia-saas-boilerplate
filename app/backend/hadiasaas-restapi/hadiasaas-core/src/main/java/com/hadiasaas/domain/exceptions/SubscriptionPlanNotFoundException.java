package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a subscription plan cannot be found by the given identifier.
 */
public class SubscriptionPlanNotFoundException extends FunctionalException {
    public SubscriptionPlanNotFoundException(String message) {
        super(message);
    }
}
