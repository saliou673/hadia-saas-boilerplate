package com.hadiasaas.infrastructure.adapter.out.storage;

import com.hadiasaas.config.ApplicationProperties;
import com.hadiasaas.domain.exceptions.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Local filesystem storage under the configured upload directory.
 */
@Slf4j
@Component
@RequiredArgsConstructor
class LocalFileStorageStrategy implements StorageStrategy {

    private final ApplicationProperties applicationProperties;

    @Override
    public String code() {
        return LOCAL_CODE;
    }

    @Override
    public Path ensureDirectory(String subdirectory) {
        try {
            Path uploadBase = resolveUploadBase();
            Path targetDirectory = uploadBase.resolve(StoragePathSupport.normalizeSubdirectory(subdirectory)).normalize();
            if (!targetDirectory.startsWith(uploadBase)) {
                throw new TechnicalException("Resolved directory escapes upload directory");
            }

            Files.createDirectories(targetDirectory);
            return targetDirectory;
        } catch (IOException e) {
            throw new TechnicalException("Could not create directory: " + e.getMessage(), e);
        }
    }

    @Override
    public String store(String subdirectory, String filename, byte[] content) {
        String relativePath = StoragePathSupport.buildRelativePath(subdirectory, filename);
        try {
            Path uploadBase = resolveUploadBase();
            Path targetPath = uploadBase.resolve(StoragePathSupport.normalizeRelativePath(relativePath)).normalize();
            if (!targetPath.startsWith(uploadBase)) {
                throw new TechnicalException("Resolved path escapes upload directory");
            }

            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, content);
            log.debug("Stored file locally: {}", targetPath);
            return relativePath;
        } catch (IOException e) {
            throw new TechnicalException("Could not store file: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] read(String relativePath) {
        Path uploadBase = resolveUploadBase();
        Path resolved = uploadBase.resolve(StoragePathSupport.normalizeRelativePath(relativePath)).normalize();
        if (!resolved.startsWith(uploadBase)) {
            throw new TechnicalException("Access to path outside upload directory is not allowed");
        }

        try {
            return Files.readAllBytes(resolved);
        } catch (IOException e) {
            throw new TechnicalException("Could not read file: " + e.getMessage(), e);
        }
    }

    private Path resolveUploadBase() {
        return Paths.get(applicationProperties.getStorage().uploadDir()).toAbsolutePath().normalize();
    }
}
