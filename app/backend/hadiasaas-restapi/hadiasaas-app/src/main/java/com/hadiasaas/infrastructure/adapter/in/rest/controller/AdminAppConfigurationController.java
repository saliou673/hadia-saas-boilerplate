package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.domain.models.appconfiguration.AppConfiguration;
import com.hadiasaas.domain.models.appconfiguration.AppConfigurationFilter;
import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.ports.in.AppConfigurationQueryUseCase;
import com.hadiasaas.domain.ports.in.AppConfigurationUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.AppConfigurationDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.AppConfigurationDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateAppConfigurationRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateAppConfigurationRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.AuditableEntity_;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hadiasaas.util.PaginationConstants.DEFAULT_PAGE_SIZE_INT;

@Validated
/** REST controller for admin application configuration management. */
@RestController
@Tag(name = "Admin configuration management")
@PreAuthorize("hasAuthority('config:manage')")
@RequestMapping("/api/v1/admin/configurations")
@RequiredArgsConstructor
public class AdminAppConfigurationController {

    private final AppConfigurationUseCase appConfigurationUseCase;
    private final AppConfigurationQueryUseCase appConfigurationQueryUseCase;
    private final AppConfigurationDtoMapper appConfigurationDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppConfigurationDTO create(@Valid @RequestBody CreateAppConfigurationRequest request) {
        return appConfigurationDtoMapper.toDTO(
                appConfigurationUseCase.create(request.category(), request.code(), request.label(), request.description())
        );
    }

    @GetMapping("/{id}")
    public AppConfigurationDTO getById(@PathVariable Long id) {
        return appConfigurationDtoMapper.toDTO(appConfigurationUseCase.getById(id));
    }

    @GetMapping
    public PaginatedResult<AppConfigurationDTO> getAll(
            AppConfigurationFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = AuditableEntity_.CREATION_DATE, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<AppConfiguration> result = appConfigurationQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, appConfigurationDtoMapper::toDTO);
    }

    @PutMapping("/{id}")
    public AppConfigurationDTO update(@PathVariable Long id, @Valid @RequestBody UpdateAppConfigurationRequest request) {
        return appConfigurationDtoMapper.toDTO(
                appConfigurationUseCase.update(id, request.code(), request.label(), request.description(), Boolean.TRUE.equals(request.active()))
        );
    }

    @PutMapping("/{category}/{code}")
    public AppConfigurationDTO updateByCategoryAndCode(
            @PathVariable AppConfigurationCategory category,
            @PathVariable String code,
            @Valid @RequestBody UpdateAppConfigurationRequest request
    ) {
        return appConfigurationDtoMapper.toDTO(
                appConfigurationUseCase.updateByCategoryAndCode(
                        category, code, request.code(), request.label(), request.description(),
                        Boolean.TRUE.equals(request.active())
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        appConfigurationUseCase.delete(id);
    }
}
