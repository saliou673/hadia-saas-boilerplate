package com.hadiasaas.domain.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Available storage backend providers.
 * <p>
 * {@code strategyCode} matches the code returned by the corresponding {@code StorageStrategy} implementation.
 */
@Getter
@RequiredArgsConstructor
public enum StorageProvider {
    LOCAL("LOCAL"),
    AWS_S3("AWS"),
    AZURE_BLOB("AZURE_BLOB"),
    GCS("GCS");

    /** Code used to look up the matching {@code StorageStrategy} bean. */
    private final String strategyCode;
}
