package com.hadiasaas.domain.models.storagesettings;

import com.hadiasaas.domain.models.query.AuditableFilter;
import com.hadiasaas.domain.models.query.filter.BooleanFilter;
import com.hadiasaas.domain.models.query.filter.LongFilter;
import com.hadiasaas.domain.models.query.filter.StorageProviderFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Filter criteria for querying {@link StorageSettings} entries.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class StorageSettingsFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StorageProviderFilter provider;
    private BooleanFilter active;

    public StorageSettingsFilter(StorageSettingsFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.provider = other.provider == null ? null : other.provider.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public StorageSettingsFilter copy() {
        return new StorageSettingsFilter(this);
    }
}
