package com.hadiasaas.domain.exceptions;

/**
 * Thrown when the user associated with a refresh token does not match the expected user.
 */
public class InvalidRefreshTokenUserException extends FunctionalException {

    public InvalidRefreshTokenUserException(String message) {
        super(message);
    }
}
