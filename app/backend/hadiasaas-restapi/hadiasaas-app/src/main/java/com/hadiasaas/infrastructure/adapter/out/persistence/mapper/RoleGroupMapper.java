package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.domain.models.rbac.RoleGroup;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.RoleGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = PermissionMapper.class
)
/** MapStruct mapper between {@link com.hadiasaas.infrastructure.adapter.out.persistence.entity.RoleGroupEntity} and {@link com.hadiasaas.domain.models.rbac.RoleGroup}. */
public interface RoleGroupMapper {

    default RoleGroup toDomain(RoleGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        return RoleGroup.rehydrate(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                permissionsToDomain(entity.getPermissions()),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }

    // Distinct name avoids Set<X>/Set<Y> erasure clash
    Set<Permission> permissionsToDomain(Set<PermissionEntity> entities);

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    RoleGroupEntity toEntity(RoleGroup domain);

    List<RoleGroup> toDomain(List<RoleGroupEntity> entities);

    Set<RoleGroup> toDomain(Set<RoleGroupEntity> entities);
}
