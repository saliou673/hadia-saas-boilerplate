package com.maitrisetcf.domain.models.query;

import com.maitrisetcf.domain.models.query.filter.InstantFilter;
import com.maitrisetcf.domain.models.query.filter.StringFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private InstantFilter creationDate;
    private InstantFilter lastUpdateDate;
    private StringFilter lastUpdatedBy;
}
