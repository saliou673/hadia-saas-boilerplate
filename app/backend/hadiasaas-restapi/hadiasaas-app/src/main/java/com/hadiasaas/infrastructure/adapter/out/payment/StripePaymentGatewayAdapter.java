package com.hadiasaas.infrastructure.adapter.out.payment;

import com.hadiasaas.config.ApplicationProperties;
import com.hadiasaas.domain.exceptions.PaymentProcessingException;
import com.hadiasaas.domain.models.subscription.PaymentRequest;
import com.hadiasaas.domain.models.subscription.PaymentResult;
import com.hadiasaas.domain.models.subscription.StripePaymentIntentResult;
import com.hadiasaas.domain.ports.out.PaymentGatewayPort;
import com.hadiasaas.domain.ports.out.StripeGatewayPort;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Real Stripe payment gateway adapter.
 * <p>
 * Implements two operations:
 * <ul>
 *   <li>{@link StripeGatewayPort#createPaymentIntent} — server-side creation of a
 *       PaymentIntent so the frontend can collect card details.</li>
 *   <li>{@link PaymentGatewayPort#process} — verifies an already-confirmed
 *       PaymentIntent before recording the subscription.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StripePaymentGatewayAdapter implements PaymentGatewayPort, StripeGatewayPort {

    private final ApplicationProperties applicationProperties;

    // ── StripeGatewayPort ──────────────────────────────────────────────────────

    @Override
    public StripePaymentIntentResult createPaymentIntent(BigDecimal amount, String currency, String planTitle, Long userId) {
        configureStripe();

        // Stripe amounts are in the currency's smallest unit (cents for USD).
        long amountInSmallestUnit = amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInSmallestUnit)
                    .setCurrency(currency.toLowerCase())
                    .setDescription(planTitle)
                    .putMetadata("userId", String.valueOf(userId))
                    .putMetadata("planTitle", planTitle)
                    // Automatic payment methods: let Stripe decide the best UI for the user's region.
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            log.info("Stripe PaymentIntent created: id={}, amount={} {}, userId={}",
                     intent.getId(), amountInSmallestUnit, currency, userId);

            return new StripePaymentIntentResult(
                    intent.getClientSecret(),
                    intent.getId(),
                    amountInSmallestUnit,
                    currency.toLowerCase()
            );
        } catch (StripeException e) {
            log.error("Failed to create Stripe PaymentIntent: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Failed to initiate Stripe payment: " + e.getUserMessage());
        }
    }

    // ── PaymentGatewayPort ────────────────────────────────────────────────────

    @Override
    public PaymentResult process(PaymentRequest request) {
        configureStripe();

        String paymentIntentId = request.getPaymentIntentId();

        if (StringUtils.isBlank(paymentIntentId)) {
            log.error("Stripe process() called without a paymentIntentId for userId={}", request.getUserId());
            return PaymentResult.failure("Stripe payment requires a confirmed PaymentIntent ID");
        }

        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            if (!"succeeded".equals(intent.getStatus())) {
                log.warn("Stripe PaymentIntent {} has status '{}', expected 'succeeded'", paymentIntentId, intent.getStatus());
                return PaymentResult.failure("Payment not completed. Status: " + intent.getStatus());
            }

            log.info("Stripe PaymentIntent {} verified successfully for userId={}", paymentIntentId, request.getUserId());
            return PaymentResult.success(paymentIntentId);
        } catch (StripeException e) {
            log.error("Failed to retrieve Stripe PaymentIntent {}: {}", paymentIntentId, e.getMessage(), e);
            return PaymentResult.failure("Stripe verification failed: " + e.getUserMessage());
        }
    }

    @Override
    public String getModeCode() {
        return "STRIPE";
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void configureStripe() {
        String secretKey = applicationProperties.getStripe() != null
                ? applicationProperties.getStripe().secretKey()
                : null;
        if (StringUtils.isBlank(secretKey)) {
            throw new PaymentProcessingException("Stripe secret key is not configured. Set STRIPE_SECRET_KEY.");
        }
        Stripe.apiKey = secretKey;
    }
}
