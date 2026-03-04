package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a user cannot be found by the given criteria.
 */
public class UserNotFoundException extends FunctionalException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
