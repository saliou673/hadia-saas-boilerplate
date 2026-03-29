package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import com.hadiasaas.domain.enumerations.SubscriptionBillingFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "SubscribeRequest")
/**
 * Request to subscribe the current user to a plan.
 *
 * @param planId                  ID of the plan to subscribe to
 * @param paymentMode             payment mode code (e.g. STRIPE, PAYPAL)
 * @param billingFrequency        chosen billing cycle (MONTHLY, YEARLY, or LIFETIME)
 * @param discountCode            optional discount code applied at checkout
 * @param stripePaymentIntentId   optional Stripe PaymentIntent ID (pi_...) confirmed on the
 *                                frontend; required when paymentMode is STRIPE
 */
public record SubscribeRequest(
        @NotNull(message = "planId is required")
        Long planId,

        @NotBlank(message = "paymentMode is required")
        @Size(max = 20, message = "paymentMode must not exceed 20 characters")
        String paymentMode,

        @NotNull(message = "billingFrequency is required")
        SubscriptionBillingFrequency billingFrequency,

        @Nullable
        @Size(max = 50, message = "discountCode must not exceed 50 characters")
        String discountCode,

        @Nullable
        @Size(max = 100, message = "stripePaymentIntentId must not exceed 100 characters")
        String stripePaymentIntentId
) {
}
