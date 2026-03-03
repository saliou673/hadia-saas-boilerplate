package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a mandatory field is missing or blank.
 */
public class RequiredFieldException extends FunctionalException {
    public RequiredFieldException(String message) {
        super(message);
    }
}
