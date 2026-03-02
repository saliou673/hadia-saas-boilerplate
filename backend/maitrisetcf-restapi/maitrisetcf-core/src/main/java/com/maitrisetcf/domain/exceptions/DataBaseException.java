package com.maitrisetcf.domain.exceptions;

public class DataBaseException extends TechnicalException {
    public DataBaseException(String message) {
        super(message);
    }

    public DataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
