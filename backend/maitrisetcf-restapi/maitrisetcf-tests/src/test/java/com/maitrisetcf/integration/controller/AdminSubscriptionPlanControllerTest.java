package com.maitrisetcf.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.CreateSubscriptionPlanRequest;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.UpdateSubscriptionPlanRequest;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import com.maitrisetcf.infrastructure.adapter.out.query.PaginatedResult;
import com.maitrisetcf.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminSubscriptionPlanControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/plans";
    private static final String CURRENCY_CODE = "XOF";

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private AppConfigurationRepository appConfigurationRepository;

    @BeforeEach
    void seedCurrency() {
        createCurrency(CURRENCY_CODE);
    }

    // region create

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateMonthlyPlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Online Premium", "Full online access",
                new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, List.of("Video lessons", "Quizzes"), true, SubscriptionPlanType.ONLINE_TRAINING
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Online Premium");
        assertThat(result.getType()).isEqualTo(SubscriptionPlanType.ONLINE_TRAINING);
        assertThat(result.getMonthlyPrice()).isEqualByComparingTo("9.99");
        assertThat(result.getYearlyPrice()).isNull();
        assertThat(result.getLifetimePrice()).isNull();
        assertThat(result.getPrice()).isNull();
        assertThat(result.getDurationDays()).isNull();
        assertThat(result.getCurrencyCode()).isEqualTo(CURRENCY_CODE);
        assertThat(result.getFeatures()).containsExactly("Video lessons", "Quizzes");
        assertThat(result.isActive()).isTrue();
        assertThat(subscriptionPlanRepository.findById(result.getId())).isPresent();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateMultiPricePlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Full Plan", null,
                new BigDecimal("9.99"), new BigDecimal("89.99"), new BigDecimal("199.99"), null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.getMonthlyPrice()).isEqualByComparingTo("9.99");
        assertThat(result.getYearlyPrice()).isEqualByComparingTo("89.99");
        assertThat(result.getLifetimePrice()).isEqualByComparingTo("199.99");
        assertThat(result.getPrice()).isNull();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateCustomCyclePlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "14-Day Trial", null,
                null, null, null, new BigDecimal("4.99"), 14,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.getPrice()).isEqualByComparingTo("4.99");
        assertThat(result.getDurationDays()).isEqualTo(14);
        assertThat(result.getMonthlyPrice()).isNull();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateInactivePlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Draft", null,
                new BigDecimal("0.00"), null, null, null, null,
                CURRENCY_CODE, null, false, SubscriptionPlanType.ON_SITE_TRAINING
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithMissingType() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, null
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithAllPricesNull() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, null, null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithCustomPriceButNoDurationDays() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, null, null, null, new BigDecimal("4.99"), null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithDurationDaysButNoCustomPrice() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, null, null, null, null, 30,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithBlankTitle() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithNegativeMonthlyPrice() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("-1.00"), null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithInvalidCurrencyCode() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                "INVALID", null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateWithInactiveCurrency() throws Exception {
        AppConfigurationEntity inactive = new AppConfigurationEntity(null, AppConfigurationCategory.CURRENCY, "EUR", "Euro", null, false);
        inactive.setCreationDate(Instant.now());
        inactive.setLastUpdateDate(Instant.now());
        inactive.setLastUpdatedBy("test");
        appConfigurationRepository.save(inactive);

        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                "EUR", null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isBadRequest());
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldGetPlanByIdSuccessfully() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        SubscriptionPlanDTO result = get(API + "/" + entity.getId(), new TypeReference<>() {}, status().isOk());

        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getTitle()).isEqualTo("Basic");
        assertThat(result.getType()).isEqualTo(SubscriptionPlanType.ONLINE_TRAINING);
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldFailToGetPlanWhenNotFound() throws Exception {
        get(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region getAll + filtering

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldGetAllPlansSuccessfully() throws Exception {
        createPlan("Online Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Workshop", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldReturnEmptyWhenNoPlansExist() throws Exception {
        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldFilterByTypeEqualsOnlineTraining() throws Exception {
        createPlan("Online Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Online Premium", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Workshop", null, new BigDecimal("199.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(
                API + "?type.equals=ONLINE_TRAINING",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).extracting(SubscriptionPlanDTO::getType)
                .containsOnly(SubscriptionPlanType.ONLINE_TRAINING);
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldFilterByTypeAndActive() throws Exception {
        createPlan("Online Active", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Online Inactive", new BigDecimal("1.99"), null, null, null, null, CURRENCY_CODE, false, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("On-Site Active", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(
                API + "?type.equals=ONLINE_TRAINING&active.equals=true",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getTitle()).isEqualTo("Online Active");
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldReturnPlansSortedByMonthlyPriceAscending() throws Exception {
        createPlan("Expensive", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ON_SITE_TRAINING);
        createPlan("Cheap", new BigDecimal("1.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createPlan("Middle", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).extracting(SubscriptionPlanDTO::getTitle)
                .containsExactly("Cheap", "Middle", "Expensive");
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldSupportPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            SubscriptionPlanType type = i % 2 == 0 ? SubscriptionPlanType.ON_SITE_TRAINING : SubscriptionPlanType.ONLINE_TRAINING;
            createPlan("Plan " + i, new BigDecimal(i * 10), null, null, null, null, CURRENCY_CODE, true, type);
        }

        PaginatedResult<SubscriptionPlanDTO> firstPage = get(
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
    @WithMockUser(authorities = "plan:update")
    void shouldUpdatePlanSuccessfully() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic Plus", "Updated",
                new BigDecimal("7.99"), new BigDecimal("69.99"), null, null, null,
                CURRENCY_CODE, List.of("New Feature"), false, SubscriptionPlanType.ON_SITE_TRAINING
        );

        SubscriptionPlanDTO result = put(API + "/" + entity.getId(), request, SubscriptionPlanDTO.class, status().isOk());

        assertThat(result.getTitle()).isEqualTo("Basic Plus");
        assertThat(result.getType()).isEqualTo(SubscriptionPlanType.ON_SITE_TRAINING);
        assertThat(result.getMonthlyPrice()).isEqualByComparingTo("7.99");
        assertThat(result.getYearlyPrice()).isEqualByComparingTo("69.99");
        assertThat(result.getLifetimePrice()).isNull();
        assertThat(result.isActive()).isFalse();

        SubscriptionPlanEntity updated = subscriptionPlanRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.getType()).isEqualTo(SubscriptionPlanType.ON_SITE_TRAINING);
    }

    @Test
    @WithMockUser(authorities = "plan:update")
    void shouldFailToUpdateWhenNotFound() throws Exception {
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        put(API + "/99999", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:update")
    void shouldFailToUpdateWithMissingType() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, null
        );
        put(API + "/" + entity.getId(), request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:update")
    void shouldFailToUpdateWithInvalidCurrencyCode() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                "INVALID", null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        put(API + "/" + entity.getId(), request, status().isBadRequest());
    }

    // endregion

    // region delete

    @Test
    @WithMockUser(authorities = "plan:delete")
    void shouldDeletePlanSuccessfully() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        delete(API + "/" + entity.getId(), status().isNoContent());

        assertThat(subscriptionPlanRepository.findById(entity.getId())).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "plan:delete")
    void shouldFailToDeleteWhenNotFound() throws Exception {
        delete(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region security

    @Test
    void shouldRejectUnauthenticatedCreate() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void shouldForbidCreateForWrongPermission() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        post(API, request, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldForbidReadWithCreatePermissionOnly() throws Exception {
        get(API, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldForbidDeleteWithReadPermissionOnly() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        delete(API + "/" + entity.getId(), status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldForbidUpdateWithReadPermissionOnly() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "New Title", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true, SubscriptionPlanType.ONLINE_TRAINING
        );
        put(API + "/" + entity.getId(), request, status().isForbidden());
    }

    // endregion

    // monthlyPrice, yearlyPrice, lifetimePrice, price (custom), durationDays (custom)
    private SubscriptionPlanEntity createPlan(String title, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, boolean active, SubscriptionPlanType type) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity(null, title, null, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, List.of(), active, type);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return subscriptionPlanRepository.save(entity);
    }

    private void createCurrency(String code) {
        AppConfigurationEntity entity = new AppConfigurationEntity(null, AppConfigurationCategory.CURRENCY, code, code, null, true);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        appConfigurationRepository.save(entity);
    }
}
