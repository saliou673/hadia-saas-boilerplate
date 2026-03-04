package com.hadiasaas.domain.exceptions;

/**
 * Thrown when attempting to create a user with an email that is already registered.
 */
public class UserAlreadyExistsException extends FunctionalException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
