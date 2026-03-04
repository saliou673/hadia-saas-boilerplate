package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.enumerations.DiscountType;
import com.maitrisetcf.domain.models.discountcode.DiscountCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Input port for discount code CRUD operations.
 */
public interface DiscountCodeUseCase {

    DiscountCode create(
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages
    );

    DiscountCode update(
            Long id,
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages
    );

    void delete(Long id);

    DiscountCode getById(Long id);
}
