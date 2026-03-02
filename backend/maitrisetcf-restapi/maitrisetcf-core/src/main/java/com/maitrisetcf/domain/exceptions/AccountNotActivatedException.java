package com.maitrisetcf.domain.exceptions;

public class AccountNotActivatedException extends AuthFunctionalException {

    public AccountNotActivatedException(String message) {
        super(message);
    }
}
