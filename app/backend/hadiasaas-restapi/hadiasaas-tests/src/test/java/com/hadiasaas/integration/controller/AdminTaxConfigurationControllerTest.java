package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.TaxConfigurationDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateTaxConfigurationRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateTaxConfigurationRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TaxConfigurationEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.TaxConfigurationRepository;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminTaxConfigurationControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/tax-configurations";

    @Autowired
    private TaxConfigurationRepository taxConfigurationRepository;

    // region create

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldCreateTaxConfigurationSuccessfully() throws Exception {
        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "TVA_20", "TVA 20%", new BigDecimal("0.20"), "Standard VAT rate"
        );

        TaxConfigurationDTO result = post(API, request, TaxConfigurationDTO.class, status().isCreated());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCode()).isEqualTo("TVA_20");
        assertThat(result.getName()).isEqualTo("TVA 20%");
        assertThat(result.getRate()).isEqualByComparingTo(new BigDecimal("0.20"));
        assertThat(result.isActive()).isTrue();

        assertThat(taxConfigurationRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithBlankCode() throws Exception {
        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "", "TVA 20%", new BigDecimal("0.20"), null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithBlankName() throws Exception {
        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "TVA_20", "", new BigDecimal("0.20"), null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithNullRate() throws Exception {
        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "TVA_20", "TVA 20%", null, null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithRateAboveOne() throws Exception {
        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "TVA_20", "TVA 20%", new BigDecimal("1.01"), null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithNegativeRate() throws Exception {
        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "TVA_20", "TVA 20%", new BigDecimal("-0.01"), null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToCreateWithDuplicateCode() throws Exception {
        saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);

        CreateTaxConfigurationRequest request = new CreateTaxConfigurationRequest(
                "TVA_20", "Another TVA", new BigDecimal("0.15"), null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithoutPermission() throws Exception {
        post(API, new CreateTaxConfigurationRequest("TVA_20", "TVA 20%", new BigDecimal("0.20"), null), status().isUnauthorized());
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldGetTaxConfigurationByIdSuccessfully() throws Exception {
        TaxConfigurationEntity entity = saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);

        TaxConfigurationDTO result = get(API + "/" + entity.getId(), TaxConfigurationDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getCode()).isEqualTo("TVA_20");
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
    void shouldGetAllTaxConfigurationsSuccessfully() throws Exception {
        saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);
        saveTaxConfiguration("TVA_10", "TVA 10%", new BigDecimal("0.10"), false);

        PaginatedResult<TaxConfigurationDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnEmptyWhenNoTaxConfigurationsExist() throws Exception {
        PaginatedResult<TaxConfigurationDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByActiveEquals() throws Exception {
        saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);
        saveTaxConfiguration("TVA_10", "TVA 10%", new BigDecimal("0.10"), false);

        PaginatedResult<TaxConfigurationDTO> result = get(
                API + "?active.equals=true",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getCode()).isEqualTo("TVA_20");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFilterByCodeEquals() throws Exception {
        saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);
        saveTaxConfiguration("TVA_10", "TVA 10%", new BigDecimal("0.10"), false);

        PaginatedResult<TaxConfigurationDTO> result = get(
                API + "?code.equals=TVA_10",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getCode()).isEqualTo("TVA_10");
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldSupportPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            saveTaxConfiguration("TVA_" + i, "TVA " + i + "%", new BigDecimal("0.0" + i), true);
        }

        PaginatedResult<TaxConfigurationDTO> firstPage = get(
                API + "?page=0&size=2",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(firstPage.getTotalItems()).isEqualTo(5);
        assertThat(firstPage.getItems()).hasSize(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
    }

    // endregion

    // region update

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldUpdateTaxConfigurationSuccessfully() throws Exception {
        TaxConfigurationEntity entity = saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);
        UpdateTaxConfigurationRequest request = new UpdateTaxConfigurationRequest(
                "TVA_20", "TVA 20% Updated", new BigDecimal("0.20"), "Updated desc", false
        );

        TaxConfigurationDTO result = put(API + "/" + entity.getId(), request, TaxConfigurationDTO.class, status().isOk());

        assertThat(result.getName()).isEqualTo("TVA 20% Updated");
        assertThat(result.isActive()).isFalse();

        TaxConfigurationEntity updated = taxConfigurationRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.isActive()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToUpdateWhenNotFound() throws Exception {
        UpdateTaxConfigurationRequest request = new UpdateTaxConfigurationRequest(
                "TVA_20", "TVA 20%", new BigDecimal("0.20"), null, true
        );
        put(API + "/99999", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailToUpdateWithDuplicateCode() throws Exception {
        saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);
        TaxConfigurationEntity second = saveTaxConfiguration("TVA_10", "TVA 10%", new BigDecimal("0.10"), false);

        UpdateTaxConfigurationRequest request = new UpdateTaxConfigurationRequest(
                "TVA_20", "Renamed", new BigDecimal("0.10"), null, false
        );
        put(API + "/" + second.getId(), request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldAllowUpdateWithSameCode() throws Exception {
        TaxConfigurationEntity entity = saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);
        UpdateTaxConfigurationRequest request = new UpdateTaxConfigurationRequest(
                "TVA_20", "TVA 20% Updated", new BigDecimal("0.20"), null, true
        );

        TaxConfigurationDTO result = put(API + "/" + entity.getId(), request, TaxConfigurationDTO.class, status().isOk());

        assertThat(result.getCode()).isEqualTo("TVA_20");
        assertThat(result.getName()).isEqualTo("TVA 20% Updated");
    }

    // endregion

    // region delete

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldDeleteTaxConfigurationSuccessfully() throws Exception {
        TaxConfigurationEntity entity = saveTaxConfiguration("TVA_20", "TVA 20%", new BigDecimal("0.20"), true);

        delete(API + "/" + entity.getId(), status().isNoContent());

        assertThat(taxConfigurationRepository.findById(entity.getId())).isEmpty();
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

    private TaxConfigurationEntity saveTaxConfiguration(String code, String name, BigDecimal rate, boolean active) {
        TaxConfigurationEntity entity = new TaxConfigurationEntity(null, code, name, rate, null, active);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return taxConfigurationRepository.save(entity);
    }
}
