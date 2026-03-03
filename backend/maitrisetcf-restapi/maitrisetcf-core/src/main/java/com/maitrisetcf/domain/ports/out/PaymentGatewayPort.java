package com.maitrisetcf.domain.ports.out;

import com.maitrisetcf.domain.models.subscription.PaymentRequest;
import com.maitrisetcf.domain.models.subscription.PaymentResult;

/**
 * Outbound port for payment gateway integrations (Stripe, PayPal, etc.).
 * Each gateway adapter implements this interface and registers itself under its mode code.
 */
public interface PaymentGatewayPort {

    /**
     * Process a payment request.
     *
     * @param request the payment details
     * @return the payment result including success status and external transaction ID
     */
    PaymentResult process(PaymentRequest request);

    /**
     * Returns the mode code that identifies this gateway (e.g. "STRIPE", "PAYPAL").
     * Must match the AppConfiguration entry code under the PAYMENT_MODE category.
     */
    String getModeCode();
}
