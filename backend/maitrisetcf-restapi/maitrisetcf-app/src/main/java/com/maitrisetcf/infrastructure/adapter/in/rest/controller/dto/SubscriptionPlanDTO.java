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
public class SubscriptionPlanDTO extends AuditableDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String currencyCode;
    private List<String> features;
    private int durationDays;
    private boolean active;
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
