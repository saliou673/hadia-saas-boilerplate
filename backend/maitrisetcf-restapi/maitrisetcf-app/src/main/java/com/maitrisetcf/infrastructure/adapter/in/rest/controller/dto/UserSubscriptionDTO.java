package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import com.maitrisetcf.domain.enumerations.SubscriptionBillingFrequency;
import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Schema(name = "UserSubscription")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
/** Response DTO representing a user's subscription. */
public class UserSubscriptionDTO extends AuditableDTO {

    private Long id;
    private Long userId;
    private Long planId;
    private String planTitle;
    private BigDecimal pricePaid;
    private String discountCodeUsed;
    private BigDecimal discountAmount;
    private String currencyCode;
    private SubscriptionBillingFrequency billingFrequency;
    private String paymentMode;
    private String externalPaymentId;
    private UserSubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean autoRenew;

    public UserSubscriptionDTO(
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
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
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
}
