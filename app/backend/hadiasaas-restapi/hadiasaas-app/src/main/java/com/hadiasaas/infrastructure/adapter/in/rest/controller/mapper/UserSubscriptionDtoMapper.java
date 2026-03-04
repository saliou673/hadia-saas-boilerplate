package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.subscription.UserSubscription;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.UserSubscriptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from {@link UserSubscription} domain to {@link UserSubscriptionDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserSubscriptionDtoMapper {

    default UserSubscriptionDTO toDTO(UserSubscription subscription) {
        if (subscription == null) {
            return null;
        }
        return new UserSubscriptionDTO(
                subscription.getId(),
                subscription.getUserId(),
                subscription.getPlanId(),
                subscription.getPlanTitle(),
                subscription.getPricePaid(),
                subscription.getDiscountCodeUsed(),
                subscription.getDiscountAmount(),
                subscription.getTaxRate(),
                subscription.getTaxAmount(),
                subscription.getCurrencyCode(),
                subscription.getBillingFrequency(),
                subscription.getPaymentMode(),
                subscription.getExternalPaymentId(),
                subscription.getStatus(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.isAutoRenew(),
                subscription.getCreationDate(),
                subscription.getLastUpdateDate(),
                subscription.getLastUpdatedBy()
        );
    }
}
