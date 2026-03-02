package com.maitrisetcf.domain.exceptions;

public class InvalidRefreshTokenUserException extends FunctionalException {

    public InvalidRefreshTokenUserException(String message) {
        super(message);
    }
}
