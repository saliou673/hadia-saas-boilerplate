package com.hadiasaas.domain.models.discountcode;

import com.hadiasaas.domain.models.query.AuditableFilter;
import com.hadiasaas.domain.models.query.filter.BooleanFilter;
import com.hadiasaas.domain.models.query.filter.DiscountTypeFilter;
import com.hadiasaas.domain.models.query.filter.LongFilter;
import com.hadiasaas.domain.models.query.filter.StringFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Filter criteria for querying {@link DiscountCode} entities.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class DiscountCodeFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter code;
    private BooleanFilter active;
    private DiscountTypeFilter discountType;

    public DiscountCodeFilter(DiscountCodeFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.discountType = other.discountType == null ? null : other.discountType.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public DiscountCodeFilter copy() {
        return new DiscountCodeFilter(this);
    }
}
