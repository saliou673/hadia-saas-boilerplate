package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import com.maitrisetcf.domain.models.auth.TwoFactorMethodType;

/**
 * Returned when a login attempt requires 2FA completion.
 */
public record TwoFactorChallengeResponse(
        String challengeId,
        TwoFactorMethodType type
) {}
