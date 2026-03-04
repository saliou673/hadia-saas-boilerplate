package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(name = "Auditable")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/** Base DTO carrying audit metadata for all response objects. */
public abstract class AuditableDTO {
    /**
     * Timestamp when the entity was first created.
     */
    private Instant creationDate;
    /**
     * Timestamp of the last modification.
     */
    private Instant lastUpdateDate;
    /**
     * Email of the user who last modified the entity.
     */
    private String lastUpdatedBy;
}
