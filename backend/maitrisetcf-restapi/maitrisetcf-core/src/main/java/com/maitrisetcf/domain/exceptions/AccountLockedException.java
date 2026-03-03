package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a login attempt is made against a locked account.
 */
public class AccountLockedException extends AuthFunctionalException {

    public AccountLockedException(String message) {
        super(message);
    }
}
