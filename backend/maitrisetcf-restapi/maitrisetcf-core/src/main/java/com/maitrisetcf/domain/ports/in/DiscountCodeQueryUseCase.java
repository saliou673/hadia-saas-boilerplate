package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.discountcode.DiscountCode;
import com.maitrisetcf.domain.models.discountcode.DiscountCodeFilter;
import com.maitrisetcf.domain.models.query.PagedResult;

/**
 * Input port for discount code queries.
 */
public interface DiscountCodeQueryUseCase {

    PagedResult<DiscountCode> findAll(DiscountCodeFilter filter, int page, int size);

    DiscountCode getByCode(String code);
}
