package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateRoleGroupRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotEmpty Set<String> permissionCodes
) {}
