package com.maitrisetcf.domain.models.appconfiguration;

import com.maitrisetcf.domain.models.query.AuditableFilter;
import com.maitrisetcf.domain.models.query.filter.AppConfigurationCategoryFilter;
import com.maitrisetcf.domain.models.query.filter.BooleanFilter;
import com.maitrisetcf.domain.models.query.filter.LongFilter;
import com.maitrisetcf.domain.models.query.filter.StringFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class AppConfigurationFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private AppConfigurationCategoryFilter category;
    private StringFilter code;
    private StringFilter label;
    private BooleanFilter active;

    public AppConfigurationFilter(AppConfigurationFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.category = other.category == null ? null : other.category.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.label = other.label == null ? null : other.label.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public AppConfigurationFilter copy() {
        return new AppConfigurationFilter(this);
    }
}
