package com.maitrisetcf.domain.exceptions;

/**
 * Generic technical exception for unexpected errors or technical failures.
 * <p>
 * Use this for infrastructure/configuration issues or external dependency failures,
 * not for business rule violations.
 */
public class TechnicalException extends RuntimeException {
    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
