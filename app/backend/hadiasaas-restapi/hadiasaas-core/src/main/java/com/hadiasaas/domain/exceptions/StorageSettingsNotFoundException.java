package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a storage settings entry cannot be found.
 */
public class StorageSettingsNotFoundException extends FunctionalException {
    public StorageSettingsNotFoundException(String message) {
        super(message);
    }
}
