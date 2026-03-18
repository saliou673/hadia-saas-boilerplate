package com.hadiasaas.domain.models.storagesettings;

import com.hadiasaas.domain.enumerations.StorageProvider;
import com.hadiasaas.domain.models.Auditable;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain entity representing a storage backend configuration.
 */
@Getter
public class StorageSettings extends Auditable<Long> {

    private final StorageProvider provider;
    private String bucketName;
    private String region;
    private String endpoint;
    private boolean active;

    private StorageSettings(
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
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.provider = provider;
        this.bucketName = bucketName;
        this.region = region;
        this.endpoint = endpoint;
        this.active = active;
    }

    public static StorageSettings create(
            StorageProvider provider,
            String bucketName,
            String region,
            String endpoint,
            boolean active
    ) {
        return new StorageSettings(null, provider, bucketName, region, endpoint, active, null, null, null);
    }

    public static StorageSettings rehydrate(
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
        return new StorageSettings(id, provider, bucketName, region, endpoint, active, creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(StorageProvider provider, String bucketName, String region, String endpoint, boolean active) {
        this.bucketName = bucketName;
        this.region = region;
        this.endpoint = endpoint;
        this.active = active;
    }
}
