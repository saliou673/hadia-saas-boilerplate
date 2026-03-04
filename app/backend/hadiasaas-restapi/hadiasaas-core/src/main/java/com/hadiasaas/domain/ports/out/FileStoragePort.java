package com.hadiasaas.domain.ports.out;

/**
 * Outbound port for storing and resolving files under the configured upload directory.
 */
public interface FileStoragePort {

    /**
     * Ensures a subdirectory exists under the upload root.
     *
     * @param subdirectory subdirectory under the upload root
     * @return a provider-specific directory handle, if any
     */
    java.nio.file.Path ensureDirectory(String subdirectory);

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
     * Reads a previously stored file.
     *
     * @param relativePath stored relative path
     * @return the binary file content
     */
    byte[] read(String relativePath);
}
