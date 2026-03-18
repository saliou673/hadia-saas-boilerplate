package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import com.hadiasaas.domain.enumerations.StorageProvider;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record UpdateStorageSettingsRequest(
        @NotNull(message = "provider is required")
        StorageProvider provider,

        @Nullable
        String bucketName,

        @Nullable
        String region,

        @Nullable
        String endpoint,

        @NotNull(message = "active is required")
        Boolean active
) {
}
