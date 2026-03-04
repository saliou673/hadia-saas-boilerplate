package com.hadiasaas.domain.models.query;

import com.hadiasaas.domain.models.query.filter.InstantFilter;
import com.hadiasaas.domain.models.query.filter.StringFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Base filter exposing audit-field criteria shared by all entity filters.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Filter on the creation timestamp.
     */
    private InstantFilter creationDate;
    /**
     * Filter on the last-modification timestamp.
     */
    private InstantFilter lastUpdateDate;
    /**
     * Filter on the email of the last modifier.
     */
    private StringFilter lastUpdatedBy;
}
