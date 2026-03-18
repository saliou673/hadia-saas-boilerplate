package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.SubscriptionPlanDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.CreateSubscriptionPlanRequest;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.UpdateSubscriptionPlanRequest;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import com.hadiasaas.integration.IntegrationTest;
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
        createSubscriptionPlanAsAdminCurrency(CURRENCY_CODE);
    }

    // region create

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateSubscriptionPlanAsAdminMonthlyPlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Online Premium", "Full online access",
                new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, List.of("Video lessons", "Quizzes"), true
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Online Premium");
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
    void shouldCreateSubscriptionPlanAsAdminMultiPricePlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Full Plan", null,
                new BigDecimal("9.99"), new BigDecimal("89.99"), new BigDecimal("199.99"), null, null,
                CURRENCY_CODE, null, true
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.getMonthlyPrice()).isEqualByComparingTo("9.99");
        assertThat(result.getYearlyPrice()).isEqualByComparingTo("89.99");
        assertThat(result.getLifetimePrice()).isEqualByComparingTo("199.99");
        assertThat(result.getPrice()).isNull();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateSubscriptionPlanAsAdminCustomCyclePlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "14-Day Trial", null,
                null, null, null, new BigDecimal("4.99"), 14,
                CURRENCY_CODE, null, true
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.getPrice()).isEqualByComparingTo("4.99");
        assertThat(result.getDurationDays()).isEqualTo(14);
        assertThat(result.getMonthlyPrice()).isNull();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldCreateSubscriptionPlanAsAdminInactivePlanSuccessfully() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Draft", null,
                new BigDecimal("0.00"), null, null, null, null,
                CURRENCY_CODE, null, false
        );

        SubscriptionPlanDTO result = post(API, request, SubscriptionPlanDTO.class, status().isCreated());

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithAllPricesNull() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, null, null, null, null, null,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithCustomPriceButNoDurationDays() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, null, null, null, new BigDecimal("4.99"), null,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithDurationDaysButNoCustomPrice() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, null, null, null, null, 30,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithBlankTitle() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithNegativeMonthlyPrice() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("-1.00"), null, null, null, null,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithInvalidCurrencyCode() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                "INVALID", null, true
        );
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldFailToCreateSubscriptionPlanAsAdminWithInactiveCurrency() throws Exception {
        AppConfigurationEntity inactive = new AppConfigurationEntity(null, AppConfigurationCategory.CURRENCY, "EUR", "Euro", null, false);
        inactive.setCreationDate(Instant.now());
        inactive.setLastUpdateDate(Instant.now());
        inactive.setLastUpdatedBy("test");
        appConfigurationRepository.save(inactive);

        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                "EUR", null, true
        );
        post(API, request, status().isBadRequest());
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldGetPlanByIdSuccessfully() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);

        SubscriptionPlanDTO result = get(API + "/" + entity.getId(), new TypeReference<>() {}, status().isOk());

        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getTitle()).isEqualTo("Basic");
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
    void shouldGetSubscriptionPlansAsAdmminPlansSuccessfully() throws Exception {
        createPlan("Online Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);
        createPlan("On-Site Workshop", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true);

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
    void shouldFilterByActiveStatus() throws Exception {
        createPlan("Active Plan", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);
        createPlan("Inactive Plan", new BigDecimal("1.99"), null, null, null, null, CURRENCY_CODE, false);

        PaginatedResult<SubscriptionPlanDTO> result = get(
                API + "?active.equals=true",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getTitle()).isEqualTo("Active Plan");
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldReturnPlansSortedByMonthlyPriceAscending() throws Exception {
        createPlan("Expensive", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true);
        createPlan("Cheap", new BigDecimal("1.99"), null, null, null, null, CURRENCY_CODE, true);
        createPlan("Middle", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        PaginatedResult<SubscriptionPlanDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getItems()).extracting(SubscriptionPlanDTO::getTitle)
                .containsExactly("Cheap", "Middle", "Expensive");
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldSupportPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            createPlan("Plan " + i, new BigDecimal(i * 10), null, null, null, null, CURRENCY_CODE, true);
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
    void shouldUpdateSubscriptionPlanAsAdminPlanSuccessfully() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic Plus", "Updated",
                new BigDecimal("7.99"), new BigDecimal("69.99"), null, null, null,
                CURRENCY_CODE, List.of("New Feature"), false
        );

        SubscriptionPlanDTO result = put(API + "/" + entity.getId(), request, SubscriptionPlanDTO.class, status().isOk());

        assertThat(result.getTitle()).isEqualTo("Basic Plus");
        assertThat(result.getMonthlyPrice()).isEqualByComparingTo("7.99");
        assertThat(result.getYearlyPrice()).isEqualByComparingTo("69.99");
        assertThat(result.getLifetimePrice()).isNull();
        assertThat(result.isActive()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "plan:update")
    void shouldFailToUpdateSubscriptionPlanAsAdminWhenNotFound() throws Exception {
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true
        );
        put(API + "/99999", request, status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "plan:update")
    void shouldFailToUpdateSubscriptionPlanAsAdminWithInvalidCurrencyCode() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                "INVALID", null, true
        );
        put(API + "/" + entity.getId(), request, status().isBadRequest());
    }

    // endregion

    // region delete

    @Test
    @WithMockUser(authorities = "plan:delete")
    void shouldDeleteSubscriptionPlanAsAdminPlanSuccessfully() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);

        delete(API + "/" + entity.getId(), status().isNoContent());

        assertThat(subscriptionPlanRepository.findById(entity.getId())).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "plan:delete")
    void shouldFailToDeleteSubscriptionPlanAsAdminWhenNotFound() throws Exception {
        delete(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region security

    @Test
    void shouldRejectUnauthenticatedCreateSubscriptionPlanAsAdmin() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void shouldForbidCreateSubscriptionPlanAsAdminForWrongPermission() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
                "Basic", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true
        );
        post(API, request, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "plan:create")
    void shouldForbidReadWithCreateSubscriptionPlanAsAdminPermissionOnly() throws Exception {
        get(API, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldForbidDeleteSubscriptionPlanAsAdminWithReadPermissionOnly() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);
        delete(API + "/" + entity.getId(), status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "plan:read")
    void shouldForbidUpdateSubscriptionPlanAsAdminWithReadPermissionOnly() throws Exception {
        SubscriptionPlanEntity entity = createPlan("Basic", new BigDecimal("4.99"), null, null, null, null, CURRENCY_CODE, true);
        UpdateSubscriptionPlanRequest request = new UpdateSubscriptionPlanRequest(
                "New Title", null, new BigDecimal("9.99"), null, null, null, null,
                CURRENCY_CODE, null, true
        );
        put(API + "/" + entity.getId(), request, status().isForbidden());
    }

    // endregion

    // monthlyPrice, yearlyPrice, lifetimePrice, price (custom), durationDays (custom)
    private SubscriptionPlanEntity createPlan(String title, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, boolean active) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity(null, title, null, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, List.of(), active);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return subscriptionPlanRepository.save(entity);
    }

    private void createSubscriptionPlanAsAdminCurrency(String code) {
        AppConfigurationEntity entity = new AppConfigurationEntity(null, AppConfigurationCategory.CURRENCY, code, code, null, true);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        appConfigurationRepository.save(entity);
    }
}
