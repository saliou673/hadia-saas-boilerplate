package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfigurationFilter;
import com.hadiasaas.domain.ports.in.TaxConfigurationQueryUseCase;
import com.hadiasaas.domain.ports.in.TaxConfigurationUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.TaxConfigurationDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.TaxConfigurationDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateTaxConfigurationRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateTaxConfigurationRequest;
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

/**
 * REST controller for admin tax configuration management.
 */
@Validated
@RestController
@Tag(name = "Admin tax configuration management")
@PreAuthorize("hasAuthority('config:manage')")
@RequestMapping("/api/v1/admin/tax-configurations")
@RequiredArgsConstructor
public class AdminTaxConfigurationController {

    private final TaxConfigurationUseCase taxConfigurationUseCase;
    private final TaxConfigurationQueryUseCase taxConfigurationQueryUseCase;
    private final TaxConfigurationDtoMapper taxConfigurationDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaxConfigurationDTO createTaxConfigurationAsAdmin(@Valid @RequestBody CreateTaxConfigurationRequest request) {
        return taxConfigurationDtoMapper.toDTO(
                taxConfigurationUseCase.create(request.code(), request.name(), request.rate(), request.description())
        );
    }

    @GetMapping("/{id}")
    public TaxConfigurationDTO getTaxConfigurationByIdAsAdmin(@PathVariable Long id) {
        return taxConfigurationDtoMapper.toDTO(taxConfigurationUseCase.getById(id));
    }

    @GetMapping
    public PaginatedResult<TaxConfigurationDTO> getTaxConfigurationsAsAdmin(
            TaxConfigurationFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = AuditableEntity_.CREATION_DATE, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<TaxConfiguration> result = taxConfigurationQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, taxConfigurationDtoMapper::toDTO);
    }

    @PutMapping("/{id}")
    public TaxConfigurationDTO updateTaxConfigurationAsAdmin(@PathVariable Long id, @Valid @RequestBody UpdateTaxConfigurationRequest request) {
        return taxConfigurationDtoMapper.toDTO(
                taxConfigurationUseCase.update(id, request.code(), request.name(), request.rate(), request.description(), Boolean.TRUE.equals(request.active()))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaxConfiguration(@PathVariable Long id) {
        taxConfigurationUseCase.delete(id);
    }
}
