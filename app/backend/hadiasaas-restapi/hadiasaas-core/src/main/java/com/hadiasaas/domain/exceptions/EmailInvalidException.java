package com.hadiasaas.domain.exceptions;

/**
 * Thrown when an email address fails format validation.
 */
public class EmailInvalidException extends FunctionalException {
    public EmailInvalidException(String message) {
        super(message);
    }
}
