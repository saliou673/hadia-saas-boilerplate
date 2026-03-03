package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when the provided email/password combination is incorrect.
 */
public class InvalidCredentialsException extends AuthFunctionalException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
