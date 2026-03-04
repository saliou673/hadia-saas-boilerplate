package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import com.hadiasaas.domain.enumerations.UserGender;
import com.hadiasaas.domain.enumerations.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

import static com.hadiasaas.domain.constants.DomainConstants.EMAIL_REGEX_PATTERN;

/**
 * Represents a user, with his resolved permissions.
 */
@Schema(name = "UserSummary")
public record UserSummaryDTO(
        Long id,

        @Pattern(regexp = EMAIL_REGEX_PATTERN)
        @NotBlank
        String email,

        @Nullable
        String phoneNumber,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
        LocalDate birthDate,

        @NotNull
        UserGender gender,

        @Nullable
        String address,

        @Nullable
        UserStatus status,

        String languageKey,

        @Nullable
        String imageUrl,

        @NotNull
        List<String> permissions) {

}
