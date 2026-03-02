package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import com.maitrisetcf.domain.models.auth.TwoFactorMethodType;
import jakarta.validation.constraints.NotNull;

public record TwoFactorSetupRequest(
        @NotNull TwoFactorMethodType type
) {}
