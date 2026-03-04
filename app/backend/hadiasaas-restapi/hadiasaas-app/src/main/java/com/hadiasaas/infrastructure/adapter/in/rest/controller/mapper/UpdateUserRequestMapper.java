package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.user.UserInfoUpdate;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting an {@link com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateUserRequest} to {@link com.hadiasaas.domain.models.user.UserInfoUpdate}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UpdateUserRequestMapper {
    UserInfoUpdate toDomain(UpdateUserRequest request);
}
