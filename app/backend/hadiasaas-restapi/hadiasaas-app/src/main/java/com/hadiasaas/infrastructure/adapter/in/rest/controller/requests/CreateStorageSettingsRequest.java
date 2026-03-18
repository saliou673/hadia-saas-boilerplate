package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import com.hadiasaas.domain.enumerations.StorageProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateStorageSettingsRequest")
public record CreateStorageSettingsRequest(
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
