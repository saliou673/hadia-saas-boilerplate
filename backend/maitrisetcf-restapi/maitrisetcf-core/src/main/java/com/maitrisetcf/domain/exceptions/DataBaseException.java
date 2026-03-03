package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a database operation fails at the infrastructure level.
 */
public class DataBaseException extends TechnicalException {
    public DataBaseException(String message) {
        super(message);
    }

    public DataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
