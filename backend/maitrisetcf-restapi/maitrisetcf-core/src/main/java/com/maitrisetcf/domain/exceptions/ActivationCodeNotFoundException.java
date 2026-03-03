package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when no account matches the given activation code.
 */
public class ActivationCodeNotFoundException extends FunctionalException {

    public ActivationCodeNotFoundException(String message) {
        super(message);
    }
}
