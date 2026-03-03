package com.maitrisetcf.infrastructure.adapter.out.payment;

import com.maitrisetcf.domain.models.subscription.PaymentRequest;
import com.maitrisetcf.domain.models.subscription.PaymentResult;
import com.maitrisetcf.domain.ports.out.PaymentGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * PayPal payment gateway adapter.
 * <p>
 * This is a stub implementation. Replace with real PayPal SDK calls when credentials are available.
 */
@Slf4j
@Component
public class PaypalPaymentGatewayAdapter implements PaymentGatewayPort {

    @Override
    public PaymentResult process(PaymentRequest request) {
        log.info("Processing PayPal payment: userId={}, amount={} {}, plan={}",
                 request.getUserId(), request.getAmount(), request.getCurrencyCode(), request.getPlanTitle());
        // TODO: integrate with PayPal SDK
        String externalId = "paypal_" + UUID.randomUUID().toString().replace("-", "");
        return PaymentResult.success(externalId);
    }

    @Override
    public String getModeCode() {
        return "PAYPAL";
    }
}
