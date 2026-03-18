package com.hadiasaas.domain.exceptions;

/**
 * Thrown when the enterprise profile has not been configured yet.
 */
public class EnterpriseProfileNotFoundException extends FunctionalException {
    public EnterpriseProfileNotFoundException(String message) {
        super(message);
    }
}
