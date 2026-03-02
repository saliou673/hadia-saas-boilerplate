package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.appconfiguration.AppConfiguration;
import com.maitrisetcf.domain.models.appconfiguration.AppConfigurationFilter;
import com.maitrisetcf.domain.models.query.PagedResult;

public interface AppConfigurationQueryUseCase {

    PagedResult<AppConfiguration> findAll(AppConfigurationFilter filter, int page, int size);

    long count(AppConfigurationFilter filter);
}
