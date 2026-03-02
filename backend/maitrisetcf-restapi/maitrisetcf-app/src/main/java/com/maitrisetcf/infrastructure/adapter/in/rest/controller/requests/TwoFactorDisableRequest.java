package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorDisableRequest(
        @NotBlank String currentPassword
) {}
