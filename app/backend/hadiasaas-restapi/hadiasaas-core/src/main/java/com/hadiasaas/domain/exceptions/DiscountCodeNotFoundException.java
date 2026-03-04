package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a discount code cannot be found.
 */
public class DiscountCodeNotFoundException extends FunctionalException {
    public DiscountCodeNotFoundException(String message) {
        super(message);
    }
}
