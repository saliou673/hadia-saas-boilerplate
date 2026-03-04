package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a refresh token is not found or is otherwise invalid.
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
