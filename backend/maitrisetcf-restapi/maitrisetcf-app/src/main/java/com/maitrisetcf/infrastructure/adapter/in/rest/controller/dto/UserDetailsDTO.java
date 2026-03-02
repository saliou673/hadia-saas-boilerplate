package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import com.maitrisetcf.domain.enumerations.UserGender;
import com.maitrisetcf.domain.enumerations.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.maitrisetcf.domain.constants.DomainConstants.EMAIL_REGEX_PATTERN;

/**
 * Represents a user, with his resolved permissions.
 * <p>:warning: Should only be used by admin endpoints.
 */
@Schema(name = "UserDetails")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserDetailsDTO extends AuditableDTO {
    private Long id;

    @Pattern(regexp = EMAIL_REGEX_PATTERN)
    @NotBlank
    private String email;

    @Nullable
    private String phoneNumber;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private UserGender gender;

    @Nullable
    private String address;

    @Nullable
    private UserStatus status;

    private String languageKey;

    @Nullable
    private String imageUrl;

    @NotNull
    private List<String> permissions;

    public UserDetailsDTO(Long id, String email, String firstName, String lastName, @Nullable String phoneNumber, LocalDate birthDate, UserGender gender, @Nullable String address, @Nonnull UserStatus status, String languageKey, @Nullable String imageUrl, @NotNull List<String> permissions, Instant creationDate, Instant lastUpdateDate, String lastUpdatedBy) {
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
        this.languageKey = languageKey;
        this.imageUrl = imageUrl;
        this.permissions = permissions;
    }

}
