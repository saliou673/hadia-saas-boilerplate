package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.enumerations.DiscountType;
import com.hadiasaas.domain.models.discountcode.DiscountCode;

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
