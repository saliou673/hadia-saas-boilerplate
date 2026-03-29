package com.hadiasaas.domain.models.subscription;

import com.hadiasaas.domain.enumerations.SubscriptionBillingFrequency;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Value object representing a payment processing request sent to a payment gateway.
 */
@Getter
@Builder
public class PaymentRequest {
    private final BigDecimal amount;
    private final String currencyCode;
    private final Long userId;
    private final String planTitle;
    private final SubscriptionBillingFrequency billingFrequency;
    /**
     * Optional Stripe PaymentIntent ID. When present, the Stripe gateway will
     * verify this intent rather than charging a new token. May be null for non-Stripe gateways.
     */
    private final String paymentIntentId;
}
