package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper converting {@link com.maitrisetcf.domain.models.rbac.Permission} to {@link com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.PermissionDTO}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PermissionDtoMapper {

    PermissionDTO toDTO(Permission permission);

    List<PermissionDTO> toDTO(List<Permission> permissions);
}
