package com.maitrisetcf.domain.exceptions;

/**
 * Base functional exception for authentication/authorization errors.
 * <p>
 * Use this to group auth-related functional errors and handle them consistently.
 */
public class AuthFunctionalException extends FunctionalException {
    public AuthFunctionalException(String message) {
        super(message);
    }
}
