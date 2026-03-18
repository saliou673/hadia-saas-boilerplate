package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "CreateSubscriptionPlanRequest")
/**
 * Request to create a new subscription plan.
 * At least one price field must be non-null.
 * {@code price} and {@code durationDays} must be provided together for a custom billing cycle.
 */
public record CreateSubscriptionPlanRequest(
        @NotBlank(message = "title is required")
        String title,

        @Nullable
        String description,

        @Nullable
        @DecimalMin(value = "0.0", message = "monthlyPrice must be non-negative")
        BigDecimal monthlyPrice,

        @Nullable
        @DecimalMin(value = "0.0", message = "yearlyPrice must be non-negative")
        BigDecimal yearlyPrice,

        @Nullable
        @DecimalMin(value = "0.0", message = "lifetimePrice must be non-negative")
        BigDecimal lifetimePrice,

        @Nullable
        @DecimalMin(value = "0.0", message = "price must be non-negative")
        BigDecimal price,

        @Nullable
        @Min(value = 1, message = "durationDays must be at least 1")
        Integer durationDays,

        @NotBlank(message = "currencyCode is required")
        @Size(max = 10, message = "currencyCode must not exceed 10 characters")
        String currencyCode,

        @Nullable
        List<String> features,

        @NotNull(message = "active is required")
        Boolean active
) {
}
