package com.maitrisetcf.domain.exceptions;

public class AccountDisabledException extends AuthFunctionalException {

    public AccountDisabledException(String message) {
        super(message);
    }
}
