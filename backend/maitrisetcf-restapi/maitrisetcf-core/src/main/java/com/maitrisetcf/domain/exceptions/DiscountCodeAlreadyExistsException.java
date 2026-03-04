package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when attempting to create or rename a discount code to a duplicate value.
 */
public class DiscountCodeAlreadyExistsException extends FunctionalException {
    public DiscountCodeAlreadyExistsException(String message) {
        super(message);
    }
}
