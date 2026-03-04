package com.maitrisetcf.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maitrisetcf.domain.enumerations.DiscountType;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.DiscountCodeStatusResponse;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.DiscountCodeRepository;
import com.maitrisetcf.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class DiscountCodeControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/discount-codes";

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Test
    void shouldReturnValidStatusForActiveCode() throws Exception {
        createDiscountCode("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, LocalDate.now().plusDays(10), 10, 1);

        DiscountCodeStatusResponse result = get(API + "/WELCOME10/status", new TypeReference<>() {}, status().isOk());

        assertThat(result.code()).isEqualTo("WELCOME10");
        assertThat(result.valid()).isTrue();
        assertThat(result.discountType()).isEqualTo(DiscountType.PERCENTAGE);
        assertThat(result.discountValue()).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldReturnInvalidStatusForUnknownCode() throws Exception {
        DiscountCodeStatusResponse result = get(API + "/UNKNOWN/status", new TypeReference<>() {}, status().isOk());

        assertThat(result.code()).isEqualTo("UNKNOWN");
        assertThat(result.valid()).isFalse();
        assertThat(result.discountType()).isNull();
    }

    @Test
    void shouldReturnInvalidStatusForInactiveExpiredAndMaxedCodes() throws Exception {
        createDiscountCode("INACTIVE", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, false, null, null, 0);
        createDiscountCode("EXPIRED", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, LocalDate.now().minusDays(1), null, 0);
        createDiscountCode("MAXED", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, null, 1, 1);

        DiscountCodeStatusResponse inactive = get(API + "/INACTIVE/status", new TypeReference<>() {}, status().isOk());
        DiscountCodeStatusResponse expired = get(API + "/EXPIRED/status", new TypeReference<>() {}, status().isOk());
        DiscountCodeStatusResponse maxed = get(API + "/MAXED/status", new TypeReference<>() {}, status().isOk());

        assertThat(inactive.valid()).isFalse();
        assertThat(expired.valid()).isFalse();
        assertThat(maxed.valid()).isFalse();
    }

    private DiscountCodeEntity createDiscountCode(
            String code,
            DiscountType discountType,
            BigDecimal discountValue,
            String currencyCode,
            boolean active,
            LocalDate expirationDate,
            Integer maxUsages,
            int usageCount
    ) {
        DiscountCodeEntity entity = new DiscountCodeEntity(
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
}
