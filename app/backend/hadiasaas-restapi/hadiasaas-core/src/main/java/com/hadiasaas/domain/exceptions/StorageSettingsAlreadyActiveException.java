package com.hadiasaas.domain.exceptions;

/**
 * Thrown when attempting to activate a storage settings entry while another is already active.
 */
public class StorageSettingsAlreadyActiveException extends FunctionalException {
    public StorageSettingsAlreadyActiveException(String message) {
        super(message);
    }
}
