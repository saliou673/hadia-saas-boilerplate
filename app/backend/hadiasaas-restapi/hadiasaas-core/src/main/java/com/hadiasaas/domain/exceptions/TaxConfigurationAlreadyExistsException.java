package com.hadiasaas.domain.exceptions;

/**
 * Thrown when attempting to create a tax configuration entry that already exists.
 */
public class TaxConfigurationAlreadyExistsException extends FunctionalException {
    public TaxConfigurationAlreadyExistsException(String message) {
        super(message);
    }
}
