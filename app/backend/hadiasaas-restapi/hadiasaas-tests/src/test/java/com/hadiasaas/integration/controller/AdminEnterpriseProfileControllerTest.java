package com.hadiasaas.integration.controller;

import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.EnterpriseProfileDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpsertEnterpriseProfileRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.EnterpriseProfileRepository;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminEnterpriseProfileControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/enterprise-profile";

    @Autowired
    private EnterpriseProfileRepository enterpriseProfileRepository;

    // region get

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnErrorWhenProfileNotYetConfigured() throws Exception {
        get(API, status().isBadRequest());
    }

    @Test
    void shouldRejectGetWithoutPermission() throws Exception {
        get(API, status().isUnauthorized());
    }

    // endregion

    // region upsert

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldCreateEnterpriseProfileOnFirstUpsert() throws Exception {
        UpsertEnterpriseProfileRequest request = new UpsertEnterpriseProfileRequest(
                "Acme Corp", "SAS", "123456789", "FR12345678901",
                "1 rue de la Paix", null, "Paris", "75001", "FR",
                "+33612345678", "contact@acme.com", "https://acme.com", null
        );

        EnterpriseProfileDTO result = put(API, request, EnterpriseProfileDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(result.getCity()).isEqualTo("Paris");
        assertThat(result.getCountryCode()).isEqualTo("FR");
        assertThat(result.getLastUpdatedBy()).isNotBlank();

        assertThat(enterpriseProfileRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldUpdateExistingProfileOnSecondUpsert() throws Exception {
        // First upsert
        put(API, buildRequest("Acme Corp", "FR"), EnterpriseProfileDTO.class, status().isOk());
        assertThat(enterpriseProfileRepository.findAll()).hasSize(1);

        // Second upsert — change company name and country
        EnterpriseProfileDTO result = put(API, buildRequest("Acme Corp Updated", "SN"), EnterpriseProfileDTO.class, status().isOk());

        assertThat(result.getCompanyName()).isEqualTo("Acme Corp Updated");
        assertThat(result.getCountryCode()).isEqualTo("SN");
        // Still only one row (singleton)
        assertThat(enterpriseProfileRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnPersistedValueAfterUpsert() throws Exception {
        put(API, buildRequest("Acme Corp", "FR"), EnterpriseProfileDTO.class, status().isOk());

        EnterpriseProfileDTO result = get(API, EnterpriseProfileDTO.class, status().isOk());

        assertThat(result.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(result.getCountryCode()).isEqualTo("FR");
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailUpsertWithBlankCompanyName() throws Exception {
        UpsertEnterpriseProfileRequest request = new UpsertEnterpriseProfileRequest(
                "", null, null, null, null, null, null, null, null, null, null, null, null
        );
        put(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldFailUpsertWithInvalidCountryCode() throws Exception {
        UpsertEnterpriseProfileRequest request = new UpsertEnterpriseProfileRequest(
                "Acme Corp", null, null, null, null, null, null, null, "FRANCE", null, null, null, null
        );
        put(API, request, status().isBadRequest());
    }

    @Test
    void shouldRejectUpsertWithoutPermission() throws Exception {
        put(API, buildRequest("Acme Corp", "FR"), status().isUnauthorized());
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

    private UpsertEnterpriseProfileRequest buildRequest(String companyName, String countryCode) {
        return new UpsertEnterpriseProfileRequest(
                companyName, null, null, null, null, null, null, null, countryCode, null, null, null, null
        );
    }
}
