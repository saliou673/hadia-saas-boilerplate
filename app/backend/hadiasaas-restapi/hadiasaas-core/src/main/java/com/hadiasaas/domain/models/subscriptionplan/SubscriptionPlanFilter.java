package com.hadiasaas.domain.models.subscriptionplan;

import com.hadiasaas.domain.models.query.AuditableFilter;
import com.hadiasaas.domain.models.query.filter.BooleanFilter;
import com.hadiasaas.domain.models.query.filter.LongFilter;
import com.hadiasaas.domain.models.query.filter.StringFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Filter criteria for querying {@link SubscriptionPlan} entities.
 * Null fields mean no constraint on that attribute.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class SubscriptionPlanFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Filter on the surrogate identifier.
     */
    private LongFilter id;
    /**
     * Filter on the plan title.
     */
    private StringFilter title;
    /**
     * Filter on the currency code.
     */
    private StringFilter currencyCode;
    /**
     * Filter on the active flag.
     */
    private BooleanFilter active;

    public SubscriptionPlanFilter(SubscriptionPlanFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.currencyCode = other.currencyCode == null ? null : other.currencyCode.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public SubscriptionPlanFilter copy() {
        return new SubscriptionPlanFilter(this);
    }
}
