package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import com.maitrisetcf.domain.constants.DomainConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "RecoverAccountRequest")
public record RecoverAccountRequest(
        @NotBlank(message = "Email is required")
        @Pattern(regexp = DomainConstants.EMAIL_REGEX_PATTERN, message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
