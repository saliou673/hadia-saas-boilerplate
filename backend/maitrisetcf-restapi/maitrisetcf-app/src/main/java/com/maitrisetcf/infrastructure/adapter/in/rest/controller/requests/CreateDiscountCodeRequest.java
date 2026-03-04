package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import com.maitrisetcf.domain.enumerations.DiscountType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request to create a discount code.
 */
public record CreateDiscountCodeRequest(
        @NotBlank(message = "code is required")
        @Size(max = 50, message = "code must not exceed 50 characters")
        String code,

        @NotNull(message = "discountType is required")
        DiscountType discountType,

        @NotNull(message = "discountValue is required")
        @DecimalMin(value = "0.01", message = "discountValue must be greater than 0")
        BigDecimal discountValue,

        @Nullable
        @Size(max = 10, message = "currencyCode must not exceed 10 characters")
        String currencyCode,

        @NotNull(message = "active is required")
        Boolean active,

        @Nullable
        LocalDate expirationDate,

        @Nullable
        @Min(value = 1, message = "maxUsages must be at least 1")
        Integer maxUsages
) {
}
