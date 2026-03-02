package com.maitrisetcf.domain.models.query.filter;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class AppConfigurationCategoryFilter extends EnumFilter<AppConfigurationCategory> {

    @Serial
    private static final long serialVersionUID = 1L;

    public AppConfigurationCategoryFilter(AppConfigurationCategoryFilter filter) {
        super(filter);
    }

    @Override
    public AppConfigurationCategoryFilter copy() {
        return new AppConfigurationCategoryFilter(this);
    }
}
