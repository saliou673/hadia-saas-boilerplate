package com.maitrisetcf.domain.exceptions;

public class AuthenticationFailedException extends AuthFunctionalException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
