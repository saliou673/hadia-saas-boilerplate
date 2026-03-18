package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.StorageSettingsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link StorageSettingsEntity} and {@link StorageSettings}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StorageSettingsMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    StorageSettingsEntity toEntity(StorageSettings storageSettings);

    default StorageSettings toDomain(StorageSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        return StorageSettings.rehydrate(
                entity.getId(),
                entity.getProvider(),
                entity.getBucketName(),
                entity.getRegion(),
                entity.getEndpoint(),
                entity.isActive(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
