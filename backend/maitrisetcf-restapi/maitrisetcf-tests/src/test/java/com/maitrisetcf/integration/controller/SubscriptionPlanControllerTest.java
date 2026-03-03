package com.maitrisetcf.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import com.maitrisetcf.infrastructure.adapter.out.query.PaginatedResult;
import com.maitrisetcf.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class SubscriptionPlanControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/plans";
    private static final String CURRENCY_CODE = "XOF";

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    // region getAll (public endpoint)

    @Test
    void shouldReturnActivePlansWithoutAuthentication() throws Exception {
        createPlan("Online Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Workshop", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListWhenNoActivePlans() throws Exception {
        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void shouldReturnOnlyActivePlans() throws Exception {
        createPlan("Active Online", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Inactive On-Site", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, false, SubscriptionPlanType.ON_SITE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().isActive()).isTrue();
        assertThat(result.getItems().getFirst().getType()).isEqualTo(SubscriptionPlanType.ONLINE_TRAINING);
    }

    @Test
    void shouldReturnPlansSortedByMonthlyPriceAscending() throws Exception {
        createPlan("Expensive On-Site", null, new BigDecimal("299.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);
        createPlan("Cheap Online", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Mid Online", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).extracting(SubscriptionPlanDTO::getTitle)
                .containsExactly("Cheap Online", "Mid Online", "Expensive On-Site");
    }

    @Test
    void shouldReturnCorrectTypeInResponse() throws Exception {
        createPlan("Online Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Plan", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems()).extracting(SubscriptionPlanDTO::getType)
                .containsExactlyInAnyOrder(SubscriptionPlanType.ONLINE_TRAINING, SubscriptionPlanType.ON_SITE_TRAINING);
    }

    @Test
    void shouldReturnPlanWithAllStandardPrices() throws Exception {
        SubscriptionPlanEntity entity = createPlanWithFeatures(
                "Premium Online",
                new BigDecimal("9.99"), new BigDecimal("89.99"), new BigDecimal("199.99"), null, null,
                CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING,
                List.of("Unlimited access", "Priority support")
        );

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).hasSize(1);
        SubscriptionPlanDTO dto = result.getItems().getFirst();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getTitle()).isEqualTo("Premium Online");
        assertThat(dto.getMonthlyPrice()).isEqualByComparingTo("9.99");
        assertThat(dto.getYearlyPrice()).isEqualByComparingTo("89.99");
        assertThat(dto.getLifetimePrice()).isEqualByComparingTo("199.99");
        assertThat(dto.getPrice()).isNull();
        assertThat(dto.getDurationDays()).isNull();
        assertThat(dto.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
        assertThat(dto.getFeatures()).containsExactly("Unlimited access", "Priority support");
    }

    @Test
    void shouldReturnCustomCyclePlan() throws Exception {
        createPlan("Trial", null, null, null, new BigDecimal("4.99"), 14, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getPrice()).isEqualByComparingTo("4.99");
        assertThat(result.getItems().getFirst().getDurationDays()).isEqualTo(14);
        assertThat(result.getItems().getFirst().getMonthlyPrice()).isNull();
    }

    @Test
    void shouldReturnLifetimePlan() throws Exception {
        createPlan("Lifetime Online", null, null, new BigDecimal("199.99"), null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().getFirst().getLifetimePrice()).isEqualByComparingTo("199.99");
        assertThat(result.getItems().getFirst().getMonthlyPrice()).isNull();
    }

    @Test
    void shouldSupportPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            createPlan("Plan " + i, new BigDecimal(i * 10), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        }

        PaginatedResult<SubscriptionPlanDTO> firstPage = get(API + "?page=0&size=2", new TypeReference<>() {}, status().isOk());

        assertThat(firstPage.getItems()).hasSize(2);
        assertThat(firstPage.getTotalItems()).isEqualTo(5);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
    }

    // endregion

    // monthlyPrice, yearlyPrice, lifetimePrice, price (custom), durationDays (custom)
    private SubscriptionPlanEntity createPlan(String title, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, boolean active, SubscriptionPlanType type) {
        return createPlanWithFeatures(title, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, active, type, List.of());
    }

    private SubscriptionPlanEntity createPlanWithFeatures(String title, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, boolean active, SubscriptionPlanType type, List<String> features) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity(null, title, null, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, features, active, type);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return subscriptionPlanRepository.save(entity);
    }
}
