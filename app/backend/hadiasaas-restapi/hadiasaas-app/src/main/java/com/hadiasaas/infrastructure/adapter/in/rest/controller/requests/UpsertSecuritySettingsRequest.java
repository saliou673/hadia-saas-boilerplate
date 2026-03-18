package com.hadiasaas.infrastructure.adapter.in.rest.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpsertSecuritySettingsRequest")
public record UpsertSecuritySettingsRequest(
        boolean twoFactorRequired
) {
}
