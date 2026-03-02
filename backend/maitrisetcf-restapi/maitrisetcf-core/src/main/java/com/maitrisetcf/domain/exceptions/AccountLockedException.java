package com.maitrisetcf.domain.exceptions;

public class AccountLockedException extends AuthFunctionalException {

    public AccountLockedException(String message) {
        super(message);
    }
}
