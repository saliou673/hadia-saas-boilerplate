package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper converting {@link com.hadiasaas.domain.models.rbac.Permission} to {@link com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.PermissionDTO}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PermissionDtoMapper {

    PermissionDTO toDTO(Permission permission);

    List<PermissionDTO> toDTO(List<Permission> permissions);
}
