package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.EnterpriseProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting {@link EnterpriseProfile} to {@link EnterpriseProfileDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EnterpriseProfileDtoMapper {

    EnterpriseProfileDTO toDTO(EnterpriseProfile enterpriseProfile);
}
