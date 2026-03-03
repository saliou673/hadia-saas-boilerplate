package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UpdateUserRequest")
/**
 * Request to update the authenticated user's profile information.
 *
 * @param firstName   new given name
 * @param lastName    new family name
 * @param phoneNumber optional phone number
 * @param address     optional postal address
 * @param languageKey optional preferred locale key
 * @param imageUrl    optional profile picture URL
 */
public record UpdateUserRequest(

        @NotBlank(message = "firstName must not be blank")
        String firstName,

        @NotBlank(message = "lastName must not be blank")
        String lastName,

        @Nullable
        String phoneNumber,

        @Nullable
        String address,

        @Nullable
        String languageKey,

        @Nullable
        String imageUrl
) {
}