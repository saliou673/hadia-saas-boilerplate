package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.models.user.UserFilter;

public interface UserQueryUseCase {

    PagedResult<User> findAll(UserFilter filter, int page, int size);

    long count(UserFilter filter);
}
