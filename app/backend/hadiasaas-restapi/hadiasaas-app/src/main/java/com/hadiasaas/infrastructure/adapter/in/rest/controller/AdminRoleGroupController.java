package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.domain.models.rbac.RoleGroup;
import com.hadiasaas.domain.ports.in.PermissionUseCase;
import com.hadiasaas.domain.ports.in.RoleGroupUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.RoleGroupDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.PermissionDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.RoleGroupDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateRoleGroupRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateRoleGroupRequest;
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
/** REST controller for admin role group management. */
@RestController
@Tag(name = "Role group management")
@PreAuthorize("hasAuthority('role-group:read')")
@RequestMapping("/api/v1/admin/role-groups")
@RequiredArgsConstructor
public class AdminRoleGroupController {

    private final RoleGroupUseCase roleGroupUseCase;
    private final PermissionUseCase permissionUseCase;
    private final RoleGroupDtoMapper roleGroupDtoMapper;
    private final PermissionDtoMapper permissionDtoMapper;

    @GetMapping
    public PaginatedResult<RoleGroupDTO> getRoleGroupsAsAdmin(
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<RoleGroup> result = roleGroupUseCase.findAll(pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, roleGroupDtoMapper::toDTO);
    }

    @GetMapping("/{id}")
    public RoleGroupDTO getRoleGroupByIdAsAdmin(@PathVariable Long id) {
        return roleGroupDtoMapper.toDTO(roleGroupUseCase.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('role-group:manage')")
    public RoleGroupDTO createRoleGroupAsAdmin(@Valid @RequestBody CreateRoleGroupRequest request) {
        return roleGroupDtoMapper.toDTO(
                roleGroupUseCase.create(request.name(), request.description(), request.permissionCodes())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role-group:manage')")
    public RoleGroupDTO updateRoleGroupAsAdmin(@PathVariable Long id, @Valid @RequestBody UpdateRoleGroupRequest request) {
        return roleGroupDtoMapper.toDTO(
                roleGroupUseCase.update(id, request.name(), request.description(), request.permissionCodes())
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role-group:manage')")
    public void deleteRoleGroupAsAdmin(@PathVariable Long id) {
        roleGroupUseCase.delete(id);
    }

    @GetMapping("/permissions")
    public PaginatedResult<PermissionDTO> getPermissionsAsAdmin(
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = "code", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PagedResult<Permission> result = permissionUseCase.findAll(pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, permissionDtoMapper::toDTO);
    }
}
