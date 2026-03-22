package com.hadiasaas.domain.models.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Result returned when a Stripe PaymentIntent is created server-side.
 * The {@code clientSecret} must be sent to the frontend so Stripe.js can
 * confirm card details.
 */
@Getter
@RequiredArgsConstructor
public class StripePaymentIntentResult {
    /** Stripe client_secret — passed to stripe.confirmCardPayment() on the frontend. */
    private final String clientSecret;
    /** Stripe PaymentIntent ID (pi_...) — sent back to the server after confirmation. */
    private final String paymentIntentId;
    /** Amount in the currency's smallest unit (e.g. cents for USD). */
    private final long amountInSmallestUnit;
    /** ISO 4217 currency code in lowercase (e.g. "usd"). */
    private final String currency;
}
