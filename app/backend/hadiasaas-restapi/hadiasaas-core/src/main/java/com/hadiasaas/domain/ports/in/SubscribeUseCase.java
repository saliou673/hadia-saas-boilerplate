package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.enumerations.SubscriptionBillingFrequency;
import com.hadiasaas.domain.models.subscription.StripePaymentIntentResult;
import com.hadiasaas.domain.models.subscription.UserSubscription;

/**
 * Input port for subscription lifecycle operations.
 */
public interface SubscribeUseCase {

    /**
     * Subscribe the current user to the given plan using the specified payment mode and billing cycle.
     *
     * @param planId                  ID of the plan to subscribe to
     * @param paymentMode             payment mode code (e.g. STRIPE, PAYPAL)
     * @param billingFrequency        chosen billing cycle (MONTHLY, YEARLY, or LIFETIME)
     * @param discountCode            optional discount code applied at checkout (nullable)
     * @param stripePaymentIntentId   optional Stripe PaymentIntent ID (pi_...) to verify instead of
     *                                creating a new charge; required when paymentMode is STRIPE (nullable)
     * @return the created subscription
     */
    UserSubscription subscribe(Long planId, String paymentMode, SubscriptionBillingFrequency billingFrequency,
                               String discountCode, String stripePaymentIntentId);

    /**
     * Create a Stripe PaymentIntent for the given plan so the frontend can collect card details.
     * The resulting {@code clientSecret} must be used with {@code stripe.confirmPayment()} on
     * the frontend, and the returned {@code paymentIntentId} must be sent back with
     * {@link #subscribe} to finalise the subscription.
     *
     * @param planId           ID of the plan to subscribe to
     * @param billingFrequency chosen billing cycle
     * @param discountCode     optional discount code (affects the charged amount, nullable)
     * @return Stripe PaymentIntent data including the clientSecret for the frontend
     */
    StripePaymentIntentResult createStripePaymentIntent(Long planId, SubscriptionBillingFrequency billingFrequency,
                                                        String discountCode);

    /**
     * Renew an existing subscription (creates a new subscription record for the next period).
     *
     * @param subscriptionId ID of the subscription to renew
     * @return the new renewed subscription
     */
    UserSubscription renew(Long subscriptionId);

    /**
     * Cancel a subscription (current user must own it, or caller is admin).
     *
     * @param subscriptionId ID of the subscription to cancel
     * @param forceAdmin     true when called by an admin (bypasses ownership check)
     * @return the cancelled subscription
     */
    UserSubscription cancel(Long subscriptionId, boolean forceAdmin);

    /**
     * Get a subscription by ID (admin only).
     *
     * @param id subscription ID
     * @return the subscription
     */
    UserSubscription getById(Long id);
}
