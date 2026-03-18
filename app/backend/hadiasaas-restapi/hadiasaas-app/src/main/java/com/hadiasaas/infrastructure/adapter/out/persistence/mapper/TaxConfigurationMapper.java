package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TaxConfigurationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link TaxConfigurationEntity} and {@link TaxConfiguration}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TaxConfigurationMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    TaxConfigurationEntity toEntity(TaxConfiguration taxConfiguration);

    default TaxConfiguration toDomain(TaxConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        return TaxConfiguration.rehydrate(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getRate(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
