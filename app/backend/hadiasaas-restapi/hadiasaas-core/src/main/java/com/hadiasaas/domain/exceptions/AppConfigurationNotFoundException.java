package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a configuration entry cannot be found.
 */
public class AppConfigurationNotFoundException extends FunctionalException {
    public AppConfigurationNotFoundException(String message) {
        super(message);
    }
}
