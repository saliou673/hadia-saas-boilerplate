package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import com.hadiasaas.domain.enumerations.SubscriptionBillingFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request to create a Stripe PaymentIntent for a given plan before card confirmation.
 */
public record StripePaymentIntentRequest(
        @NotNull(message = "planId is required")
        Long planId,

        @NotNull(message = "billingFrequency is required")
        SubscriptionBillingFrequency billingFrequency,

        @Nullable
        @Size(max = 50, message = "discountCode must not exceed 50 characters")
        String discountCode
) {
}
