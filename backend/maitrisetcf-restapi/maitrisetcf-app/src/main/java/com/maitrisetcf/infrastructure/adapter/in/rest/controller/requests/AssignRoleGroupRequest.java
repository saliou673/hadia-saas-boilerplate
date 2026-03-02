package com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests;

import jakarta.validation.constraints.NotNull;

public record AssignRoleGroupRequest(@NotNull Long roleGroupId) {}
