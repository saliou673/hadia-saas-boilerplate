package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.domain.models.storagesettings.StorageSettingsFilter;
import com.hadiasaas.domain.ports.in.StorageSettingsQueryUseCase;
import com.hadiasaas.domain.ports.in.StorageSettingsUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.StorageSettingsDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.StorageSettingsDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateStorageSettingsRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateStorageSettingsRequest;
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
 * REST controller for admin storage settings management.
 */
@Validated
@RestController
@Tag(name = "Admin storage settings management")
@PreAuthorize("hasAuthority('config:manage')")
@RequestMapping("/api/v1/admin/storage-settings")
@RequiredArgsConstructor
public class AdminStorageSettingsController {

    private final StorageSettingsUseCase storageSettingsUseCase;
    private final StorageSettingsQueryUseCase storageSettingsQueryUseCase;
    private final StorageSettingsDtoMapper storageSettingsDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StorageSettingsDTO createStorageSettingsAsAdmin(@Valid @RequestBody CreateStorageSettingsRequest request) {
        return storageSettingsDtoMapper.toDTO(
                storageSettingsUseCase.create(request.provider(), request.bucketName(), request.region(), request.endpoint(), Boolean.TRUE.equals(request.active()))
        );
    }

    @GetMapping("/{id}")
    public StorageSettingsDTO getStorageSettingsByIdAsAdmin(@PathVariable Long id) {
        return storageSettingsDtoMapper.toDTO(storageSettingsUseCase.getById(id));
    }

    @GetMapping
    public PaginatedResult<StorageSettingsDTO> getStorageSettingsAsAdmin(
            StorageSettingsFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = AuditableEntity_.CREATION_DATE, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<StorageSettings> result = storageSettingsQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, storageSettingsDtoMapper::toDTO);
    }

    @PutMapping("/{id}")
    public StorageSettingsDTO updateStorageSettingsAsAdmin(@PathVariable Long id, @Valid @RequestBody UpdateStorageSettingsRequest request) {
        return storageSettingsDtoMapper.toDTO(
                storageSettingsUseCase.update(id, request.provider(), request.bucketName(), request.region(), request.endpoint(), Boolean.TRUE.equals(request.active()))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStorageSettings(@PathVariable Long id) {
        storageSettingsUseCase.delete(id);
    }
}
