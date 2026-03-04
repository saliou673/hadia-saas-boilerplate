package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a 2FA challenge is not found, has expired, or the provided code is wrong.
 */
public class InvalidTwoFactorChallengeException extends FunctionalException {
    public InvalidTwoFactorChallengeException(String message) {
        super(message);
    }
}
