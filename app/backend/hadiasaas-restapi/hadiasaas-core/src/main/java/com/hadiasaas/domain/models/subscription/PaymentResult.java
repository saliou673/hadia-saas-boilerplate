package com.hadiasaas.domain.models.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Value object representing the result of a payment gateway transaction.
 */
@Getter
@RequiredArgsConstructor
public class PaymentResult {
    private final boolean success;
    /**
     * External transaction/payment identifier returned by the gateway. May be null on failure.
     */
    private final String externalPaymentId;
    /**
     * Error description when {@code success} is false.
     */
    private final String errorMessage;

    public static PaymentResult success(String externalPaymentId) {
        return new PaymentResult(true, externalPaymentId, null);
    }

    public static PaymentResult failure(String errorMessage) {
        return new PaymentResult(false, null, errorMessage);
    }
}
