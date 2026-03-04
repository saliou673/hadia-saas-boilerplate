package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import com.hadiasaas.domain.models.auth.TwoFactorMethodType;
import jakarta.validation.constraints.NotNull;

/**
 * Request to initiate a 2FA setup flow.
 *
 * @param type the 2FA method to configure
 */
public record TwoFactorSetupRequest(
        @NotNull TwoFactorMethodType type
) {}
