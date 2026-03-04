package com.hadiasaas.domain.exceptions;

/**
 * Thrown when the token value stored within a refresh token record is invalid.
 */
public class InvalidRefreshTokenTokenException extends FunctionalException {

    public InvalidRefreshTokenTokenException(String message) {
        super(message);
    }
}
