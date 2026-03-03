package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting {@link com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan} to {@link com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SubscriptionPlanDtoMapper {

    SubscriptionPlanDTO toDTO(SubscriptionPlan plan);
}
