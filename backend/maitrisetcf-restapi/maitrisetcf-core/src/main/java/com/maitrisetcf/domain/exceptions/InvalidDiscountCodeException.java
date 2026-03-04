package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a discount code is unusable for a subscription.
 */
public class InvalidDiscountCodeException extends FunctionalException {
    public InvalidDiscountCodeException(String message) {
        super(message);
    }
}
