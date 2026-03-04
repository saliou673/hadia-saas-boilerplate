package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.domain.enumerations.DiscountType;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.DiscountCodeDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateDiscountCodeRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateDiscountCodeRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.DiscountCodeRepository;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminDiscountCodeControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/discount-codes";
    private static final String CURRENCY_CODE = "EUR";

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Autowired
    private AppConfigurationRepository appConfigurationRepository;

    @BeforeEach
    void seedCurrency() {
        createCurrency(CURRENCY_CODE);
    }

    @Test
    @WithMockUser(authorities = "discount-code:create")
    void shouldCreatePercentageDiscountCodeSuccessfully() throws Exception {
        CreateDiscountCodeRequest request = new CreateDiscountCodeRequest(
                "WELCOME10",
                DiscountType.PERCENTAGE,
                new BigDecimal("10.00"),
                null,
                true,
                LocalDate.now().plusDays(10),
                100
        );

        DiscountCodeDTO result = post(API, request, DiscountCodeDTO.class, status().isCreated());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCode()).isEqualTo("WELCOME10");
        assertThat(result.getDiscountType()).isEqualTo(DiscountType.PERCENTAGE);
        assertThat(result.getDiscountValue()).isEqualByComparingTo("10.00");
        assertThat(result.getCurrencyCode()).isNull();
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @WithMockUser(authorities = "discount-code:create")
    void shouldCreateFixedAmountDiscountCodeSuccessfully() throws Exception {
        CreateDiscountCodeRequest request = new CreateDiscountCodeRequest(
                "SAVE15",
                DiscountType.FIXED_AMOUNT,
                new BigDecimal("15.00"),
                CURRENCY_CODE,
                true,
                null,
                null
        );

        DiscountCodeDTO result = post(API, request, DiscountCodeDTO.class, status().isCreated());

        assertThat(result.getDiscountType()).isEqualTo(DiscountType.FIXED_AMOUNT);
        assertThat(result.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
    }

    @Test
    @WithMockUser(authorities = "discount-code:read")
    void shouldListDiscountCodesSuccessfully() throws Exception {
        createDiscountCodeCurrency("SAVE5", DiscountType.FIXED_AMOUNT, new BigDecimal("5.00"), CURRENCY_CODE, true, null, null, 0);
        createDiscountCodeCurrency("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, null, null, 0);

        PaginatedResult<DiscountCodeDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    @WithMockUser(authorities = "discount-code:read")
    void shouldGetDiscountCodeByIdSuccessfully() throws Exception {
        Long id = createDiscountCodeCurrency("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, null, null, 0).getId();

        DiscountCodeDTO result = get(API + "/" + id, new TypeReference<>() {}, status().isOk());

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getCode()).isEqualTo("WELCOME10");
    }

    @Test
    @WithMockUser(authorities = "discount-code:update")
    void shouldUpdateDiscountCodeSuccessfully() throws Exception {
        Long id = createDiscountCodeCurrency("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, null, 10, 0).getId();

        UpdateDiscountCodeRequest request = new UpdateDiscountCodeRequest(
                "WELCOME20",
                DiscountType.FIXED_AMOUNT,
                new BigDecimal("20.00"),
                CURRENCY_CODE,
                false,
                LocalDate.now().plusDays(5),
                5
        );

        DiscountCodeDTO result = put(API + "/" + id, request, DiscountCodeDTO.class, status().isOk());

        assertThat(result.getCode()).isEqualTo("WELCOME20");
        assertThat(result.getDiscountType()).isEqualTo(DiscountType.FIXED_AMOUNT);
        assertThat(result.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
        assertThat(result.isActive()).isFalse();
        assertThat(result.getMaxUsages()).isEqualTo(5);
    }

    @Test
    @WithMockUser(authorities = "discount-code:delete")
    void shouldDeleteDiscountCodeSuccessfully() throws Exception {
        Long id = createDiscountCodeCurrency("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, null, null, 0).getId();

        delete(API + "/" + id, status().isNoContent());

        assertThat(discountCodeRepository.findById(id)).isEmpty();
    }

    private com.hadiasaas.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity createDiscountCodeCurrency(
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages,
            int usageCount
    ) {
        var entity = new com.hadiasaas.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity(
                null,
                code,
                discountType,
                discountValue,
                currencyCode,
                active,
                expirationDate,
                maxUsages,
                usageCount
        );
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return discountCodeRepository.save(entity);
    }

    private void createCurrency(String code) {
        AppConfigurationEntity entity = new AppConfigurationEntity(null, AppConfigurationCategory.CURRENCY, code, code, null, true);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        appConfigurationRepository.save(entity);
    }
}
