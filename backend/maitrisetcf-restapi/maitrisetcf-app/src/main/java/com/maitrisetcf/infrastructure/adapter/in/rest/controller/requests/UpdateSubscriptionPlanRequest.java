package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "UpdateSubscriptionPlanRequest")
public record UpdateSubscriptionPlanRequest(
        @NotBlank(message = "title is required")
        String title,

        @Nullable
        String description,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "price must be non-negative")
        BigDecimal price,

        @NotBlank(message = "currencyCode is required")
        @Size(max = 10, message = "currencyCode must not exceed 10 characters")
        String currencyCode,

        @Nullable
        List<String> features,

        @NotNull(message = "durationDays is required")
        @Min(value = -1, message = "durationDays must be -1 (lifetime) or a positive number of days")
        Integer durationDays,

        @NotNull(message = "active is required")
        Boolean active,

        @NotNull(message = "type is required")
        SubscriptionPlanType type
) {
}
