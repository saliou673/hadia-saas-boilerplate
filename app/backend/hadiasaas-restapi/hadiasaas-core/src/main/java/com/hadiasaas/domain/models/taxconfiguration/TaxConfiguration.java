package com.hadiasaas.domain.models.taxconfiguration;

import com.hadiasaas.domain.models.Auditable;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain entity representing a tax configuration entry (e.g. 20% VAT).
 */
@Getter
public class TaxConfiguration extends Auditable<Long> {

    /** Short unique code (e.g. {@code "VAT_20"}). */
    private String code;
    /** Human-readable name (e.g. "Standard VAT"). */
    private String name;
    /** The tax rate as a decimal fraction (e.g. 0.20 for 20%). */
    private BigDecimal rate;
    /** Optional description. */
    private String description;
    /** Whether this entry is currently enabled. */
    private boolean active;

    private TaxConfiguration(
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
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.active = active;
    }

    public static TaxConfiguration create(String code, String name, BigDecimal rate, String description) {
        return new TaxConfiguration(null, code, name, rate, description, true, null, null, null);
    }

    public static TaxConfiguration rehydrate(
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
        return new TaxConfiguration(id, code, name, rate, description, active, creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(String code, String name, BigDecimal rate, String description, boolean active) {
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.description = description;
        this.active = active;
    }
}
