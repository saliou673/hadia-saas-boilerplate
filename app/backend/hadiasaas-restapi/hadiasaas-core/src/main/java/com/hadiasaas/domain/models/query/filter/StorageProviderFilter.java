package com.hadiasaas.domain.models.query.filter;

import com.hadiasaas.domain.enumerations.StorageProvider;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Filter class for {@link StorageProvider} enum attributes.
 */
@NoArgsConstructor
public class StorageProviderFilter extends EnumFilter<StorageProvider> {

    @Serial
    private static final long serialVersionUID = 1L;

    public StorageProviderFilter(StorageProviderFilter filter) {
        super(filter);
    }

    @Override
    public StorageProviderFilter copy() {
        return new StorageProviderFilter(this);
    }
}
