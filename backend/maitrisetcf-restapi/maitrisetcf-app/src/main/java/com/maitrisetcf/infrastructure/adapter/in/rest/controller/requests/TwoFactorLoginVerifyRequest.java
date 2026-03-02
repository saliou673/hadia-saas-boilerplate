package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorLoginVerifyRequest(
        @NotBlank String challengeId,
        @NotBlank String code
) {}
