package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateAppConfigurationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper extracting fields from a {@link com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateAppConfigurationRequest} for use in service calls.
 */
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
