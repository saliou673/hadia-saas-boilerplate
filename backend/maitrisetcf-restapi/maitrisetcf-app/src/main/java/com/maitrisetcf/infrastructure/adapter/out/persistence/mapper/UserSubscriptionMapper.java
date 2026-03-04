package com.maitrisetcf.infrastructure.adapter.out.persistence.mapper;

import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between {@link UserSubscriptionEntity} and {@link UserSubscription}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserSubscriptionMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    UserSubscriptionEntity toEntity(UserSubscription subscription);

    default UserSubscription toDomain(UserSubscriptionEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserSubscription.rehydrate(
                entity.getId(),
                entity.getUserId(),
                entity.getPlanId(),
                entity.getPlanTitle(),
                entity.getPricePaid(),
                entity.getDiscountCodeUsed(),
                entity.getDiscountAmount(),
                entity.getCurrencyCode(),
                entity.getBillingFrequency(),
                entity.getPaymentMode(),
                entity.getExternalPaymentId(),
                entity.getStatus(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isAutoRenew(),
                entity.getCreationDate(),
                entity.getLastUpdateDate(),
                entity.getLastUpdatedBy()
        );
    }
}
