package com.maitrisetcf.domain.models.appconfiguration;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.models.Auditable;
import lombok.Getter;

import java.time.Instant;

@Getter
public class AppConfiguration extends Auditable<Long> {

    private final AppConfigurationCategory category;
    private String code;
    private String label;
    private String description;
    private boolean active;

    private AppConfiguration(
            Long id,
            AppConfigurationCategory category,
            String code,
            String label,
            String description,
            boolean active,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.category = category;
        this.code = code;
        this.label = label;
        this.description = description;
        this.active = active;
    }

    public static AppConfiguration create(
            AppConfigurationCategory category,
            String code,
            String label,
            String description
    ) {
        return new AppConfiguration(null, category, code, label, description, true, null, null, null);
    }

    public static AppConfiguration rehydrate(
            Long id,
            AppConfigurationCategory category,
            String code,
            String label,
            String description,
            boolean active,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new AppConfiguration(id, category, code, label, description, active, creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(String code, String label, String description, boolean active) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.active = active;
    }
}
