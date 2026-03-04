package com.hadiasaas.domain.exceptions;

/**
 * Thrown when the user's credentials (e.g. password) have expired.
 */
public class CredentialsExpiredException extends AuthFunctionalException {

    public CredentialsExpiredException(String message) {
        super(message);
    }
}
