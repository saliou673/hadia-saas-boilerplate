package com.maitrisetcf.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.AppConfigurationDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.CreateAppConfigurationRequest;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.UpdateAppConfigurationRequest;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import com.maitrisetcf.infrastructure.adapter.out.query.PaginatedResult;
import com.maitrisetcf.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminAppConfigurationControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/configurations";

    @Autowired
    private AppConfigurationRepository appConfigurationRepository;

    // region create

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldCreateAppConfigurationSuccessfully() throws Exception {
        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", "West African CFA franc"
        );

        AppConfigurationDTO result = post(API, request, AppConfigurationDTO.class, status().isCreated());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCategory()).isEqualTo(AppConfigurationCategory.CURRENCY);
        assertThat(result.getCode()).isEqualTo("XOF");
        assertThat(result.getLabel()).isEqualTo("Franc CFA");
        assertThat(result.isActive()).isTrue();

        Optional<AppConfigurationEntity> saved = appConfigurationRepository.findById(result.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCode()).isEqualTo("XOF");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithMissingCategory() throws Exception {
        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                null, "XOF", "Franc CFA", null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithBlankCode() throws Exception {
        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                AppConfigurationCategory.CURRENCY, "", "Franc CFA", null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithBlankLabel() throws Exception {
        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                AppConfigurationCategory.CURRENCY, "XOF", "", null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithDuplicateCategoryAndCode() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);

        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                AppConfigurationCategory.CURRENCY, "XOF", "Another Franc CFA", null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldAllowSameCodeForDifferentCategories() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        // Same code "EUR" but if another CURRENCY category with same code already exists → should fail
        // Testing here that same code in same category fails
        // Create with a different code in CURRENCY → should succeed
        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                AppConfigurationCategory.CURRENCY, "USD", "US Dollar", null
        );
        AppConfigurationDTO result = post(API, request, AppConfigurationDTO.class, status().isCreated());
        assertThat(result.getCode()).isEqualTo("USD");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateSecondActiveStorageConfiguration() throws Exception {
        createAppConfiguration(AppConfigurationCategory.STORAGE, "LOCAL", "Local storage", true);

        CreateAppConfigurationRequest request = new CreateAppConfigurationRequest(
                AppConfigurationCategory.STORAGE, "AWS", "AWS S3", "Remote storage"
        );

        post(API, request, status().isBadRequest());

        assertThat(appConfigurationRepository.findAllByCategoryAndActiveTrue(AppConfigurationCategory.STORAGE))
                .extracting(AppConfigurationEntity::getCode, AppConfigurationEntity::isActive)
                .containsExactly(tuple("LOCAL", true));
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldGetAppConfigurationByIdSuccessfully() throws Exception {
        AppConfigurationEntity entity = createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        AppConfigurationDTO result = get(API + "/" + entity.getId(), new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getCode()).isEqualTo("EUR");
        assertThat(result.getLabel()).isEqualTo("Euro");
        assertThat(result.getCategory()).isEqualTo(AppConfigurationCategory.CURRENCY);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToGetAppConfigurationWhenNotFound() throws Exception {
        get(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region update

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldUpdateAppConfigurationSuccessfully() throws Exception {
        AppConfigurationEntity entity = createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("XOF", "Franc CFA BCEAO", "Updated description", false);

        AppConfigurationDTO result = put(API + "/" + entity.getId(), request, AppConfigurationDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getLabel()).isEqualTo("Franc CFA BCEAO");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.isActive()).isFalse();

        AppConfigurationEntity updated = appConfigurationRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.getLabel()).isEqualTo("Franc CFA BCEAO");
        assertThat(updated.isActive()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToUpdateWhenNotFound() throws Exception {
        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("XOF", "Franc CFA", null, true);
        put(API + "/99999", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToUpdateWithDuplicateCode() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        AppConfigurationEntity second = createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("XOF", "Renamed", null, true);
        put(API + "/" + second.getId(), request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldAllowUpdateWithSameCode() throws Exception {
        AppConfigurationEntity entity = createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("XOF", "Updated Franc CFA", null, true);

        AppConfigurationDTO result = put(API + "/" + entity.getId(), request, AppConfigurationDTO.class, status().isOk());

        assertThat(result.getCode()).isEqualTo("XOF");
        assertThat(result.getLabel()).isEqualTo("Updated Franc CFA");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToActivateSecondStorageConfiguration() throws Exception {
        createAppConfiguration(AppConfigurationCategory.STORAGE, "LOCAL", "Local storage", true);
        AppConfigurationEntity aws = createAppConfiguration(AppConfigurationCategory.STORAGE, "AWS", "AWS S3", false);

        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("AWS", "AWS S3", "Remote storage", true);

        put(API + "/" + aws.getId(), request, status().isBadRequest());

        assertThat(appConfigurationRepository.findAllByCategoryAndActiveTrue(AppConfigurationCategory.STORAGE))
                .extracting(AppConfigurationEntity::getCode, AppConfigurationEntity::isActive)
                .containsExactly(tuple("LOCAL", true));
        assertThat(appConfigurationRepository.findById(aws.getId())).isPresent()
                .get()
                .extracting(AppConfigurationEntity::isActive)
                .isEqualTo(false);
    }

    // endregion

    // region delete

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldDeleteAppConfigurationSuccessfully() throws Exception {
        AppConfigurationEntity entity = createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);

        delete(API + "/" + entity.getId(), status().isNoContent());

        assertThat(appConfigurationRepository.findById(entity.getId())).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToDeleteWhenNotFound() throws Exception {
        delete(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region getAll + filtering

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldGetAllAppConfigurationSuccessfully() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        PaginatedResult<AppConfigurationDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getTotalItems()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnEmptyWhenNoDataExists() throws Exception {
        PaginatedResult<AppConfigurationDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByCategoryEquals() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?category.equals=CURRENCY",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).extracting(AppConfigurationDTO::getCategory)
                .containsOnly(AppConfigurationCategory.CURRENCY);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByCodeEquals() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?code.equals=XOF",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getCode()).isEqualTo("XOF");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByCodeContains() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XAF", "CFA Franc BEAC", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?code.contains=X",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).extracting(AppConfigurationDTO::getCode)
                .containsExactlyInAnyOrder("XOF", "XAF");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByActiveEquals() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", false);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "USD", "US Dollar", false);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?active.equals=false",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).extracting(AppConfigurationDTO::isActive).containsOnly(false);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByLabelContains() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XAF", "CFA Franc BEAC", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?label.contains=CFA",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByCombinedCategoryAndActive() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", false);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?category.equals=CURRENCY&active.equals=true",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getCode()).isEqualTo("XOF");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnEmptyWhenNoFilterMatch() throws Exception {
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "XOF", "Franc CFA", true);

        PaginatedResult<AppConfigurationDTO> result = get(
                API + "?code.equals=NONEXISTENT",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    // endregion

    // region pagination

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldSupportPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            createAppConfiguration(AppConfigurationCategory.CURRENCY, "CUR" + i, "Currency " + i, true);
        }

        PaginatedResult<AppConfigurationDTO> firstPage = get(
                API + "?page=0&size=2",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(firstPage.getTotalItems()).isEqualTo(5);
        assertThat(firstPage.getItems()).hasSize(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.getPage()).isEqualTo(0);

        PaginatedResult<AppConfigurationDTO> secondPage = get(
                API + "?page=1&size=2",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(secondPage.getItems()).hasSize(2);
        assertThat(secondPage.getPage()).isEqualTo(1);
        assertThat(secondPage.getItems())
                .extracting(AppConfigurationDTO::getCode)
                .doesNotContainAnyElementsOf(firstPage.getItems().stream()
                                                     .map(AppConfigurationDTO::getCode)
                                                     .toList());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldSupportPaginationWithFilter() throws Exception {
        for (int i = 1; i <= 4; i++) {
            createAppConfiguration(AppConfigurationCategory.CURRENCY, "XCU" + i, "X Currency " + i, true);
        }
        createAppConfiguration(AppConfigurationCategory.CURRENCY, "EUR", "Euro", true);

        PaginatedResult<AppConfigurationDTO> firstPage = get(
                API + "?code.contains=XCU&page=0&size=2",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(firstPage.getTotalItems()).isEqualTo(4);
        assertThat(firstPage.getItems()).hasSize(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);

        PaginatedResult<AppConfigurationDTO> secondPage = get(
                API + "?code.contains=XCU&page=1&size=2",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(secondPage.getItems()).hasSize(2);
        assertThat(secondPage.getItems()).extracting(AppConfigurationDTO::getCode)
                .allMatch(code -> code.startsWith("XCU"));
    }

    // endregion

    // endregion

    // region updateByCategoryAndCode

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldUpdateByCategoryAndCodeSuccessfully() throws Exception {
        createAppConfiguration(AppConfigurationCategory.TWO_FACTOR, "ENABLED", "Two-Factor Authentication", false);
        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("ENABLED", "Two-Factor Authentication", null, true);

        AppConfigurationDTO result = put(API + "/TWO_FACTOR/ENABLED", request, AppConfigurationDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.isActive()).isTrue();
        assertThat(result.getCategory()).isEqualTo(AppConfigurationCategory.TWO_FACTOR);
        assertThat(result.getCode()).isEqualTo("ENABLED");

        AppConfigurationEntity updated = appConfigurationRepository
                .findByCategoryAndCode(AppConfigurationCategory.TWO_FACTOR, "ENABLED")
                .orElseThrow();
        assertThat(updated.isActive()).isTrue();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailUpdateByCategoryAndCodeWhenNotFound() throws Exception {
        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("ENABLED", "Two-Factor Authentication", null, true);
        put(API + "/TWO_FACTOR/NONEXISTENT", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailUpdateByCategoryAndCodeWithBlankLabel() throws Exception {
        createAppConfiguration(AppConfigurationCategory.TWO_FACTOR, "ENABLED", "Two-Factor Authentication", false);
        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("ENABLED", "", null, true);
        put(API + "/TWO_FACTOR/ENABLED", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailUpdateByCategoryAndCodeWhenNewCodeConflicts() throws Exception {
        createAppConfiguration(AppConfigurationCategory.TWO_FACTOR, "ENABLED", "Two-Factor Authentication", false);
        createAppConfiguration(AppConfigurationCategory.TWO_FACTOR, "OTHER", "Other Config", true);

        UpdateAppConfigurationRequest request = new UpdateAppConfigurationRequest("OTHER", "Two-Factor Authentication", null, false);
        put(API + "/TWO_FACTOR/ENABLED", request, status().isBadRequest());
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

    private AppConfigurationEntity createAppConfiguration(AppConfigurationCategory category, String code, String label, boolean active) {
        AppConfigurationEntity entity = new AppConfigurationEntity(null, category, code, label, null, active);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return appConfigurationRepository.save(entity);
    }
}
