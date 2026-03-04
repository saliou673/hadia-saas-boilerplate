package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.domain.models.appconfiguration.AppConfiguration;
import com.hadiasaas.domain.models.appconfiguration.AppConfigurationFilter;
import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.ports.in.AppConfigurationQueryUseCase;
import com.hadiasaas.domain.ports.in.AppConfigurationUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.AppConfigurationDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.AppConfigurationDtoMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.AuditableEntity_;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hadiasaas.util.PaginationConstants.DEFAULT_PAGE_SIZE_INT;

/**
 * REST controller for querying application configuration values.
 */
@Validated
@RestController
@Tag(name = "Configuration management")
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
public class AppConfigurationController {

    private final AppConfigurationQueryUseCase appConfigurationQueryUseCase;
    private final AppConfigurationUseCase appConfigurationUseCase;
    private final AppConfigurationDtoMapper appConfigurationDtoMapper;

    @GetMapping
    public PaginatedResult<AppConfigurationDTO> getAll(
            AppConfigurationFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = AuditableEntity_.CREATION_DATE, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<AppConfiguration> result = appConfigurationQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, appConfigurationDtoMapper::toDTO);
    }

    @GetMapping("/{category}/{code}")
    public ResponseEntity<AppConfigurationDTO> getByCategoryAndCode(
            @PathVariable AppConfigurationCategory category,
            @PathVariable String code
    ) {
        return appConfigurationUseCase.getByCategoryAndCode(category, code)
                .map(appConfigurationDtoMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
