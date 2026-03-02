package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.models.user.UserCredentials;
import com.maitrisetcf.domain.models.user.UserInfo;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.CreateAdminUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CreateAdminUserRequestMapper {

    default User toDomain(CreateAdminUserRequest request) {
        if (request == null) {
            return null;
        }
        return User.create(toUserInfo(request), toUserCredentials(request));
    }

    default Set<String> toRoleGroupNames(CreateAdminUserRequest request) {
        return request.roleGroupNames();
    }

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "languageKey", source = "languageKey")
    @Mapping(target = "imageUrl", source = "imageUrl")
    UserInfo toUserInfo(CreateAdminUserRequest request);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "passwordHash", constant = "!MANAGED_NO_PASSWORD")
    @Mapping(target = "resetCode", ignore = true)
    @Mapping(target = "resetDate", ignore = true)
    @Mapping(target = "activationCode", ignore = true)
    @Mapping(target = "activationDate", ignore = true)
    UserCredentials toUserCredentials(CreateAdminUserRequest request);
}
