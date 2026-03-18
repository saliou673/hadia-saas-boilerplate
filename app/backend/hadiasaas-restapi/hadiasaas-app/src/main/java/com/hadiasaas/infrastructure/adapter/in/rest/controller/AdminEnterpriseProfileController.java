package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.ports.in.EnterpriseProfileUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.EnterpriseProfileDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.EnterpriseProfileDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpsertEnterpriseProfileRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin enterprise profile management.
 */
@Validated
@RestController
@Tag(name = "Admin enterprise profile management")
@PreAuthorize("hasAuthority('config:manage')")
@RequestMapping("/api/v1/admin/enterprise-profile")
@RequiredArgsConstructor
public class AdminEnterpriseProfileController {

    private final EnterpriseProfileUseCase enterpriseProfileUseCase;
    private final EnterpriseProfileDtoMapper enterpriseProfileDtoMapper;

    @GetMapping
    public EnterpriseProfileDTO getEnterpriseProfileAsAdmin() {
        return enterpriseProfileDtoMapper.toDTO(enterpriseProfileUseCase.get());
    }

    @PutMapping
    public EnterpriseProfileDTO upsertEnterpriseProfileAsAdmin(@Valid @RequestBody UpsertEnterpriseProfileRequest request) {
        return enterpriseProfileDtoMapper.toDTO(
                enterpriseProfileUseCase.upsert(
                        request.companyName(),
                        request.legalForm(),
                        request.registrationNumber(),
                        request.vatNumber(),
                        request.addressLine1(),
                        request.addressLine2(),
                        request.city(),
                        request.postalCode(),
                        request.countryCode(),
                        request.phoneNumber(),
                        request.email(),
                        request.website(),
                        request.logoUrl()
                )
        );
    }
}
