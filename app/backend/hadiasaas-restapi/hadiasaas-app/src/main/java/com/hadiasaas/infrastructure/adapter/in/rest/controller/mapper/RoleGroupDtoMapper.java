package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.rbac.RoleGroup;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.RoleGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper converting {@link com.hadiasaas.domain.models.rbac.RoleGroup} to {@link com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.RoleGroupDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = PermissionDtoMapper.class
)
public interface RoleGroupDtoMapper {

    RoleGroupDTO toDTO(RoleGroup roleGroup);

    List<RoleGroupDTO> toDTO(List<RoleGroup> roleGroups);
}
