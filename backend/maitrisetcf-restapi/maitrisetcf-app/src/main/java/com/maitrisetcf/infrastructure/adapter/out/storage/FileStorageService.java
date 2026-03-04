package com.maitrisetcf.infrastructure.adapter.out.storage;

import com.maitrisetcf.config.ApplicationProperties;
import com.maitrisetcf.domain.exceptions.TechnicalException;
import com.maitrisetcf.domain.ports.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Infrastructure service for storing and resolving files under the configured upload directory.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService implements FileStoragePort {

    private final ApplicationProperties applicationProperties;

    @Override
    public Path ensureDirectory(String subdirectory) {
        try {
            Path uploadBase = resolveUploadBase();
            Path targetDirectory = uploadBase.resolve(subdirectory).normalize();
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
        try {
            Path targetDirectory = ensureDirectory(subdirectory);

            Path targetPath = targetDirectory.resolve(filename).normalize();
            if (!targetPath.startsWith(targetDirectory)) {
                throw new TechnicalException("Resolved path escapes upload directory");
            }

            Files.write(targetPath, content);
            log.debug("Stored file: {}", targetPath);
            return subdirectory + "/" + filename;
        } catch (IOException e) {
            throw new TechnicalException("Could not store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Path resolve(String relativePath) {
        Path uploadBase = resolveUploadBase();
        Path resolved = uploadBase.resolve(relativePath).normalize();
        if (!resolved.startsWith(uploadBase)) {
            throw new TechnicalException("Access to path outside upload directory is not allowed");
        }
        return resolved;
    }

    private Path resolveUploadBase() {
        return Paths.get(applicationProperties.getStorage().uploadDir()).toAbsolutePath().normalize();
    }
}
