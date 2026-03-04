package com.hadiasaas.domain.exceptions;

/**
 * Thrown when no account is found for the given credentials or identifier.
 */
public class AccountNotFoundException extends AuthFunctionalException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
