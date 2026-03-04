package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import com.hadiasaas.domain.enumerations.SubscriptionPlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Schema(name = "SubscriptionPlan")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
/** Response DTO representing a subscription plan. */
public class SubscriptionPlanDTO extends AuditableDTO {

    private Long id;
    private String title;
    private String description;
    /**
     * Price for a monthly billing cycle; {@code null} if not offered.
     */
    private BigDecimal monthlyPrice;
    /**
     * Price for a yearly billing cycle; {@code null} if not offered.
     */
    private BigDecimal yearlyPrice;
    /**
     * Price for lifetime access; {@code null} if not offered.
     */
    private BigDecimal lifetimePrice;
    /**
     * Price for a custom billing cycle; {@code null} if not offered. Paired with {@link #durationDays}.
     */
    private BigDecimal price;
    /**
     * Duration in days for the custom billing cycle; {@code null} if no custom cycle.
     */
    private Integer durationDays;
    private String currencyCode;
    private List<String> features;
    private boolean active;
    private SubscriptionPlanType type;

    public SubscriptionPlanDTO(
            Long id,
            String title,
            String description,
            BigDecimal monthlyPrice,
            BigDecimal yearlyPrice,
            BigDecimal lifetimePrice,
            BigDecimal price,
            Integer durationDays,
            String currencyCode,
            List<String> features,
            boolean active,
            SubscriptionPlanType type,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.title = title;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.lifetimePrice = lifetimePrice;
        this.price = price;
        this.durationDays = durationDays;
        this.currencyCode = currencyCode;
        this.features = features;
        this.active = active;
        this.type = type;
    }
}
