package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.discountcode.DiscountCode;
import com.hadiasaas.domain.models.discountcode.DiscountCodeFilter;
import com.hadiasaas.domain.models.query.PagedResult;

/**
 * Input port for discount code queries.
 */
public interface DiscountCodeQueryUseCase {

    PagedResult<DiscountCode> findAll(DiscountCodeFilter filter, int page, int size);

    DiscountCode getByCode(String code);
}
