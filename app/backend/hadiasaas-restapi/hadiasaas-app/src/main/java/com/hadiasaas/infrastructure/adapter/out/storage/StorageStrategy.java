package com.hadiasaas.infrastructure.adapter.out.storage;

import java.nio.file.Path;

interface StorageStrategy {

    String LOCAL_CODE = "LOCAL";
    String AWS_CODE = "AWS";

    String code();

    Path ensureDirectory(String subdirectory);

    String store(String subdirectory, String filename, byte[] content);

    byte[] read(String relativePath);
}
