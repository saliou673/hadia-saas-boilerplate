package com.hadiasaas.infrastructure.adapter.out.persistence.mapper;

import com.hadiasaas.domain.models.subscriptionplan.SubscriptionPlan;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link SubscriptionPlanEntity} and {@link SubscriptionPlan}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SubscriptionPlanMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    SubscriptionPlanEntity toEntity(SubscriptionPlan plan);

    default SubscriptionPlan toDomain(SubscriptionPlanEntity entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionPlan.rehydrate(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getMonthlyPrice(),
                entity.getYearlyPrice(),
                entity.getLifetimePrice(),
                entity.getPrice(),
                entity.getDurationDays(),
                entity.getCurrencyCode(),
                entity.getFeatures(),
                entity.isActive(),
                entity.getType(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
