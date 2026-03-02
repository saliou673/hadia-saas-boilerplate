package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorSetupConfirmRequest(
        @NotBlank String code
) {}
