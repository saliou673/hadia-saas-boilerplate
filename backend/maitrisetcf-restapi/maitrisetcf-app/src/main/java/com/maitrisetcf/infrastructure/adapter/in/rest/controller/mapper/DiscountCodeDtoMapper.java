package com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper;

import com.maitrisetcf.domain.models.discountcode.DiscountCode;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.DiscountCodeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from {@link DiscountCode} to {@link DiscountCodeDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DiscountCodeDtoMapper {

    default DiscountCodeDTO toDTO(DiscountCode discountCode) {
        if (discountCode == null) {
            return null;
        }
        return new DiscountCodeDTO(
                discountCode.getId(),
                discountCode.getCode(),
                discountCode.getDiscountType(),
                discountCode.getDiscountValue(),
                discountCode.getCurrencyCode(),
                discountCode.isActive(),
                discountCode.getExpirationDate(),
                discountCode.getMaxUsages(),
                discountCode.getUsageCount(),
                discountCode.getCreationDate(),
                discountCode.getLastUpdateDate(),
                discountCode.getLastUpdatedBy()
        );
    }
}
