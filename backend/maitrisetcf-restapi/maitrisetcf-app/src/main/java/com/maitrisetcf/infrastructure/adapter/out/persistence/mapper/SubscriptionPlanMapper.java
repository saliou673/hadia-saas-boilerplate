package com.maitrisetcf.infrastructure.adapter.out.persistence.mapper;

import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

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
                entity.getPrice(),
                entity.getCurrencyCode(),
                entity.getFeatures(),
                entity.getDurationDays(),
                entity.isActive(),
                entity.getType(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
