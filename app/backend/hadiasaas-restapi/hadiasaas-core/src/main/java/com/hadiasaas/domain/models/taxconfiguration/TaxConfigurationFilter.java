package com.hadiasaas.domain.models.taxconfiguration;

import com.hadiasaas.domain.models.query.AuditableFilter;
import com.hadiasaas.domain.models.query.filter.BooleanFilter;
import com.hadiasaas.domain.models.query.filter.LongFilter;
import com.hadiasaas.domain.models.query.filter.StringFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Filter criteria for querying {@link TaxConfiguration} entries.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class TaxConfigurationFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private StringFilter code;
    private StringFilter name;
    private BooleanFilter active;

    public TaxConfigurationFilter(TaxConfigurationFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public TaxConfigurationFilter copy() {
        return new TaxConfigurationFilter(this);
    }
}
