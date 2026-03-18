package com.hadiasaas.domain.models.subscriptionplan;

import com.hadiasaas.domain.models.Auditable;
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
     * Duration in days for the custom billing cycle; {@code null} when no custom cycle is offered.
     */
    private Integer durationDays;
    /**
     * ISO currency code (must be an active CURRENCY configuration entry).
     */
    private String currencyCode;
    /**
     * Ordered list of feature bullet points.
     */
    private List<String> features;
    /**
     * Whether this plan is currently available for purchase.
     */
    private boolean active;

    private SubscriptionPlan(
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
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.title = title;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.lifetimePrice = lifetimePrice;
        this.price = price;
        this.durationDays = durationDays;
        this.currencyCode = currencyCode;
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
        this.active = active;
    }

    public static SubscriptionPlan create(
            String title,
            String description,
            BigDecimal monthlyPrice,
            BigDecimal yearlyPrice,
            BigDecimal lifetimePrice,
            BigDecimal price,
            Integer durationDays,
            String currencyCode,
            List<String> features,
            boolean active
    ) {
        return new SubscriptionPlan(null,
                                    title,
                                    description,
                                    monthlyPrice,
                                    yearlyPrice,
                                    lifetimePrice,
                                    price,
                                    durationDays,
                                    currencyCode,
                                    features,
                                    active,
                                    null,
                                    null,
                                    null);
    }

    public static SubscriptionPlan rehydrate(
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
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new SubscriptionPlan(id,
                                    title,
                                    description,
                                    monthlyPrice,
                                    yearlyPrice,
                                    lifetimePrice,
                                    price,
                                    durationDays,
                                    currencyCode,
                                    features,
                                    active,
                                    creationDate,
                                    lastUpdateDate,
                                    lastUpdatedBy);
    }

    public void update(
            String title,
            String description,
            BigDecimal monthlyPrice,
            BigDecimal yearlyPrice,
            BigDecimal lifetimePrice,
            BigDecimal price,
            Integer durationDays,
            String currencyCode,
            List<String> features,
            boolean active
    ) {
        this.title = title;
        this.description = description;
        this.monthlyPrice = monthlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.lifetimePrice = lifetimePrice;
        this.price = price;
        this.durationDays = durationDays;
        this.currencyCode = currencyCode;
        this.features = features != null ? new ArrayList<>(features) : new ArrayList<>();
        this.active = active;
    }
}
