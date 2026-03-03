package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when the current password provided during a change-password flow is incorrect.
 */
public class InvalidCurrentPasswordException extends FunctionalException {

    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}
