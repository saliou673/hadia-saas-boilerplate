package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.CreateAppConfigurationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CreateAppConfigurationRequestMapper {

    default AppConfigurationCategory toCategory(CreateAppConfigurationRequest request) {
        return request.category();
    }

    default String toCode(CreateAppConfigurationRequest request) {
        return request.code();
    }

    default String toLabel(CreateAppConfigurationRequest request) {
        return request.label();
    }

    default String toDescription(CreateAppConfigurationRequest request) {
        return request.description();
    }
}
