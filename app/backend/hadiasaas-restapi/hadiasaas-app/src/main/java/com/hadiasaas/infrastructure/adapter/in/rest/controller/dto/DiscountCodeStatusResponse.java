package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import com.hadiasaas.domain.enumerations.DiscountType;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Public status response for a discount code.
 */
public record DiscountCodeStatusResponse(
        String code,
        boolean valid,
        @Nullable DiscountType discountType,
        @Nullable BigDecimal discountValue,
        @Nullable String currencyCode,
        @Nullable LocalDate expirationDate
) {
}
