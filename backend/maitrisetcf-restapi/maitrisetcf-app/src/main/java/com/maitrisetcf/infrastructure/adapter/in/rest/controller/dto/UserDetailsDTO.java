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
    /**
     * Unique identifier of the user.
     */
    private Long id;

    @Pattern(regexp = EMAIL_REGEX_PATTERN)
    @NotBlank
    /** Validated email address. */
    private String email;

    @Nullable
    /** Optional phone number. */
    private String phoneNumber;

    @NotBlank
    /** Given name. */
    private String firstName;

    @NotBlank
    /** Family name. */
    private String lastName;

    @NotNull
    /** Date of birth. */
    private LocalDate birthDate;

    @NotNull
    /** Biological gender. */
    private UserGender gender;

    @Nullable
    /** Optional postal address. */
    private String address;

    @Nullable
    /** Current account lifecycle status. */
    private UserStatus status;

    /**
     * Preferred locale key (e.g. {@code "fr"}).
     */
    private String languageKey;

    @Nullable
    /** Profile picture URL. */
    private String imageUrl;

    @NotNull
    /** Flat list of permission codes resolved from the user's role groups. */
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
