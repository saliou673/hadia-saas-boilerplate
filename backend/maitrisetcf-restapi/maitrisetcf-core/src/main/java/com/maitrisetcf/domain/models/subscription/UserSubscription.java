package com.maitrisetcf.domain.models.subscription;

import com.maitrisetcf.domain.enumerations.SubscriptionBillingFrequency;
import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import com.maitrisetcf.domain.models.Auditable;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Domain entity representing a user's subscription to a plan.
 */
@Getter
public class UserSubscription extends Auditable<Long> {

    private Long userId;

    private Long planId;
    /**
     * Snapshot of the plan title at subscription time.
     */
    private String planTitle;
    /**
     * Snapshot of the price paid.
     */
    private BigDecimal pricePaid;
    private String discountCodeUsed;
    private BigDecimal discountAmount;

    private String currencyCode;

    private SubscriptionBillingFrequency billingFrequency;
    /**
     * Payment mode used (e.g. STRIPE, PAYPAL).
     */
    private String paymentMode;
    /**
     * External transaction ID returned by the payment gateway.
     */
    private String externalPaymentId;

    private UserSubscriptionStatus status;

    private LocalDate startDate;
    /**
     * Null for lifetime subscriptions.
     */
    private LocalDate endDate;

    private boolean autoRenew;

    private UserSubscription(
            Long id,
            Long userId,
            Long planId,
            String planTitle,
            BigDecimal pricePaid,
            String discountCodeUsed,
            BigDecimal discountAmount,
            String currencyCode,
            SubscriptionBillingFrequency billingFrequency,
            String paymentMode,
            String externalPaymentId,
            UserSubscriptionStatus status,
            LocalDate startDate,
            LocalDate endDate,
            boolean autoRenew,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.userId = userId;
        this.planId = planId;
        this.planTitle = planTitle;
        this.pricePaid = pricePaid;
        this.discountCodeUsed = discountCodeUsed;
        this.discountAmount = discountAmount;
        this.currencyCode = currencyCode;
        this.billingFrequency = billingFrequency;
        this.paymentMode = paymentMode;
        this.externalPaymentId = externalPaymentId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.autoRenew = autoRenew;
    }

    public static UserSubscription create(
            Long userId,
            Long planId,
            String planTitle,
            BigDecimal pricePaid,
            String discountCodeUsed,
            BigDecimal discountAmount,
            String currencyCode,
            SubscriptionBillingFrequency billingFrequency,
            String paymentMode,
            String externalPaymentId,
            UserSubscriptionStatus status,
            LocalDate startDate,
            LocalDate endDate,
            boolean autoRenew
    ) {
        return new UserSubscription(null, userId, planId, planTitle, pricePaid, discountCodeUsed, discountAmount, currencyCode,
                                    billingFrequency, paymentMode, externalPaymentId, status, startDate, endDate, autoRenew,
                                    null, null, null);
    }

    public static UserSubscription rehydrate(
            Long id,
            Long userId,
            Long planId,
            String planTitle,
            BigDecimal pricePaid,
            String discountCodeUsed,
            BigDecimal discountAmount,
            String currencyCode,
            SubscriptionBillingFrequency billingFrequency,
            String paymentMode,
            String externalPaymentId,
            UserSubscriptionStatus status,
            LocalDate startDate,
            LocalDate endDate,
            boolean autoRenew,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new UserSubscription(id, userId, planId, planTitle, pricePaid, discountCodeUsed, discountAmount, currencyCode,
                                    billingFrequency, paymentMode, externalPaymentId, status, startDate, endDate, autoRenew,
                                    creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void cancel() {
        this.status = UserSubscriptionStatus.CANCELLED;
    }

    public void activate(String externalPaymentId) {
        this.status = UserSubscriptionStatus.ACTIVE;
        this.externalPaymentId = externalPaymentId;
    }

    public void renew(LocalDate newEndDate, String newExternalPaymentId) {
        this.endDate = newEndDate;
        this.externalPaymentId = newExternalPaymentId;
        this.status = UserSubscriptionStatus.ACTIVE;
    }
}
