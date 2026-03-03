package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when the expiry date of a refresh token is missing or in the past.
 */
public class InvalidRefreshTokenExpiryDateException extends FunctionalException {

    public InvalidRefreshTokenExpiryDateException(String message) {
        super(message);
    }
}
