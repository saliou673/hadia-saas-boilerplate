package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper between {@link com.hadiasaas.infrastructure.adapter.out.persistence.entity.PermissionEntity} and {@link com.hadiasaas.domain.models.rbac.Permission}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PermissionMapper {

    Permission toDomain(PermissionEntity entity);

    PermissionEntity toEntity(Permission domain);

    List<Permission> toDomain(List<PermissionEntity> entities);

    Set<Permission> toDomain(Set<PermissionEntity> entities);
}
