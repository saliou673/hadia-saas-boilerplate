package com.maitrisetcf.domain.exceptions;

public class AccountNotFoundException extends AuthFunctionalException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
