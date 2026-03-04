package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a login attempt is made against a disabled account.
 */
public class AccountDisabledException extends AuthFunctionalException {

    public AccountDisabledException(String message) {
        super(message);
    }
}
