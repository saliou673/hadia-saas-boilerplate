package com.maitrisetcf.domain.exceptions;

/**
 * Base exception for functional (business) errors.
 * <p>
 * Use this type for domain or application errors that are not technical failures
 * (e.g., invalid user input, business rule violation).
 */
public class FunctionalException extends RuntimeException {
    public FunctionalException(String message) {
        super(message);
    }

    public FunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
