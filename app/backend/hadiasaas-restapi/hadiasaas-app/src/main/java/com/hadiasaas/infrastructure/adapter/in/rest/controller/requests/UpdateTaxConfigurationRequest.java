package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(name = "UpdateTaxConfigurationRequest")
public record UpdateTaxConfigurationRequest(
        @NotBlank(message = "code is required")
        @Size(max = 50, message = "code must not exceed 50 characters")
        String code,

        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "rate is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "rate must be >= 0")
        @DecimalMax(value = "1.0", inclusive = true, message = "rate must be <= 1")
        BigDecimal rate,

        @Nullable
        String description,

        @NotNull(message = "active is required")
        Boolean active
) {
}
