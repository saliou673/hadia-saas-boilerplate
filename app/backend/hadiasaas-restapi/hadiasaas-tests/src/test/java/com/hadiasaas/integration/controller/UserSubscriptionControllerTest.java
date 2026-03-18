package com.hadiasaas.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hadiasaas.domain.enumerations.*;
import com.hadiasaas.domain.models.subscription.PaymentResult;
import com.hadiasaas.domain.ports.out.NotificationSenderPort;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.dto.UserSubscriptionDTO;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.SubscribeRequest;
import com.hadiasaas.infrastructure.adapter.out.payment.StripePaymentGatewayAdapter;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.AppConfigurationEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.EmbeddableUserInfo;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.EnterpriseProfileEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.StorageSettingsEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TaxConfigurationEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.AppConfigurationRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.DiscountCodeRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.EnterpriseProfileRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.StorageSettingsRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.TaxConfigurationRepository;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.UserSubscriptionRepository;
import com.hadiasaas.infrastructure.adapter.out.query.PaginatedResult;
import com.hadiasaas.integration.IntegrationTest;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    private TaxConfigurationRepository taxConfigurationRepository;

    @Autowired
    private StorageSettingsRepository storageSettingsRepository;

    @Autowired
    private EnterpriseProfileRepository enterpriseProfileRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @MockitoBean
    private NotificationSenderPort notificationSenderPort;

    @MockitoBean
    private StripePaymentGatewayAdapter stripePaymentGatewayAdapter;

    @MockitoBean
    private S3Client s3Client;

    @BeforeEach
    void cleanUploadDirectory() throws IOException {
        Path uploadDir = Paths.get("./test-uploads");
        if (!Files.exists(uploadDir)) {
            return;
        }

        try (Stream<Path> stream = Files.walk(uploadDir)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }

    @BeforeEach
    void seedData() {
        createCurrency(CURRENCY_CODE);
        createPaymentMode(PAYMENT_MODE);
        createEnterpriseProfile();
        createTaxConfiguration(new BigDecimal("0.20"));
        when(stripePaymentGatewayAdapter.getModeCode()).thenReturn(PAYMENT_MODE);
        when(stripePaymentGatewayAdapter.process(any())).thenReturn(PaymentResult.success("stripe_test_payment"));
        when(s3Client.putObject(org.mockito.ArgumentMatchers.<PutObjectRequest>any(), org.mockito.ArgumentMatchers.<RequestBody>any()))
                .thenReturn(PutObjectResponse.builder().build());
    }

    // region subscribe

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeToPlanMonthlySuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Multi Plan", new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null, CURRENCY_CODE, true);

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
        assertThat(result.getTaxRate()).isEqualByComparingTo("20");
        assertThat(result.getTaxAmount()).isEqualByComparingTo("2.00");
        assertThat(result.getPricePaid()).isEqualByComparingTo("11.99");
        Path billPath = assertAndGetGeneratedBill();
        assertThat(extractPdfText(billPath))
                .contains("reçu")
                .contains("numéro de facture")
                .contains("moyen de paiement")
                .contains("carte bancaire");
        verify(notificationSenderPort).sendSubscriptionPaymentSucceededNotification(any(), any(), argThat(path -> path.startsWith("bills/") && path.endsWith(".pdf")));
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldGenerateBillInEnglishWhenUserLanguageKeyIsNull() throws Exception {
        UserEntity user = createDefaultUser();
        updateUserLanguage(user, null);
        SubscriptionPlanEntity plan = createPlan("Multi Plan", new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null, CURRENCY_CODE, true);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());

        Path billPath = assertAndGetGeneratedBill();
        assertThat(extractPdfText(billPath))
                .contains("receipt")
                .contains("invoice number")
                .contains("payment method")
                .contains("bank card");
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldStoreBillInAwsWhenAwsStrategyIsConfigured() throws Exception {
        createDefaultUser();
        createStorageSettings(StorageProvider.AWS_S3, "test-bucket", true);
        SubscriptionPlanEntity plan = createPlan("Multi Plan", new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null, CURRENCY_CODE, true);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());

        verify(s3Client).putObject(
                org.mockito.ArgumentMatchers.<PutObjectRequest>argThat(request -> "test-bucket".equals(request.bucket()) && request.key().startsWith("bills/") && request.key().endsWith(".pdf")),
                org.mockito.ArgumentMatchers.<RequestBody>any()
        );
        assertThat(Paths.get("./test-uploads/bills")).doesNotExist();
        verify(notificationSenderPort).sendSubscriptionPaymentSucceededNotification(any(), any(), argThat(path -> path.startsWith("bills/") && path.endsWith(".pdf")));
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeToPlanYearlySuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Multi Plan", new BigDecimal("9.99"), new BigDecimal("89.99"), null, null, null, CURRENCY_CODE, true);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.YEARLY, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.YEARLY);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusYears(1));
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldApplyZeroTaxWhenTaxConfigurationIsMissing() throws Exception {
        createDefaultUser();
        taxConfigurationRepository.deleteAll();
        SubscriptionPlanEntity plan = createPlan("No Tax Plan", new BigDecimal("10.00"), null, null, null, null, CURRENCY_CODE, true);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getTaxRate()).isEqualByComparingTo("0");
        assertThat(result.getTaxAmount()).isEqualByComparingTo("0.00");
        assertThat(result.getPricePaid()).isEqualByComparingTo("10.00");
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeToPlanLifetimeSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Full Plan", null, null, new BigDecimal("299.99"), null, null, CURRENCY_CODE, true);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.LIFETIME, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.LIFETIME);
        assertThat(result.getEndDate()).isNull();
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeToPlanCustomCycleSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("14-Day Trial", null, null, null, new BigDecimal("4.99"), 14, CURRENCY_CODE, true);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.CUSTOM, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(result.getBillingFrequency()).isEqualTo(SubscriptionBillingFrequency.CUSTOM);
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusDays(14));
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeToPlanWithPaypalSuccessfully() throws Exception {
        createDefaultUser();
        createPaymentMode("PAYPAL");
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        UserSubscriptionDTO result = post(API, new SubscribeRequest(plan.getId(), "PAYPAL", SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());

        assertThat(result.getPaymentMode()).isEqualTo("PAYPAL");
        assertThat(result.getExternalPaymentId()).startsWith("paypal_");
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanToInactivePlan() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Inactive Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, false);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanWhenBillingFrequencyNotSupported() throws Exception {
        createDefaultUser();
        // Plan only offers monthly; requesting YEARLY should fail
        SubscriptionPlanEntity plan = createPlan("Monthly Only", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.YEARLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanCustomWhenNoPriceSet() throws Exception {
        createDefaultUser();
        // Plan has no custom price
        SubscriptionPlanEntity plan = createPlan("Monthly Only", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.CUSTOM, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanWithInvalidPaymentMode() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        post(API, new SubscribeRequest(plan.getId(), "BITCOIN", SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanToNonExistentPlan() throws Exception {
        createDefaultUser();

        post(API, new SubscribeRequest(99999L, PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanWhenAlreadyActiveSubscription() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        SubscribeRequest request = new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null);
        post(API, request, UserSubscriptionDTO.class, status().isCreated());
        post(API, request, status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldNotifyUserWhenSubscriptionPaymentFails() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);
        when(stripePaymentGatewayAdapter.process(any())).thenReturn(PaymentResult.failure("Card declined"));

        post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isBadRequest());

        verify(notificationSenderPort).sendSubscriptionPaymentFailedNotification(any(), eq("Monthly Plan"));
        assertThat(userSubscriptionRepository.count()).isZero();
    }

    @Test
    void shouldRejectUnauthenticatedSubscribeToPlan() throws Exception {
        post(API, new SubscribeRequest(1L, PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldSubscribeToPlanWithPercentageDiscountSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Discounted Plan", new BigDecimal("100.00"), null, null, null, null, CURRENCY_CODE, true);
        DiscountCodeEntity discountCode = createDiscountCode("WELCOME10", DiscountType.PERCENTAGE, new BigDecimal("10.00"), null, true, LocalDate.now().plusDays(5), 10, 0);

        UserSubscriptionDTO result = post(
                API,
                new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, "WELCOME10"),
                UserSubscriptionDTO.class,
                status().isCreated()
        );

        assertThat(result.getPricePaid()).isEqualByComparingTo("108.00");
        assertThat(result.getDiscountCodeUsed()).isEqualTo("WELCOME10");
        assertThat(result.getDiscountAmount()).isEqualByComparingTo("10.00");
        assertThat(result.getTaxRate()).isEqualByComparingTo("20");
        assertThat(result.getTaxAmount()).isEqualByComparingTo("18.00");
        assertThat(discountCodeRepository.findById(discountCode.getId()).orElseThrow().getUsageCount()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToSubscribeToPlanWhenFixedDiscountCurrencyDoesNotMatch() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("EUR Plan", new BigDecimal("50.00"), null, null, null, null, CURRENCY_CODE, true);
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
        SubscriptionPlanEntity plan1 = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);
        SubscriptionPlanEntity plan2 = createPlan("Yearly Plan", null, new BigDecimal("99.99"), null, null, null, CURRENCY_CODE, true);

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
    void shouldRenewSubscriptionSubscriptionSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        UserSubscriptionDTO original = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());
        UserSubscriptionDTO renewed = post(API + "/" + original.getId() + "/renew", null, UserSubscriptionDTO.class, status().isCreated());

        assertThat(renewed.getId()).isNotEqualTo(original.getId());
        assertThat(renewed.getStatus()).isEqualTo(UserSubscriptionStatus.ACTIVE);
        assertThat(renewed.getPlanId()).isEqualTo(plan.getId());
    }

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldFailToRenewSubscriptionNonOwnedSubscription() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);
        UserSubscriptionEntity other = createSubscriptionDirectly(createUser("other@sub-test.com").getId(), plan.getId(), SubscriptionBillingFrequency.MONTHLY);

        post(API + "/" + other.getId() + "/renew", null, status().isBadRequest());
    }

    // endregion

    // region cancel

    @Test
    @WithMockUser(username = DEFAULT_USER_EMAIL)
    void shouldCancelSubscriptionSubscriptionSuccessfully() throws Exception {
        createDefaultUser();
        SubscriptionPlanEntity plan = createPlan("Monthly Plan", new BigDecimal("9.99"), null, null, null, null, CURRENCY_CODE, true);

        UserSubscriptionDTO original = post(API, new SubscribeRequest(plan.getId(), PAYMENT_MODE, SubscriptionBillingFrequency.MONTHLY, null), UserSubscriptionDTO.class, status().isCreated());
        UserSubscriptionDTO cancelled = put(API + "/" + original.getId() + "/cancel", null, UserSubscriptionDTO.class, status().isOk());

        assertThat(cancelled.getStatus()).isEqualTo(UserSubscriptionStatus.CANCELLED);
        assertThat(userSubscriptionRepository.findById(original.getId()).orElseThrow().getStatus())
                .isEqualTo(UserSubscriptionStatus.CANCELLED);
    }

    // endregion

    // region helpers

    // monthlyPrice, yearlyPrice, lifetimePrice, price (custom), durationDays (custom)
    private SubscriptionPlanEntity createPlan(String title, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, boolean active) {
        SubscriptionPlanEntity entity = new SubscriptionPlanEntity(null, title, null, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, List.of(), active);
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

    private void createStorageSettings(StorageProvider provider, String bucketName, boolean active) {
        StorageSettingsEntity entity = new StorageSettingsEntity(null, provider, bucketName, null, null, active);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        storageSettingsRepository.save(entity);
    }

    private void createEnterpriseProfile() {
        EnterpriseProfileEntity entity = new EnterpriseProfileEntity(
                null, "Hadia SaaS", null, null, null,
                "10 Rue de Paris, 75000 Paris", null, null, null, null,
                "+33 1 23 45 67 89", "contact@hadiasaas.com", null, null
        );
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        enterpriseProfileRepository.save(entity);
    }

    private void createTaxConfiguration(BigDecimal rate) {
        TaxConfigurationEntity entity = new TaxConfigurationEntity(null, "VAT", "VAT", rate, null, true);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        taxConfigurationRepository.save(entity);
    }

    private void updateUserLanguage(UserEntity user, String languageKey) {
        EmbeddableUserInfo userInfo = user.getUserInfo();
        userInfo.setLanguageKey(languageKey);
        user.setUserInfo(userInfo);
        userRepository.save(user);
    }

    private Path assertAndGetGeneratedBill() throws IOException {
        Path billsDirectory = Paths.get("./test-uploads/bills");
        assertThat(billsDirectory).exists().isDirectory();
        try (var billFiles = Files.list(billsDirectory)) {
            Path billPath = billFiles.findFirst().orElseThrow();
            assertThat(billPath.getFileName().toString()).endsWith(".pdf");
            byte[] header = Files.readAllBytes(billPath);
            assertThat(new String(header, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
            return billPath;
        }
    }

    private String extractPdfText(Path billPath) throws IOException {
        try (PDDocument document = Loader.loadPDF(billPath.toFile())) {
            return new PDFTextStripper().getText(document)
                    .replace('\u00A0', ' ')
                    .replace("\r", "")
                    .toLowerCase(Locale.ROOT);
        }
    }

    private UserSubscriptionEntity createSubscriptionDirectly(Long userId, Long planId, SubscriptionBillingFrequency frequency) {
        UserSubscriptionEntity entity = new UserSubscriptionEntity(
                null, userId, planId, "Some Plan", new BigDecimal("9.99"), null, null, BigDecimal.ZERO, BigDecimal.ZERO, CURRENCY_CODE,
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
