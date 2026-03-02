package com.maitrisetcf.domain.models.query.filter;

import com.maitrisetcf.domain.enumerations.UserStatus;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class UserStatusFilter extends EnumFilter<UserStatus> {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserStatusFilter(UserStatusFilter filter) {
        super(filter);
    }

    @Override
    public UserStatusFilter copy() {
        return new UserStatusFilter(this);
    }
}