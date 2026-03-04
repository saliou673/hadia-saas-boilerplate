package com.maitrisetcf.domain.models.query.filter;

import com.maitrisetcf.domain.enumerations.DiscountType;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Filter class for {@link DiscountType} enum attributes.
 */
@NoArgsConstructor
public class DiscountTypeFilter extends EnumFilter<DiscountType> {

    @Serial
    private static final long serialVersionUID = 1L;

    public DiscountTypeFilter(DiscountTypeFilter filter) {
        super(filter);
    }

    @Override
    public DiscountTypeFilter copy() {
        return new DiscountTypeFilter(this);
    }
}
