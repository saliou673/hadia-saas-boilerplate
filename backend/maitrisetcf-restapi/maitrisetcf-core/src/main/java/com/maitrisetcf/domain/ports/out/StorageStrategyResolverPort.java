package com.maitrisetcf.domain.ports.out;

/**
 * Resolves the currently active file storage strategy.
 */
public interface StorageStrategyResolverPort {

    /**
     * @return the active storage strategy code
     */
    String resolveStorageStrategyCode();
}
