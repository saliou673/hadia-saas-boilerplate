package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when the authentication process fails for a generic reason.
 */
public class AuthenticationFailedException extends AuthFunctionalException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
