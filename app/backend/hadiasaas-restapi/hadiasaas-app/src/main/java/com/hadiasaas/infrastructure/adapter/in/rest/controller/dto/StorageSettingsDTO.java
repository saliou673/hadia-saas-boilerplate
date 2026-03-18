package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import com.hadiasaas.domain.enumerations.StorageProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(name = "StorageSettings")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
/** Response DTO representing a storage settings entry. */
public class StorageSettingsDTO extends AuditableDTO {

    private Long id;
    private StorageProvider provider;
    private String bucketName;
    private String region;
    private String endpoint;
    private boolean active;

    public StorageSettingsDTO(
            Long id,
            StorageProvider provider,
            String bucketName,
            String region,
            String endpoint,
            boolean active,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.provider = provider;
        this.bucketName = bucketName;
        this.region = region;
        this.endpoint = endpoint;
        this.active = active;
    }
}
