package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Permission")
/**
 * Response DTO representing a permission.
 *
 * @param code        unique permission code (e.g. {@code "user:read"})
 * @param description human-readable description of the permission
 */
public record PermissionDTO(String code, String description) {}
