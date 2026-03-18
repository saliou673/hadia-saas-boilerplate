package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.ports.in.SecuritySettingsUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.SecuritySettingsDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.SecuritySettingsDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpsertSecuritySettingsRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin security settings management.
 */
@Validated
@RestController
@Tag(name = "Admin security settings management")
@PreAuthorize("hasAuthority('config:manage')")
@RequestMapping("/api/v1/admin/security-settings")
@RequiredArgsConstructor
public class AdminSecuritySettingsController {

    private final SecuritySettingsUseCase securitySettingsUseCase;
    private final SecuritySettingsDtoMapper securitySettingsDtoMapper;

    @GetMapping
    public SecuritySettingsDTO getSecuritySettingsAsAdmin() {
        return securitySettingsDtoMapper.toDTO(securitySettingsUseCase.get());
    }

    @PutMapping
    public SecuritySettingsDTO upsertSecuritySettingsAsAdmin(@Valid @RequestBody UpsertSecuritySettingsRequest request) {
        return securitySettingsDtoMapper.toDTO(
                securitySettingsUseCase.upsert(request.twoFactorRequired())
        );
    }
}
