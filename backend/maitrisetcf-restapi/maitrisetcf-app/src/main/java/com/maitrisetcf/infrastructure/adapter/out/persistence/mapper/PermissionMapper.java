package com.maitrisetcf.infrastructure.adapter.out.persistence.mapper;

import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper between {@link com.maitrisetcf.infrastructure.adapter.out.persistence.entity.PermissionEntity} and {@link com.maitrisetcf.domain.models.rbac.Permission}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PermissionMapper {

    Permission toDomain(PermissionEntity entity);

    PermissionEntity toEntity(Permission domain);

    List<Permission> toDomain(List<PermissionEntity> entities);

    Set<Permission> toDomain(Set<PermissionEntity> entities);
}
