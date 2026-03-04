package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.discountcode.DiscountCode;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link DiscountCodeEntity} and {@link DiscountCode}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DiscountCodeMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    DiscountCodeEntity toEntity(DiscountCode discountCode);

    default DiscountCode toDomain(DiscountCodeEntity entity) {
        if (entity == null) {
            return null;
        }
        return DiscountCode.rehydrate(
                entity.getId(),
                entity.getCode(),
                entity.getDiscountType(),
                entity.getDiscountValue(),
                entity.getCurrencyCode(),
                entity.isActive(),
                entity.getExpirationDate(),
                entity.getMaxUsages(),
                entity.getUsageCount(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
