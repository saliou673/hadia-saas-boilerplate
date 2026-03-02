package com.maitrisetcf.domain.exceptions;

public class CredentialsExpiredException extends AuthFunctionalException {

    public CredentialsExpiredException(String message) {
        super(message);
    }
}
