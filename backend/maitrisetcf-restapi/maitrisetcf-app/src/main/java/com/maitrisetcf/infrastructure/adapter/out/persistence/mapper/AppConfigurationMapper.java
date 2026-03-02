package com.maitrisetcf.infrastructure.adapter.out.persistence.mapper;

import com.maitrisetcf.domain.models.appconfiguration.AppConfiguration;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AppConfigurationMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    AppConfigurationEntity toEntity(AppConfiguration appConfiguration);

    default AppConfiguration toDomain(AppConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        return AppConfiguration.rehydrate(
                entity.getId(),
                entity.getCategory(),
                entity.getCode(),
                entity.getLabel(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
