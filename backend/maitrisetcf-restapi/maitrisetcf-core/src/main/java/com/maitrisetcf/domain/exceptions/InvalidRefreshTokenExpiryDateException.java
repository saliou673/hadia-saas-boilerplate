package com.maitrisetcf.domain.exceptions;

public class InvalidRefreshTokenExpiryDateException extends FunctionalException {

    public InvalidRefreshTokenExpiryDateException(String message) {
        super(message);
    }
}
