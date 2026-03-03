package com.maitrisetcf.domain.models.query.filter;

import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Filter class for {@link UserSubscriptionStatus} enum attributes.
 */
@NoArgsConstructor
public class UserSubscriptionStatusFilter extends EnumFilter<UserSubscriptionStatus> {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserSubscriptionStatusFilter(UserSubscriptionStatusFilter filter) {
        super(filter);
    }

    @Override
    public UserSubscriptionStatusFilter copy() {
        return new UserSubscriptionStatusFilter(this);
    }
}
