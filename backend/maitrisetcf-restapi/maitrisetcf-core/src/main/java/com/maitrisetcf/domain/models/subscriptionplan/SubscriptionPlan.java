package com.maitrisetcf.domain.models.subscriptionplan;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.domain.models.Auditable;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SubscriptionPlan extends Auditable<Long> {

    private String title;
    private String description;
    private BigDecimal price;
    private String currencyCode;
    private List<String> features;
    private int durationDays;
    private boolean active;
    private SubscriptionPlanType type;

    private SubscriptionPlan(
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
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.title = title;
        this.description = description;
        this.price = price;
        this.currencyCode = currencyCode;
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
        this.durationDays = durationDays;
        this.active = active;
        this.type = type;
    }

    public static SubscriptionPlan create(
            String title,
            String description,
            BigDecimal price,
            String currencyCode,
            List<String> features,
            int durationDays,
            boolean active,
            SubscriptionPlanType type
    ) {
        return new SubscriptionPlan(null, title, description, price, currencyCode, features, durationDays, active, type, null, null, null);
    }

    public static SubscriptionPlan rehydrate(
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
        return new SubscriptionPlan(id, title, description, price, currencyCode, features, durationDays, active, type, creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(
            String title,
            String description,
            BigDecimal price,
            String currencyCode,
            List<String> features,
            int durationDays,
            boolean active,
            SubscriptionPlanType type
    ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.currencyCode = currencyCode;
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
        this.durationDays = durationDays;
        this.active = active;
        this.type = type;
    }
}
