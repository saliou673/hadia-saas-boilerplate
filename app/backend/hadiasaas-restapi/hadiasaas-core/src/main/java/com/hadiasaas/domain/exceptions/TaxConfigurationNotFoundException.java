package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a tax configuration entry cannot be found.
 */
public class TaxConfigurationNotFoundException extends FunctionalException {
    public TaxConfigurationNotFoundException(String message) {
        super(message);
    }
}
