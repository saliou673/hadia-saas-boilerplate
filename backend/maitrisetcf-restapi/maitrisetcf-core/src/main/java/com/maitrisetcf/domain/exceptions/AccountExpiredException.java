package com.maitrisetcf.domain.exceptions;

public class AccountExpiredException extends AuthFunctionalException {

    public AccountExpiredException(String message) {
        super(message);
    }
}
