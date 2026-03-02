package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.rbac.RoleGroup;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.RoleGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = PermissionDtoMapper.class
)
public interface RoleGroupDtoMapper {

    RoleGroupDTO toDTO(RoleGroup roleGroup);

    List<RoleGroupDTO> toDTO(List<RoleGroup> roleGroups);
}
