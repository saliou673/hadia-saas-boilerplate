package com.hadiasaas.infrastructure.adapter.out.payment;

import com.hadiasaas.domain.models.subscription.PaymentRequest;
import com.hadiasaas.domain.models.subscription.PaymentResult;
import com.hadiasaas.domain.ports.out.PaymentGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Stripe payment gateway adapter.
 * <p>
 * This is a stub implementation. Replace with real Stripe SDK calls when credentials are available.
 */
@Slf4j
@Component
public class StripePaymentGatewayAdapter implements PaymentGatewayPort {

    @Override
    public PaymentResult process(PaymentRequest request) {
        log.info("Processing Stripe payment: userId={}, amount={} {}, plan={}",
                 request.getUserId(), request.getAmount(), request.getCurrencyCode(), request.getPlanTitle());
        // TODO: integrate with Stripe SDK
        String externalId = "stripe_" + UUID.randomUUID().toString().replace("-", "");
        return PaymentResult.success(externalId);
    }

    @Override
    public String getModeCode() {
        return "STRIPE";
    }
}
