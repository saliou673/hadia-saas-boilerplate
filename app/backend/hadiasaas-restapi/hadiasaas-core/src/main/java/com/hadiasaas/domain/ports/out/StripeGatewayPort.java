package com.hadiasaas.domain.ports.out;

import com.hadiasaas.domain.models.subscription.StripePaymentIntentResult;

import java.math.BigDecimal;

/**
 * Outbound port for Stripe-specific operations that require a two-step
 * payment flow (create PaymentIntent on the server → confirm on the client).
 */
public interface StripeGatewayPort {

    /**
     * Create a Stripe PaymentIntent with the given amount and currency.
     *
     * @param amount    total charge amount (e.g. 9.99)
     * @param currency  ISO 4217 currency code (e.g. "usd")
     * @param planTitle human-readable description shown on the Stripe dashboard
     * @param userId    internal user ID stored as PaymentIntent metadata
     * @return result containing the clientSecret and paymentIntentId
     */
    StripePaymentIntentResult createPaymentIntent(BigDecimal amount, String currency, String planTitle, Long userId);
}
