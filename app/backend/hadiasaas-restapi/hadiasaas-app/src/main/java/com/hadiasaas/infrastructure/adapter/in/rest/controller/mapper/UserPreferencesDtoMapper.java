package com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper;

import com.hadiasaas.domain.models.userpreference.AppearancePreferences;
import com.hadiasaas.domain.models.userpreference.UserPreferences;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.AppearancePreferencesDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.UserPreferencesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between preference domain models and REST DTOs.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserPreferencesDtoMapper {

    default UserPreferencesDTO toDTO(UserPreferences preferences) {
        if (preferences == null) {
            return null;
        }
        return new UserPreferencesDTO(toDTO(preferences.appearance()));
    }

    default UserPreferences toDomain(UserPreferencesDTO preferences) {
        if (preferences == null) {
            return null;
        }
        return UserPreferences.of(toDomain(preferences.appearance()));
    }

    default AppearancePreferencesDTO toDTO(AppearancePreferences appearance) {
        if (appearance == null) {
            return null;
        }
        return new AppearancePreferencesDTO(appearance.getTheme(), appearance.getFont());
    }

    default AppearancePreferences toDomain(AppearancePreferencesDTO appearance) {
        if (appearance == null) {
            return null;
        }
        return AppearancePreferences.of(appearance.theme(), appearance.font());
    }
}
