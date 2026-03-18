package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.EnterpriseProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link EnterpriseProfileEntity} and {@link EnterpriseProfile}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EnterpriseProfileMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    EnterpriseProfileEntity toEntity(EnterpriseProfile enterpriseProfile);

    default EnterpriseProfile toDomain(EnterpriseProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return EnterpriseProfile.rehydrate(
                entity.getId(),
                entity.getCompanyName(),
                entity.getLegalForm(),
                entity.getRegistrationNumber(),
                entity.getVatNumber(),
                entity.getAddressLine1(),
                entity.getAddressLine2(),
                entity.getCity(),
                entity.getPostalCode(),
                entity.getCountryCode(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getWebsite(),
                entity.getLogoUrl(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
