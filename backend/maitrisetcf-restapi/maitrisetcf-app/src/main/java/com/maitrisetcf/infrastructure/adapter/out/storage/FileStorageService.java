package com.maitrisetcf.infrastructure.adapter.out.storage;

import com.maitrisetcf.domain.exceptions.TechnicalException;
import com.maitrisetcf.domain.ports.out.FileStoragePort;
import com.maitrisetcf.domain.ports.out.StorageStrategyResolverPort;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Delegates file operations to the storage strategy selected in app configuration.
 */
@Service
public class FileStorageService implements FileStoragePort {

    private final StorageStrategyResolverPort storageStrategyResolverPort;
    private final Map<String, StorageStrategy> strategiesByCode;

    public FileStorageService(StorageStrategyResolverPort storageStrategyResolverPort, List<StorageStrategy> storageStrategies) {
        this.storageStrategyResolverPort = storageStrategyResolverPort;
        this.strategiesByCode = storageStrategies.stream()
                .collect(Collectors.toMap(StorageStrategy::code, Function.identity()));
    }

    @Override
    public Path ensureDirectory(String subdirectory) {
        return resolveStrategy().ensureDirectory(subdirectory);
    }

    @Override
    public String store(String subdirectory, String filename, byte[] content) {
        return resolveStrategy().store(subdirectory, filename, content);
    }

    @Override
    public byte[] read(String relativePath) {
        return resolveStrategy().read(relativePath);
    }

    private StorageStrategy resolveStrategy() {
        return getStrategy(storageStrategyResolverPort.resolveStorageStrategyCode());
    }

    private StorageStrategy getStrategy(String strategyCode) {
        StorageStrategy strategy = strategiesByCode.get(strategyCode);
        if (strategy == null) {
            throw new TechnicalException("Unsupported storage strategy: " + strategyCode);
        }
        return strategy;
    }
}
