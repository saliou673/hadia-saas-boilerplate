package com.maitrisetcf.domain.models.subscriptionplan;

import com.maitrisetcf.domain.models.query.AuditableFilter;
import com.maitrisetcf.domain.models.query.filter.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class SubscriptionPlanFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter title;
    private BigDecimalFilter price;
    private StringFilter currencyCode;
    private IntegerFilter durationDays;
    private BooleanFilter active;
    private SubscriptionPlanTypeFilter type;

    public SubscriptionPlanFilter(SubscriptionPlanFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.currencyCode = other.currencyCode == null ? null : other.currencyCode.copy();
        this.durationDays = other.durationDays == null ? null : other.durationDays.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public SubscriptionPlanFilter copy() {
        return new SubscriptionPlanFilter(this);
    }
}
