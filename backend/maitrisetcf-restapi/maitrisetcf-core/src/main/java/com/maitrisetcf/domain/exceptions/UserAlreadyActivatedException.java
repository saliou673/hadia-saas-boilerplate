package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when an account activation is attempted on an account that is already active.
 */
public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
        super("User already activated");
    }
}
