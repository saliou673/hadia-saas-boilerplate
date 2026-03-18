package com.hadiasaas.integration.controller;

import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.SecuritySettingsDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpsertSecuritySettingsRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.SecuritySettingsRepository;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminSecuritySettingsControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/security-settings";

    @Autowired
    private SecuritySettingsRepository securitySettingsRepository;

    // region get

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnDefaultSecuritySettingsWhenNotYetConfigured() throws Exception {
        SecuritySettingsDTO result = get(API, SecuritySettingsDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.isTwoFactorRequired()).isFalse();
        // No row in DB yet — returns default
        assertThat(securitySettingsRepository.findAll()).isEmpty();
    }

    @Test
    void shouldRejectGetSecuritySettingsWithoutPermission() throws Exception {
        get(API, status().isUnauthorized());
    }

    // endregion

    // region upsert

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldCreateSecuritySettingsOnFirstUpsert() throws Exception {
        UpsertSecuritySettingsRequest request = new UpsertSecuritySettingsRequest(true);

        SecuritySettingsDTO result = put(API, request, SecuritySettingsDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.isTwoFactorRequired()).isTrue();
        assertThat(result.getLastUpdatedBy()).isNotBlank();

        assertThat(securitySettingsRepository.findAll()).hasSize(1);
        assertThat(securitySettingsRepository.findAll().get(0).isTwoFactorRequired()).isTrue();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldUpdateExistingSecuritySettingsOnSecondUpsert() throws Exception {
        // First upsert — enable 2FA requirement
        put(API, new UpsertSecuritySettingsRequest(true), SecuritySettingsDTO.class, status().isOk());
        assertThat(securitySettingsRepository.findAll()).hasSize(1);

        // Second upsert — disable 2FA requirement
        SecuritySettingsDTO result = put(API, new UpsertSecuritySettingsRequest(false), SecuritySettingsDTO.class, status().isOk());

        assertThat(result.isTwoFactorRequired()).isFalse();
        // Still only one row (singleton)
        assertThat(securitySettingsRepository.findAll()).hasSize(1);
        assertThat(securitySettingsRepository.findAll().get(0).isTwoFactorRequired()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "config:manage")
    void shouldReturnPersistedValueAfterUpsert() throws Exception {
        put(API, new UpsertSecuritySettingsRequest(true), SecuritySettingsDTO.class, status().isOk());

        SecuritySettingsDTO result = get(API, SecuritySettingsDTO.class, status().isOk());

        assertThat(result.isTwoFactorRequired()).isTrue();
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void shouldRejectUpsertSecuritySettingsWithoutPermission() throws Exception {
        put(API, new UpsertSecuritySettingsRequest(true), status().isUnauthorized());
    }

    // endregion
}
