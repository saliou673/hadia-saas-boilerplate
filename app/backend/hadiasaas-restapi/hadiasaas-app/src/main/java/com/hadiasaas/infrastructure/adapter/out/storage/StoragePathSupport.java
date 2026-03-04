package com.hadiasaas.infrastructure.adapter.out.storage;

import com.hadiasaas.domain.exceptions.TechnicalException;

import java.nio.file.Path;
import java.nio.file.Paths;

final class StoragePathSupport {

    private StoragePathSupport() {}

    static Path normalizeRelativePath(String relativePath) {
        Path normalized = Paths.get(relativePath).normalize();
        if (normalized.isAbsolute() || normalized.startsWith("..")) {
            throw new TechnicalException("Path outside storage root is not allowed");
        }
        return normalized;
    }

    static Path normalizeSubdirectory(String subdirectory) {
        return normalizeRelativePath(subdirectory);
    }

    static String buildRelativePath(String subdirectory, String filename) {
        Path normalizedSubdirectory = normalizeSubdirectory(subdirectory);
        Path normalizedFile = normalizeRelativePath(filename);
        if (normalizedFile.getNameCount() != 1) {
            throw new TechnicalException("Filename must not contain path segments");
        }
        return normalizedSubdirectory.resolve(normalizedFile).toString().replace('\\', '/');
    }
}
