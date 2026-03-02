package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Schema(name = "RoleGroup")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RoleGroupDTO extends AuditableDTO {

    private Long id;
    private String name;
    private String description;
    private List<PermissionDTO> permissions;

    public RoleGroupDTO(
            Long id,
            String name,
            String description,
            List<PermissionDTO> permissions,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }
}
