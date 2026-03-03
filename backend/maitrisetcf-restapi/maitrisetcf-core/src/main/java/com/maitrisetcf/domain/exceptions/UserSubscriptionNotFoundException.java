package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a requested user subscription cannot be found.
 */
public class UserSubscriptionNotFoundException extends FunctionalException {
    public UserSubscriptionNotFoundException(String message) {
        super(message);
    }
}
