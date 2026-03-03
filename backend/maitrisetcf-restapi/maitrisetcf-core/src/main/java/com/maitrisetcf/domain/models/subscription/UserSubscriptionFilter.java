package com.maitrisetcf.domain.models.subscription;

import com.maitrisetcf.domain.models.query.AuditableFilter;
import com.maitrisetcf.domain.models.query.filter.LongFilter;
import com.maitrisetcf.domain.models.query.filter.StringFilter;
import com.maitrisetcf.domain.models.query.filter.UserSubscriptionStatusFilter;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Filter criteria for querying {@link UserSubscription} entities.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public final class UserSubscriptionFilter extends AuditableFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;
    private LongFilter userId;
    private LongFilter planId;
    private StringFilter paymentMode;
    private UserSubscriptionStatusFilter status;

    public UserSubscriptionFilter(UserSubscriptionFilter other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.planId = other.planId == null ? null : other.planId.copy();
        this.paymentMode = other.paymentMode == null ? null : other.paymentMode.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.setCreationDate(other.getCreationDate() == null ? null : other.getCreationDate().copy());
        this.setLastUpdateDate(other.getLastUpdateDate() == null ? null : other.getLastUpdateDate().copy());
        this.setLastUpdatedBy(other.getLastUpdatedBy() == null ? null : other.getLastUpdatedBy().copy());
    }

    public UserSubscriptionFilter copy() {
        return new UserSubscriptionFilter(this);
    }
}
