package com.hadiasaas.domain.exceptions;

/**
 * Thrown when the provided currency code does not match any active CURRENCY configuration entry.
 */
public class InvalidCurrencyException extends FunctionalException {
    public InvalidCurrencyException(String message) {
        super(message);
    }
}
