package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import com.hadiasaas.domain.enumerations.DiscountType;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Response DTO representing a discount code.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class DiscountCodeDTO extends AuditableDTO {

    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    @Nullable
    private String currencyCode;
    private boolean active;
    @Nullable
    private LocalDate expirationDate;
    @Nullable
    private Integer maxUsages;
    private int usageCount;

    public DiscountCodeDTO(
            Long id,
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            @Nullable String currencyCode,
            boolean active,
            @Nullable LocalDate expirationDate,
            @Nullable Integer maxUsages,
            int usageCount,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.currencyCode = currencyCode;
        this.active = active;
        this.expirationDate = expirationDate;
        this.maxUsages = maxUsages;
        this.usageCount = usageCount;
    }
}
