package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
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

    /**
     * Unique identifier of the plan.
     */
    private Long id;
    /**
     * Display title of the plan.
     */
    private String title;
    /**
     * Optional longer description.
     */
    private String description;
    /**
     * Plan price.
     */
    private BigDecimal price;
    /**
     * ISO currency code for the price.
     */
    private String currencyCode;
    /**
     * Ordered list of feature bullet points.
     */
    private List<String> features;
    /**
     * Plan duration in days; {@code -1} means lifetime access.
     */
    private int durationDays;
    /**
     * Whether this plan is publicly available.
     */
    private boolean active;
    /**
     * Training delivery mode.
     */
    private SubscriptionPlanType type;

    public SubscriptionPlanDTO(
            Long id,
            String title,
            String description,
            BigDecimal price,
            String currencyCode,
            List<String> features,
            int durationDays,
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
        this.price = price;
        this.currencyCode = currencyCode;
        this.features = features;
        this.durationDays = durationDays;
        this.active = active;
        this.type = type;
    }
}
