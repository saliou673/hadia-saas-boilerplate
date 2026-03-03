package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when the provided password-reset code is invalid or has expired.
 */
public class InvalidResetCodeException extends FunctionalException {

    public InvalidResetCodeException(String message) {
        super(message);
    }
}
