package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a login attempt is made against an expired account.
 */
public class AccountExpiredException extends AuthFunctionalException {

    public AccountExpiredException(String message) {
        super(message);
    }
}
