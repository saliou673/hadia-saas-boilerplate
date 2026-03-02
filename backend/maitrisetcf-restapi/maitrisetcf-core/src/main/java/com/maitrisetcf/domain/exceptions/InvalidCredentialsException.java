package com.maitrisetcf.domain.exceptions;

public class InvalidCredentialsException extends AuthFunctionalException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
