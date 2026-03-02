package com.maitrisetcf.infrastructure.adapter.in.rest.controller;

import com.maitrisetcf.domain.ports.in.PermissionUseCase;
import com.maitrisetcf.domain.ports.in.RoleGroupUseCase;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.RoleGroupDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.PermissionDtoMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.RoleGroupDtoMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.CreateRoleGroupRequest;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.UpdateRoleGroupRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
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
    public List<RoleGroupDTO> findAll() {
        return roleGroupDtoMapper.toDTO(roleGroupUseCase.findAll());
    }

    @GetMapping("/{id}")
    public RoleGroupDTO getById(@PathVariable Long id) {
        return roleGroupDtoMapper.toDTO(roleGroupUseCase.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('role-group:manage')")
    public RoleGroupDTO create(@Valid @RequestBody CreateRoleGroupRequest request) {
        return roleGroupDtoMapper.toDTO(
                roleGroupUseCase.create(request.name(), request.description(), request.permissionCodes())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role-group:manage')")
    public RoleGroupDTO update(@PathVariable Long id, @Valid @RequestBody UpdateRoleGroupRequest request) {
        return roleGroupDtoMapper.toDTO(
                roleGroupUseCase.update(id, request.name(), request.description(), request.permissionCodes())
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role-group:manage')")
    public void delete(@PathVariable Long id) {
        roleGroupUseCase.delete(id);
    }

    @GetMapping("/permissions")
    public List<PermissionDTO> listPermissions() {
        return permissionDtoMapper.toDTO(permissionUseCase.findAll());
    }
}
