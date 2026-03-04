package com.hadiasaas.domain.models.subscription;

import com.hadiasaas.domain.enumerations.SubscriptionBillingFrequency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * Value object representing a payment processing request sent to a payment gateway.
 */
@Getter
@RequiredArgsConstructor
public class PaymentRequest {
    private final BigDecimal amount;
    private final String currencyCode;
    private final Long userId;
    private final String planTitle;
    private final SubscriptionBillingFrequency billingFrequency;
}
