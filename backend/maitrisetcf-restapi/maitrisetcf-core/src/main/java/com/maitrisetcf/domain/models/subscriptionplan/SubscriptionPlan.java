package com.maitrisetcf.domain.models.subscriptionplan;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.domain.models.Auditable;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing a purchasable subscription plan.
 */
@Getter
public class SubscriptionPlan extends Auditable<Long> {

    /**
     * Short display name shown to the user.
     */
    private String title;
    /**
     * Optional longer description of what the plan includes.
     */
    private String description;
    /**
     * Plan price in the given currency.
     */
    private BigDecimal price;
    /**
     * ISO currency code (must be an active CURRENCY configuration entry).
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
     * Whether this plan is currently available for purchase.
     */
    private boolean active;
    /**
     * Training delivery mode (online or on-site).
     */
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
