package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a login attempt is made against an account that has not been activated yet.
 */
public class AccountNotActivatedException extends AuthFunctionalException {

    public AccountNotActivatedException(String message) {
        super(message);
    }
}
