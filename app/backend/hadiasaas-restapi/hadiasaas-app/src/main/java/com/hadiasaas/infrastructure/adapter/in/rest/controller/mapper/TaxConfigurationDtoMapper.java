package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.TaxConfigurationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting {@link TaxConfiguration} to {@link TaxConfigurationDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TaxConfigurationDtoMapper {

    TaxConfigurationDTO toDTO(TaxConfiguration taxConfiguration);
}
