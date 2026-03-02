package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Permission")
public record PermissionDTO(String code, String description) {}
