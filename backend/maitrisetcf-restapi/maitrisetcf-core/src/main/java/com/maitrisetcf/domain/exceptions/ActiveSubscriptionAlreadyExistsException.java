package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a user tries to subscribe to a plan they already have an active subscription for.
 */
public class ActiveSubscriptionAlreadyExistsException extends FunctionalException {
    public ActiveSubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
