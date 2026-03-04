package com.hadiasaas.infrastructure.adapter.in.rest.controller;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.rbac.Permission;
import com.hadiasaas.domain.models.user.User;
import com.hadiasaas.domain.models.user.UserFilter;
import com.hadiasaas.domain.models.user.UserInfoUpdate;
import com.hadiasaas.domain.ports.in.AccountUseCase;
import com.hadiasaas.domain.ports.in.RoleGroupUseCase;
import com.hadiasaas.domain.ports.in.UserQueryUseCase;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.PermissionCheckResponse;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.UserDetailsDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.CreateAdminUserRequestMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.PermissionDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.UpdateUserRequestMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.mapper.UserDtoMapper;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.AssignRoleGroupRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateAdminUserRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateUserRequest;
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

import java.util.Comparator;
import java.util.List;

import static com.hadiasaas.util.PaginationConstants.DEFAULT_PAGE_SIZE_INT;

/**
 * REST controller for admin user management.
 */
@Validated
@RestController
@Tag(name = "Admin user management")
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AccountUseCase accountUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final RoleGroupUseCase roleGroupUseCase;
    private final CreateAdminUserRequestMapper createAdminUserRequestMapper;
    private final UpdateUserRequestMapper updateUserRequestMapper;
    private final UserDtoMapper userDtoMapper;
    private final PermissionDtoMapper permissionDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('user:create')")
    public UserDetailsDTO createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        return userDtoMapper.toDetailsDTO(
                accountUseCase.createManagedUser(
                        createAdminUserRequestMapper.toDomain(request),
                        createAdminUserRequestMapper.toRoleGroupNames(request)
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public UserDetailsDTO getUser(@PathVariable Long id) {
        return userDtoMapper.toDetailsDTO(accountUseCase.getUserWithAuthoritiesById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public PaginatedResult<UserDetailsDTO> getUsers(
            UserFilter filter,
            @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort = AuditableEntity_.CREATION_DATE, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResult<User> result = userQueryUseCase.findAll(filter, pageable.getPageNumber(), pageable.getPageSize());
        return new PaginatedResult<>(result, userDtoMapper::toDetailsDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public UserDetailsDTO updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        UserInfoUpdate infoUpdate = updateUserRequestMapper.toDomain(request);
        return userDtoMapper.toDetailsDTO(accountUseCase.updateUserById(id, infoUpdate));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:deactivate')")
    public void deleteUser(@PathVariable Long id) {
        accountUseCase.deleteUserById(id);
    }

    @PostMapping("/{id}/role-groups")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void assignRoleGroup(@PathVariable Long id, @Valid @RequestBody AssignRoleGroupRequest request) {
        roleGroupUseCase.assignToUser(id, request.roleGroupId());
    }

    @DeleteMapping("/{id}/role-groups/{roleGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('user:update')")
    public void revokeRoleGroup(@PathVariable Long id, @PathVariable Long roleGroupId) {
        roleGroupUseCase.revokeFromUser(id, roleGroupId);
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('user:read')")
    public List<PermissionDTO> getUserPermissions(@PathVariable Long id) {
        return accountUseCase.getUserWithAuthoritiesById(id)
                .resolvePermissions()
                .stream()
                .sorted(Comparator.comparing(Permission::code))
                .map(permissionDtoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}/permissions/check")
    @PreAuthorize("hasAuthority('user:read')")
    public PermissionCheckResponse checkUserPermission(@PathVariable Long id, @RequestParam String code) {
        boolean hasPermission = accountUseCase.getUserWithAuthoritiesById(id)
                .resolvePermissions()
                .stream()
                .anyMatch(p -> p.code().equals(code));
        return new PermissionCheckResponse(hasPermission);
    }
}
