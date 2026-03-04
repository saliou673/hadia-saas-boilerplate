package com.hadiasaas.infrastructure.adapter.out.storage;

import com.hadiasaas.config.ApplicationProperties;
import com.hadiasaas.domain.exceptions.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Path;

/**
 * AWS S3-backed file storage.
 */
@Slf4j
@Component
@RequiredArgsConstructor
class AwsS3FileStorageStrategy implements StorageStrategy {

    private final ApplicationProperties applicationProperties;
    private final ObjectProvider<S3Client> s3ClientProvider;

    @Override
    public String code() {
        return AWS_CODE;
    }

    @Override
    public Path ensureDirectory(String subdirectory) {
        return StoragePathSupport.normalizeSubdirectory(subdirectory);
    }

    @Override
    public String store(String subdirectory, String filename, byte[] content) {
        String relativePath = StoragePathSupport.buildRelativePath(subdirectory, filename);
        try {
            getS3Client().putObject(
                    PutObjectRequest.builder()
                            .bucket(getBucket())
                            .key(relativePath)
                            .contentType("application/octet-stream")
                            .build(),
                    RequestBody.fromBytes(content)
            );
            log.debug("Stored file in S3: {}", relativePath);
            return relativePath;
        } catch (S3Exception e) {
            throw new TechnicalException("Could not store file in AWS S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public byte[] read(String relativePath) {
        String normalizedPath = StoragePathSupport.normalizeRelativePath(relativePath).toString().replace('\\', '/');
        try {
            return getS3Client().getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(getBucket())
                            .key(normalizedPath)
                            .build()
            ).asByteArray();
        } catch (NoSuchKeyException e) {
            throw new TechnicalException("Stored file not found in AWS S3: " + normalizedPath, e);
        } catch (S3Exception e) {
            throw new TechnicalException("Could not read file from AWS S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    private S3Client getS3Client() {
        S3Client s3Client = s3ClientProvider.getIfAvailable();
        if (s3Client == null) {
            throw new TechnicalException("AWS S3 client is not configured");
        }
        return s3Client;
    }

    private String getBucket() {
        String bucket = applicationProperties.getStorage().aws().bucket();
        if (StringUtils.isBlank(bucket)) {
            throw new TechnicalException("AWS S3 bucket is not configured");
        }
        return bucket;
    }
}
