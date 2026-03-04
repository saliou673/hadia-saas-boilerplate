package com.hadiasaas.domain.models.query.filter;

import com.hadiasaas.domain.enumerations.SubscriptionPlanType;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Filter class for {@link SubscriptionPlanType} enum attributes.
 */
@NoArgsConstructor
public class SubscriptionPlanTypeFilter extends EnumFilter<SubscriptionPlanType> {

    @Serial
    private static final long serialVersionUID = 1L;

    public SubscriptionPlanTypeFilter(SubscriptionPlanTypeFilter filter) {
        super(filter);
    }

    @Override
    public SubscriptionPlanTypeFilter copy() {
        return new SubscriptionPlanTypeFilter(this);
    }
}
