package com.maitrisetcf.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
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
        createPlan("Online Basic", new BigDecimal("4.99"), CURRENCY_CODE, 30, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Workshop", new BigDecimal("99.99"), CURRENCY_CODE, 7, true, SubscriptionPlanType.ON_SITE_TRAINING);

        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListWhenNoActivePlans() throws Exception {
        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOnlyActivePlans() throws Exception {
        createPlan("Active Online", new BigDecimal("4.99"), CURRENCY_CODE, 30, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Inactive On-Site", new BigDecimal("99.99"), CURRENCY_CODE, 7, false, SubscriptionPlanType.ON_SITE_TRAINING);

        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().isActive()).isTrue();
        assertThat(result.getFirst().getType()).isEqualTo(SubscriptionPlanType.ONLINE_TRAINING);
    }

    @Test
    void shouldReturnPlansSortedByPriceAscending() throws Exception {
        createPlan("Expensive On-Site", new BigDecimal("299.99"), CURRENCY_CODE, 3, true, SubscriptionPlanType.ON_SITE_TRAINING);
        createPlan("Cheap Online", new BigDecimal("4.99"), CURRENCY_CODE, 30, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Mid Online", new BigDecimal("9.99"), CURRENCY_CODE, 90, true, SubscriptionPlanType.ONLINE_TRAINING);

        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).extracting(SubscriptionPlanDTO::getTitle)
                .containsExactly("Cheap Online", "Mid Online", "Expensive On-Site");
    }

    @Test
    void shouldReturnCorrectTypeInResponse() throws Exception {
        createPlan("Online Plan", new BigDecimal("9.99"), CURRENCY_CODE, 30, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Plan", new BigDecimal("99.99"), CURRENCY_CODE, 7, true, SubscriptionPlanType.ON_SITE_TRAINING);

        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(SubscriptionPlanDTO::getType)
                .containsExactlyInAnyOrder(SubscriptionPlanType.ONLINE_TRAINING, SubscriptionPlanType.ON_SITE_TRAINING);
    }

    @Test
    void shouldReturnPlanWithAllFields() throws Exception {
        SubscriptionPlanEntity entity = createPlanWithFeatures(
                "Premium Online", new BigDecimal("9.99"), CURRENCY_CODE, 365, true,
                SubscriptionPlanType.ONLINE_TRAINING, List.of("Unlimited access", "Priority support")
        );

        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).hasSize(1);
        SubscriptionPlanDTO dto = result.getFirst();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getTitle()).isEqualTo("Premium Online");
        assertThat(dto.getPrice()).isEqualByComparingTo("9.99");
        assertThat(dto.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
        assertThat(dto.getDurationDays()).isEqualTo(365);
        assertThat(dto.getType()).isEqualTo(SubscriptionPlanType.ONLINE_TRAINING);
        assertThat(dto.getFeatures()).containsExactly("Unlimited access", "Priority support");
    }

    @Test
    void shouldReturnLifetimePlan() throws Exception {
        createPlan("Lifetime Online", new BigDecimal("199.99"), CURRENCY_CODE, -1, true, SubscriptionPlanType.ONLINE_TRAINING);

        List<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDurationDays()).isEqualTo(-1);
    }

    // endregion

    private SubscriptionPlanEntity createPlan(String title, BigDecimal price, String currencyCode, int durationDays, boolean active, SubscriptionPlanType type) {
        return createPlanWithFeatures(title, price, currencyCode, durationDays, active, type, List.of());
    }

    private SubscriptionPlanEntity createPlanWithFeatures(String title, BigDecimal price, String currencyCode, int durationDays, boolean active, SubscriptionPlanType type, List<String> features) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity(null, title, null, price, currencyCode, features, durationDays, active, type);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return subscriptionPlanRepository.save(entity);
    }
}
