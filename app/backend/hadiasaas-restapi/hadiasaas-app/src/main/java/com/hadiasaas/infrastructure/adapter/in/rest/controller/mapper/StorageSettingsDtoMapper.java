package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.StorageSettingsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper converting {@link StorageSettings} to {@link StorageSettingsDTO}.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StorageSettingsDtoMapper {

    StorageSettingsDTO toDTO(StorageSettings storageSettings);
}
