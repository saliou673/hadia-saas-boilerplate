package com.maitrisetcf.domain.models.discountcode;

import com.maitrisetcf.domain.enumerations.DiscountType;
import com.maitrisetcf.domain.exceptions.InvalidDiscountCodeException;
import com.maitrisetcf.domain.models.Auditable;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Domain entity representing a discount code.
 */
@Getter
public class DiscountCode extends Auditable<Long> {

    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private String currencyCode;
    private boolean active;
    private LocalDate expirationDate;
    private Integer maxUsages;
    private int usageCount;

    private DiscountCode(
            Long id,
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages,
            int usageCount,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.currencyCode = currencyCode;
        this.active = active;
        this.expirationDate = expirationDate;
        this.maxUsages = maxUsages;
        this.usageCount = usageCount;
    }

    public static DiscountCode create(
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages
    ) {
        return new DiscountCode(null, code, discountType, discountValue, currencyCode, active, expirationDate, maxUsages, 0,
                                null, null, null);
    }

    public static DiscountCode rehydrate(
            Long id,
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages,
            int usageCount,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new DiscountCode(id, code, discountType, discountValue, currencyCode, active, expirationDate, maxUsages, usageCount,
                                creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages
    ) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.currencyCode = currencyCode;
        this.active = active;
        this.expirationDate = expirationDate;
        this.maxUsages = maxUsages;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public void validateForUse(LocalDate currentDate) {
        if (!active) {
            throw new InvalidDiscountCodeException("Discount code is inactive");
        }
        if (expirationDate != null && expirationDate.isBefore(currentDate)) {
            throw new InvalidDiscountCodeException("Discount code is expired");
        }
        if (maxUsages != null && usageCount >= maxUsages) {
            throw new InvalidDiscountCodeException("Discount code usage limit reached");
        }
    }
}
