package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.domain.enumerations.SubscriptionBillingFrequency;
import com.hadiasaas.domain.enumerations.UserSubscriptionStatus;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.UserSubscriptionDTO;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.UserSubscriptionRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AdminUserSubscriptionControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/admin/subscriptions";
    private static final String CURRENCY_CODE = "EUR";
    private static final String PAYMENT_MODE = "STRIPE";

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    private Long seedPlanId;

    @BeforeEach
    void seedPlan() {
        SubscriptionPlanEntity plan = new SubscriptionPlanEntity(
                null, "Test Plan", null, new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null,
                CURRENCY_CODE, List.of(), true
        );
        plan.setCreationDate(Instant.now());
        plan.setLastUpdateDate(Instant.now());
        plan.setLastUpdatedBy("test");
        seedPlanId = subscriptionPlanRepository.save(plan).getId();
    }

    // region getAll

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldGetUserSubscriptionAsAdminSubscriptionsSuccessfully() throws Exception {
        createSubscription(UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);
        createSubscription(UserSubscriptionStatus.EXPIRED, SubscriptionBillingFrequency.YEARLY);

        PaginatedResult<UserSubscriptionDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldReturnEmptyWhenNoSubscriptions() throws Exception {
        PaginatedResult<UserSubscriptionDTO> result = get(API, new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(0);
    }

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldFilterByStatusEquals() throws Exception {
        createSubscription(UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);
        createSubscription(UserSubscriptionStatus.CANCELLED, SubscriptionBillingFrequency.YEARLY);

        PaginatedResult<UserSubscriptionDTO> result = get(
                API + "?status.equals=ACTIVE",
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().getFirst().getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
    }

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldFilterByUserId() throws Exception {
        UserEntity user1 = createUser("user1@sub-test.com");
        UserEntity user2 = createUser("user2@sub-test.com");

        createSubscription(user1.getId(), UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);
        createSubscription(user2.getId(), UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);
        createSubscription(user1.getId(), UserSubscriptionStatus.EXPIRED, SubscriptionBillingFrequency.YEARLY);

        PaginatedResult<UserSubscriptionDTO> result = get(
                API + "?userId.equals=" + user1.getId(),
                new TypeReference<>() {}, status().isOk()
        );

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).extracting(UserSubscriptionDTO::getUserId)
                .containsOnly(user1.getId());
    }

    // endregion

    // region getById

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldGetSubscriptionByIdSuccessfully() throws Exception {
        UserSubscriptionEntity entity = createSubscription(UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);

        UserSubscriptionDTO result = get(API + "/" + entity.getId(), new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
    }

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldFailToGetSubscriptionWhenNotFound() throws Exception {
        get(API + "/99999", status().isBadRequest());
    }

    // endregion

    // region cancel

    @Test
    @WithMockUser(authorities = "subscription:manage")
    void shouldCancelSubscriptionUserSubscriptionAsAdminSubscriptionSuccessfully() throws Exception {
        UserSubscriptionEntity entity = createSubscription(UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);

        UserSubscriptionDTO result = put(API + "/" + entity.getId() + "/cancel", null, UserSubscriptionDTO.class, status().isOk());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.CANCELLED);

        UserSubscriptionEntity updated = userSubscriptionRepository.findById(entity.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(UserSubscriptionStatus.CANCELLED);
    }

    @Test
    @WithMockUser(authorities = "subscription:manage")
    void shouldFailToCancelSubscriptionUserSubscriptionAsAdminNonExistentSubscription() throws Exception {
        put(API + "/99999/cancel", null, status().isBadRequest());
    }

    // endregion

    // region security

    @Test
    void shouldRejectUnauthenticatedGetUserSubscriptionAsAdmin() throws Exception {
        get(API, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void shouldForbidGetUserSubscriptionAsAdminForWrongPermission() throws Exception {
        get(API, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "subscription:read")
    void shouldForbidCancelSubscriptionUserSubscriptionAsAdminWithReadPermissionOnly() throws Exception {
        UserSubscriptionEntity entity = createSubscription(UserSubscriptionStatus.ACTIVE, SubscriptionBillingFrequency.MONTHLY);
        put(API + "/" + entity.getId() + "/cancel", null, status().isForbidden());
    }

    // endregion

    // region helpers

    private UserSubscriptionEntity createSubscription(UserSubscriptionStatus status, SubscriptionBillingFrequency frequency) {
        UserEntity user = createUser("sub-" + System.nanoTime() + "@test.com");
        return createSubscription(user.getId(), status, frequency);
    }

    private UserSubscriptionEntity createSubscription(Long userId, UserSubscriptionStatus status, SubscriptionBillingFrequency frequency) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = frequency == SubscriptionBillingFrequency.LIFETIME ? null :
                frequency == SubscriptionBillingFrequency.MONTHLY ? startDate.plusMonths(1) : startDate.plusYears(1);

        UserSubscriptionEntity entity = new UserSubscriptionEntity(
                null, userId, seedPlanId, "Test Plan", new BigDecimal("9.99"), null, null, BigDecimal.ZERO, BigDecimal.ZERO, CURRENCY_CODE,
                frequency, PAYMENT_MODE, "ext_" + System.nanoTime(), status,
                startDate, endDate, true
        );
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return userSubscriptionRepository.save(entity);
    }

    // endregion
}
