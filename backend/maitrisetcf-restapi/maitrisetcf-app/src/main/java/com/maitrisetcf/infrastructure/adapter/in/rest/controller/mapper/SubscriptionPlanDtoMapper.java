package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting {@link SubscriptionPlan} to {@link SubscriptionPlanDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SubscriptionPlanDtoMapper {

    default SubscriptionPlanDTO toDTO(SubscriptionPlan plan) {
        if (plan == null) {
            return null;
        }
        return new SubscriptionPlanDTO(
                plan.getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getMonthlyPrice(),
                plan.getYearlyPrice(),
                plan.getLifetimePrice(),
                plan.getPrice(),
                plan.getDurationDays(),
                plan.getCurrencyCode(),
                plan.getFeatures(),
                plan.isActive(),
                plan.getType(),
                plan.getCreationDate(),
                plan.getLastUpdateDate(),
                plan.getLastUpdatedBy()
        );
    }
}
