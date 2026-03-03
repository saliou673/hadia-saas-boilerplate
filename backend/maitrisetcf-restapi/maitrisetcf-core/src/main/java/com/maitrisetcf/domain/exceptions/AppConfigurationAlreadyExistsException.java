package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when attempting to create a configuration entry that already exists.
 */
public class AppConfigurationAlreadyExistsException extends FunctionalException {
    public AppConfigurationAlreadyExistsException(String message) {
        super(message);
    }
}
