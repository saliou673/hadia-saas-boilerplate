package com.maitrisetcf.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maitrisetcf.domain.enumerations.*;
import com.maitrisetcf.domain.models.subscription.PaymentResult;
import com.maitrisetcf.domain.ports.out.NotificationSenderPort;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.UserSubscriptionDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.SubscribeRequest;
import com.maitrisetcf.infrastructure.adapter.out.payment.StripePaymentGatewayAdapter;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.DiscountCodeRepository;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.UserSubscriptionRepository;
import com.maitrisetcf.infrastructure.adapter.out.query.PaginatedResult;
import com.maitrisetcf.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class UserSubscriptionControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/subscriptions";
    private static final String CURRENCY_CODE = "EUR";
    private static final String PAYMENT_MODE = "STRIPE";

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private AppConfigurationRepository appConfigurationRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @MockitoBean
    private NotificationSenderPort notificationSenderPort;

    @MockitoBean
    private StripePaymentGatewayAdapter stripePaymentGatewayAdapter;

    @BeforeEach
    void seedData() {
        createCurrency(CURRENCY_CODE);
        createPaymentMode(PAYMENT_MODE);
        when(stripePaymentGatewayAdapter.getModeCode()).thenReturn(PAYMENT_MODE);
        when(stripePaymentGatewayAdapter.process(any())).thenReturn(PaymentResult.success("stripe_test_payment"));
    }

    // region subscribe

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeMonthlySuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Multi Plan", new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getPlanId()).isEqualTo(plan.getId());
        assertThat(result.getPlanTitle()).isEqualTo("Multi Plan");
        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.MONTHLY);
        assertThat(result.getPaymentMode()).isEqualTo(PAYMENT_MODE);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(result.isAutoRenew()).isTrue();
        assertThat(result.getExternalPaymentId()).isNotBlank();
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeYearlySuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Multi Plan", new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.YEARLY, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.YEARLY);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusYears(1));
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeLifetimeSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Full Plan", null, null, new BigDecimal("299.99"), null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.LIFETIME, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.LIFETIME);
        assertThat(result.getEndDate()).isNull();
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeCustomCycleSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("14-Day Trial", null, null, null, new BigDecimal("4.99"), 14, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.CUSTOM, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.CUSTOM);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusDays(14));
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeWithPaypalSuccessfully() throws Exception {
        createDefaultUser();
        createPaymentMode("PAYPAL");
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), "PAYPAL", SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getPaymentMode()).isEqualTo("PAYPAL");
        assertThat(result.getExternalPaymentId()).startsWith("paypal_");
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToInactivePlan() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Inactive Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, false, SubscriptionPlanType.ONLINE_TRAINING);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeWhenBillingFrequencyNotSupported() throws Exception {
        createDefaultUser();
        // Plan only offers monthly; requesting YEARLY should fail
        SubscriptionPlanEntity plan = createPlan("Monthly Only", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.YEARLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeCustomWhenNoPriceSet() throws Exception {
        createDefaultUser();
        // Plan has no custom price
        SubscriptionPlanEntity plan = createPlan("Monthly Only", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.CUSTOM, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeWithInvalidPaymentMode() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        post(API, new SubscribeRequest(plan.getId(), "BITCOIN", SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToNonExistentPlan() throws Exception {
        createDefaultUser();

        post(API, new SubscribeRequest(99999L, PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeWhenAlreadyActiveSubscription() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        SubscribeRequest request = new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null);
        post(API, request, UserSubscriptionDTO.class, status().isCreated());
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldNotifyUserWhenSubscriptionPaymentFails() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        when(stripePaymentGatewayAdapter.process(any())).thenReturn(PaymentResult.failure("Card declined"));

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());

        verify(notificationSenderPort).sendSubscriptionPaymentFailedNotification(any(), eq("Monthly Plan"));
        assertThat(userSubscriptionRepository.count()).isZero();
    }

    @Test
    void shouldRejectUnauthenticatedSubscribe() throws Exception {
        post(API, new SubscribeRequest(1L, PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeWithPercentageDiscountSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Discounted Plan", new BigDecimal("100.00"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        DiscountCodeEntity discountCode = createDiscountCode("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, LocalDate.now().plusDays(5), 10, 0);

        UserSubscriptionDTO result = post(
                API,
                new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, "WELCOME10"),
                UserSubscriptionDTO.class,
                status().isCreated()
        );

        assertThat(result.getPricePaid()).isEqualByComparingTo("90.00");
        assertThat(result.getDiscountCodeUsed()).isEqualTo("WELCOME10");
        assertThat(result.getDiscountAmount()).isEqualByComparingTo("10.00");
        assertThat(discountCodeRepository.findById(discountCode.getId()).orElseThrow().getUsageCount()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeWhenFixedDiscountCurrencyDoesNotMatch() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("EUR Plan", new BigDecimal("50.00"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        createCurrency("USD");
        createDiscountCode("SAVE15USD", DiscountType.FIXED_AMOUNT, new BigDecimal("15.00"), "USD", true, LocalDate.now().plusDays(5), 10, 0);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, "SAVE15USD"), status().isBadRequest());
    }

    // endregion

    // region getMySubscriptions

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldReturnMySubscriptions() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan1 = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        SubscriptionPlanEntity plan2 = createPlan("Yearly Plan", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        post(API, new SubscribeRequest(plan1.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());
        post(API, new SubscribeRequest(plan2.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.YEARLY, null), UserSubscriptionDTO.class, status().isCreated());

        PaginatedResult<UserSubscriptionDTO> result = get(API + "/me", new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldReturnEmptyListWhenNoSubscriptions() throws Exception {
        createDefaultUser();

        PaginatedResult<UserSubscriptionDTO> result = get(API + "/me", new TypeReference<>() {}, status().isOk());

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void shouldRejectUnauthenticatedGetMySubscriptions() throws Exception {
        get(API + "/me", status().isUnauthorized());
    }

    // endregion

    // region renew

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldRenewSubscriptionSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO original = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());
        UserSubscriptionDTO renewed = post(API + "/" + original.getId() + "/renew", null, UserSubscriptionDTO.class, status().isCreated());

        assertThat(renewed.getId()).isNotEqualTo(original.getId());
        assertThat(renewed.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(renewed.getPlanId()).isEqualTo(plan.getId());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToRenewNonOwnedSubscription() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);
        UserSubscriptionEntity other = createSubscriptionDirectly(createUser("other@sub-test.com").getId(), plan.getId(), SubscriptionBillingFrequency.MONTHLY);

        post(API + "/" + other.getId() + "/renew", null, status().isBadRequest());
    }

    // endregion

    // region cancel

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldCancelSubscriptionSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true, SubscriptionPlanType.ONLINE_TRAINING);

        UserSubscriptionDTO original = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());
        UserSubscriptionDTO cancelled = put(API + "/" + original.getId() + "/cancel", null, UserSubscriptionDTO.class, status().isOk());

        assertThat(cancelled.getStatus()).isEqualTo(UserSubscriptionStatus.CANCELLED);
        assertThat(userSubscriptionRepository.findById(original.getId()).orElseThrow().getStatus())
                .isEqualTo(UserSubscriptionStatus.CANCELLED);
    }

    // endregion

    // region helpers

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

    private void createPaymentMode(String code) {
        AppConfigurationEntity entity = new AppConfigurationEntity(null, AppConfigurationCategory.PAYMENT_MODE, code, code, null, true);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        appConfigurationRepository.save(entity);
    }

    private UserSubscriptionEntity createSubscriptionDirectly(Long userId, Long planId, SubscriptionBillingFrequency frequency) {
        UserSubscriptionEntity entity = new UserSubscriptionEntity(
                null, userId, planId, "Some Plan", new BigDecimal("9.99"), null, null, CURRENCY_CODE,
                frequency, PAYMENT_MODE, "ext_123", UserSubscriptionStatus.ACTIVE,
                LocalDate.now(), LocalDate.now().plusMonths(1), true
        );
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return userSubscriptionRepository.save(entity);
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

    // endregion
}
