package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UpsertEnterpriseProfileRequest")
public record UpsertEnterpriseProfileRequest(
        @NotBlank(message = "companyName is required")
        String companyName,

        @Nullable
        String legalForm,

        @Nullable
        String registrationNumber,

        @Nullable
        String vatNumber,

        @Nullable
        String addressLine1,

        @Nullable
        String addressLine2,

        @Nullable
        String city,

        @Nullable
        String postalCode,

        @Nullable
        @Size(min = 2, max = 2, message = "countryCode must be ISO 3166-1 alpha-2 (2 characters)")
        String countryCode,

        @Nullable
        String phoneNumber,

        @Nullable
        String email,

        @Nullable
        String website,

        @Nullable
        String logoUrl
) {
}
