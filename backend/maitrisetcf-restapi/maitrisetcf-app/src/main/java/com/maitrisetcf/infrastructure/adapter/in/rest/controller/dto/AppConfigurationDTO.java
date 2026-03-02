package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(name = "AppConfiguration")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class AppConfigurationDTO extends AuditableDTO {

    private Long id;
    private AppConfigurationCategory category;
    private String code;
    private String label;
    private String description;
    private boolean active;

    public AppConfigurationDTO(
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
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.category = category;
        this.code = code;
        this.label = label;
        this.description = description;
        this.active = active;
    }
}
