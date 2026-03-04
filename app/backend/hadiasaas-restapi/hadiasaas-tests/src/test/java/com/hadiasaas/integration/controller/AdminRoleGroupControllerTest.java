package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.RoleGroupDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateRoleGroupRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateRoleGroupRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.RoleGroupEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.PermissionRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.RoleGroupRepository;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminRoleGroupControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/role-groups";

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    // region create

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldCreateRoleGroupSuccessfully() throws Exception {
        CreateRoleGroupRequest request = new CreateRoleGroupRequest(
                "Editors", "Can edit content", Set.of("user:read")
        );

        RoleGroupDTO result = post(API, request, RoleGroupDTO.class, status().isCreated());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Editors");
        assertThat(result.getDescription()).isEqualTo("Can edit content");
        assertThat(result.getPermissions()).hasSize(1);
        assertThat(result.getPermissions()).extracting(PermissionDTO::code)
                .containsExactly("user:read");
        assertThat(roleGroupRepository.existsByName("Editors")).isTrue();
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToCreateWithDuplicateName() throws Exception {
        createRoleGroup("Editors", "First group");

        CreateRoleGroupRequest request = new CreateRoleGroupRequest(
                "Editors", "Second group", Set.of("user:read")
        );

        post(API, request, status().isConflict());
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToCreateWithBlankName() throws Exception {
        CreateRoleGroupRequest request = new CreateRoleGroupRequest(
                "", "Description", Set.of("user:read")
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToCreateWithBlankDescription() throws Exception {
        CreateRoleGroupRequest request = new CreateRoleGroupRequest(
                "Name", "", Set.of("user:read")
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToCreateWithEmptyPermissions() throws Exception {
        CreateRoleGroupRequest request = new CreateRoleGroupRequest(
                "Name", "Description", Set.of()
        );
        post(API, request, status().isBadRequest());
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldGetRoleGroupByIdSuccessfully() throws Exception {
        RoleGroupEntity entity = createRoleGroup("Editors", "Can edit content");

        RoleGroupDTO result = get(API + "/" + entity.getId(), new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getName()).isEqualTo("Editors");
        assertThat(result.getDescription()).isEqualTo("Can edit content");
    }

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldFailToGetRoleGroupWhenNotFound() throws Exception {
        get(API + "/99999", status().isNotFound());
    }

    // endregion

    // region findAll

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldFindAllRoleGroupsSuccessfully() throws Exception {
        createRoleGroup("Group A", "First group");
        createRoleGroup("Group B", "Second group");

        List<RoleGroupDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).extracting(RoleGroupDTO::getName)
                .contains("Group A", "Group B");
    }

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldReturnSeededRoleGroupsByDefault() throws Exception {
        List<RoleGroupDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        // 6 default role groups are seeded by DML migrations
        assertThat(result).hasSize(4);
    }

    // endregion

    // region update

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldUpdateRoleGroupSuccessfully() throws Exception {
        RoleGroupEntity entity = createRoleGroup("Old Name", "Old description");

        UpdateRoleGroupRequest request = new UpdateRoleGroupRequest(
                "New Name", "New description", Set.of("role-group:manage")
        );

        RoleGroupDTO result = put(API + "/" + entity.getId(), request, RoleGroupDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getPermissions()).extracting(PermissionDTO::code)
                .containsExactly("role-group:manage");

        RoleGroupEntity updated = roleGroupRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldAllowUpdateWithSameName() throws Exception {
        RoleGroupEntity entity = createRoleGroup("Same Name", "Description");

        UpdateRoleGroupRequest request = new UpdateRoleGroupRequest(
                "Same Name", "Updated description", Set.of("user:read")
        );

        RoleGroupDTO result = put(API + "/" + entity.getId(), request, RoleGroupDTO.class, status().isOk());

        assertThat(result.getName()).isEqualTo("Same Name");
        assertThat(result.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToUpdateWithDuplicateName() throws Exception {
        createRoleGroup("Existing Name", "Group 1");
        RoleGroupEntity second = createRoleGroup("Second Group", "Group 2");

        UpdateRoleGroupRequest request = new UpdateRoleGroupRequest(
                "Existing Name", "Updated description", Set.of("user:read")
        );

        put(API + "/" + second.getId(), request, status().isConflict());
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToUpdateWhenNotFound() throws Exception {
        UpdateRoleGroupRequest request = new UpdateRoleGroupRequest(
                "Name", "Description", Set.of("user:read")
        );

        put(API + "/99999", request, status().isNotFound());
    }

    // endregion

    // region delete

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldDeleteRoleGroupSuccessfully() throws Exception {
        RoleGroupEntity entity = createRoleGroup("To Delete", "Will be deleted");

        delete(API + "/" + entity.getId(), status().isNoContent());

        assertThat(roleGroupRepository.findById(entity.getId())).isEmpty();
    }

    @Test
    @WithMockUser(authorities = {"role-group:read", "role-group:manage"})
    void shouldFailToDeleteWhenNotFound() throws Exception {
        delete(API + "/99999", status().isNotFound());
    }

    // endregion

    // region listPermissions

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldListAllPermissionsSuccessfully() throws Exception {
        List<PermissionDTO> result = get(API + "/permissions", new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotEmpty();
        assertThat(result).extracting(PermissionDTO::code)
                .contains("role-group:read", "role-group:manage", "user:read", "config:manage");
    }

    // endregion

    // region security

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void shouldForbidReadAccessForUserWithNoPermissions() throws Exception {
        get(API, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldForbidCreateForUserWithReadOnlyPermission() throws Exception {
        CreateRoleGroupRequest request = new CreateRoleGroupRequest(
                "Name", "Description", Set.of("user:read")
        );
        post(API, request, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "role-group:read")
    void shouldForbidDeleteForUserWithReadOnlyPermission() throws Exception {
        RoleGroupEntity entity = createRoleGroup("Group", "Description");

        delete(API + "/" + entity.getId(), status().isForbidden());
    }

    // endregion

    private RoleGroupEntity createRoleGroup(String name, String description) {
        PermissionEntity permission = permissionRepository.findById("user:read").orElseThrow();
        RoleGroupEntity entity = new RoleGroupEntity(null, name, description, new HashSet<>(Set.of(permission)));
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return roleGroupRepository.save(entity);
    }
}
