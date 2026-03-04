package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a payment gateway returns a failure result.
 */
public class PaymentProcessingException extends FunctionalException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}
