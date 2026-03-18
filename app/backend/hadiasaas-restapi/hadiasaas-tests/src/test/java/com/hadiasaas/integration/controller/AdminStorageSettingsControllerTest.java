package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.domain.enumerations.StorageProvider;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.StorageSettingsDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateStorageSettingsRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateStorageSettingsRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.StorageSettingsEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.StorageSettingsRepository;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminStorageSettingsControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/storage-settings";

    @Autowired
    private StorageSettingsRepository storageSettingsRepository;

    // region create

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldCreateStorageSettingsSuccessfully() throws Exception {
        CreateStorageSettingsRequest request = new CreateStorageSettingsRequest(
                StorageProvider.AWS_S3, "my-bucket", "eu-west-1", null, true
        );

        StorageSettingsDTO result = post(API, request, StorageSettingsDTO.class, status().isCreated());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getProvider()).isEqualTo(StorageProvider.AWS_S3);
        assertThat(result.getBucketName()).isEqualTo("my-bucket");
        assertThat(result.isActive()).isTrue();

        assertThat(storageSettingsRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithNullProvider() throws Exception {
        CreateStorageSettingsRequest request = new CreateStorageSettingsRequest(
                null, "my-bucket", "eu-west-1", null, false
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateSecondActiveStorageSettings() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);

        CreateStorageSettingsRequest request = new CreateStorageSettingsRequest(
                StorageProvider.GCS, "bucket-2", null, null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldAllowCreatingInactiveWhenActiveAlreadyExists() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);

        CreateStorageSettingsRequest request = new CreateStorageSettingsRequest(
                StorageProvider.LOCAL, null, null, null, false
        );
        StorageSettingsDTO result = post(API, request, StorageSettingsDTO.class, status().isCreated());

        assertThat(result.isActive()).isFalse();
        assertThat(storageSettingsRepository.findAll()).hasSize(2);
    }

    @Test
    void shouldRejectCreateWithoutPermission() throws Exception {
        post(API, new CreateStorageSettingsRequest(StorageProvider.LOCAL, null, null, null, false), status().isUnauthorized());
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldGetStorageSettingsByIdSuccessfully() throws Exception {
        StorageSettingsEntity entity = saveStorageSettings(StorageProvider.AWS_S3, "my-bucket", true);

        StorageSettingsDTO result = get(API + "/" + entity.getId(), StorageSettingsDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getProvider()).isEqualTo(StorageProvider.AWS_S3);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToGetByIdWhenNotFound() throws Exception {
        get(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region getAll

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldGetAllStorageSettingsSuccessfully() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);
        saveStorageSettings(StorageProvider.LOCAL, null, false);

        PaginatedResult<StorageSettingsDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnEmptyWhenNoStorageSettingsExist() throws Exception {
        PaginatedResult<StorageSettingsDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByActiveEquals() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);
        saveStorageSettings(StorageProvider.LOCAL, null, false);

        PaginatedResult<StorageSettingsDTO> result = get(
                API + "?active.equals=true",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getProvider()).isEqualTo(StorageProvider.AWS_S3);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByProviderEquals() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);
        saveStorageSettings(StorageProvider.LOCAL, null, false);

        PaginatedResult<StorageSettingsDTO> result = get(
                API + "?provider.equals=AWS_S3",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getProvider()).isEqualTo(StorageProvider.AWS_S3);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldSupportPagination() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);
        saveStorageSettings(StorageProvider.LOCAL, null, false);
        saveStorageSettings(StorageProvider.GCS, "bucket-gcs", false);

        PaginatedResult<StorageSettingsDTO> firstPage = get(
                API + "?page=0&size=2",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(firstPage.getTotalItems()).isEqualTo(3);
        assertThat(firstPage.getItems()).hasSize(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
    }

    // endregion

    // region update

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldUpdateStorageSettingsSuccessfully() throws Exception {
        StorageSettingsEntity entity = saveStorageSettings(StorageProvider.AWS_S3, "old-bucket", false);
        UpdateStorageSettingsRequest request = new UpdateStorageSettingsRequest(
                StorageProvider.AWS_S3, "new-bucket", "us-east-1", null, true
        );

        StorageSettingsDTO result = put(API + "/" + entity.getId(), request, StorageSettingsDTO.class, status().isOk());

        assertThat(result.getBucketName()).isEqualTo("new-bucket");
        assertThat(result.isActive()).isTrue();

        StorageSettingsEntity updated = storageSettingsRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.getBucketName()).isEqualTo("new-bucket");
        assertThat(updated.isActive()).isTrue();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToUpdateWhenNotFound() throws Exception {
        UpdateStorageSettingsRequest request = new UpdateStorageSettingsRequest(
                StorageProvider.LOCAL, null, null, null, false
        );
        put(API + "/99999", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToUpdateToActiveWhenAnotherAlreadyActive() throws Exception {
        saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);
        StorageSettingsEntity second = saveStorageSettings(StorageProvider.LOCAL, null, false);

        UpdateStorageSettingsRequest request = new UpdateStorageSettingsRequest(
                StorageProvider.LOCAL, null, null, null, true
        );
        put(API + "/" + second.getId(), request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldAllowUpdatingCurrentActiveEntry() throws Exception {
        StorageSettingsEntity entity = saveStorageSettings(StorageProvider.AWS_S3, "bucket-1", true);
        UpdateStorageSettingsRequest request = new UpdateStorageSettingsRequest(
                StorageProvider.AWS_S3, "new-bucket", "eu-west-1", null, true
        );

        StorageSettingsDTO result = put(API + "/" + entity.getId(), request, StorageSettingsDTO.class, status().isOk());

        assertThat(result.isActive()).isTrue();
        assertThat(result.getBucketName()).isEqualTo("new-bucket");
        assertThat(storageSettingsRepository.findAll()).hasSize(1);
    }

    // endregion

    // region delete

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldDeleteStorageSettingsSuccessfully() throws Exception {
        StorageSettingsEntity entity = saveStorageSettings(StorageProvider.LOCAL, null, false);

        delete(API + "/" + entity.getId(), status().isNoContent());

        assertThat(storageSettingsRepository.findById(entity.getId())).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToDeleteWhenNotFound() throws Exception {
        delete(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region security

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void shouldForbidAccessForSimpleUser() throws Exception {
        get(API, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldForbidAccessForAdmin() throws Exception {
        get(API, status().isForbidden());
    }

    // endregion

    private StorageSettingsEntity saveStorageSettings(StorageProvider provider, String bucketName, boolean active) {
        StorageSettingsEntity entity = new StorageSettingsEntity(null, provider, bucketName, null, null, active);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return storageSettingsRepository.save(entity);
    }
}
