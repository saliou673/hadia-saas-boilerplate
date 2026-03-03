package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.models.user.Email;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.UserDetailsDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.UserSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper converting {@link com.maitrisetcf.domain.models.user.User} to REST DTOs.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserDtoMapper {
    @Mapping(target = "email", source = "userCredentials.email")
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "phoneNumber", source = "userInfo.phoneNumber")
    @Mapping(target = "birthDate", source = "userInfo.birthDate")
    @Mapping(target = "gender", source = "userInfo.gender")
    @Mapping(target = "address", source = "userInfo.address")
    @Mapping(target = "languageKey", source = "userInfo.languageKey")
    @Mapping(target = "imageUrl", source = "userInfo.imageUrl")
    @Mapping(target = "permissions", expression = "java(mapPermissions(user.resolvePermissions()))")
    UserSummaryDTO toSummaryDTO(User user);

    @Mapping(target = "email", source = "userCredentials.email")
    @Mapping(target = "firstName", source = "userInfo.firstName")
    @Mapping(target = "lastName", source = "userInfo.lastName")
    @Mapping(target = "phoneNumber", source = "userInfo.phoneNumber")
    @Mapping(target = "birthDate", source = "userInfo.birthDate")
    @Mapping(target = "gender", source = "userInfo.gender")
    @Mapping(target = "address", source = "userInfo.address")
    @Mapping(target = "languageKey", source = "userInfo.languageKey")
    @Mapping(target = "imageUrl", source = "userInfo.imageUrl")
    @Mapping(target = "permissions", expression = "java(mapPermissions(user.resolvePermissions()))")
    UserDetailsDTO toDetailsDTO(User user);

    default String map(Email email) {
        return email == null ? null : email.value();
    }

    default List<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return List.of();
        }
        return permissions.stream()
                .map(Permission::code)
                .sorted(Comparator.naturalOrder())
                .toList();
    }

}
