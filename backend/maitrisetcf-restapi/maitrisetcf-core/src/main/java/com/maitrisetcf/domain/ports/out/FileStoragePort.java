package com.maitrisetcf.domain.ports.out;

import java.nio.file.Path;

/**
 * Outbound port for storing and resolving files under the configured upload directory.
 */
public interface FileStoragePort {

    /**
     * Ensures a subdirectory exists under the upload root.
     *
     * @param subdirectory subdirectory under the upload root
     * @return the resolved directory path
     */
    Path ensureDirectory(String subdirectory);

    /**
     * Stores binary content in a subdirectory of the upload directory.
     *
     * @param subdirectory subdirectory under the upload root
     * @param filename     file name to write
     * @param content      binary file content
     * @return the stored path relative to the upload directory
     */
    String store(String subdirectory, String filename, byte[] content);

    /**
     * Resolves a stored path relative to the upload directory.
     *
     * @param relativePath stored relative path
     * @return the resolved absolute path
     */
    Path resolve(String relativePath);
}
