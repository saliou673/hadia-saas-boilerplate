package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(name = "TaxConfiguration")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
/** Response DTO representing a tax configuration entry. */
public class TaxConfigurationDTO extends AuditableDTO {

    private Long id;
    private String code;
    private String name;
    private BigDecimal rate;
    private String description;
    private boolean active;

    public TaxConfigurationDTO(
            Long id,
            String code,
            String name,
            BigDecimal rate,
            String description,
            boolean active,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.active = active;
    }
}
