package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a username (first or last name) fails validation.
 */
public class InvalidUserNameException extends RuntimeException {
    public InvalidUserNameException(String message) {
        super(message);
    }
}
