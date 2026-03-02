package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UpdateUserRequest")
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