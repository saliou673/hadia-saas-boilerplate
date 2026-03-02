package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.user.UserInfoUpdate;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UpdateUserRequestMapper {
    UserInfoUpdate toDomain(UpdateUserRequest request);
}
