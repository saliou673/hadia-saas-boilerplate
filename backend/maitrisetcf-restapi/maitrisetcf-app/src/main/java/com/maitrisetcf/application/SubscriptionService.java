package com.maitrisetcf.application;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.enumerations.DiscountType;
import com.maitrisetcf.domain.enumerations.SubscriptionBillingFrequency;
import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import com.maitrisetcf.domain.exceptions.*;
import com.maitrisetcf.domain.models.discountcode.DiscountCode;
import com.maitrisetcf.domain.models.subscription.PaymentRequest;
import com.maitrisetcf.domain.models.subscription.PaymentResult;
import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.ports.in.SubscribeUseCase;
import com.maitrisetcf.domain.ports.out.CurrentUserEmailPort;
import com.maitrisetcf.domain.ports.out.NotificationSenderPort;
import com.maitrisetcf.domain.ports.out.PaymentGatewayPort;
import com.maitrisetcf.domain.ports.out.persistenceport.*;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Application service implementing {@link SubscribeUseCase}: subscription lifecycle management.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService implements SubscribeUseCase {

    private final SubscriptionPlanPersistencePort subscriptionPlanPersistencePort;
    private final UserSubscriptionPersistencePort userSubscriptionPersistencePort;
    private final AppConfigurationPersistencePort appConfigurationPersistencePort;
    private final DiscountCodePersistencePort discountCodePersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final CurrentUserEmailPort currentUserEmailPort;
    private final NotificationSenderPort notificationSenderPort;
    private final List<PaymentGatewayPort> paymentGateways;

    @Override
    public UserSubscription subscribe(Long planId, String paymentMode, SubscriptionBillingFrequency billingFrequency, @Nullable String discountCode) {
        User currentUser = resolveCurrentUser();
        log.debug("Subscribing userId={} to planId={} via paymentMode={}, billingFrequency={}, discountCode={}",
                  currentUser.getId(), planId, paymentMode, billingFrequency, discountCode);

        SubscriptionPlan plan = subscriptionPlanPersistencePort.findById(planId)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found with id: " + planId));

        if (!plan.isActive()) {
            throw new SubscriptionPlanNotActiveException("Subscription plan '" + plan.getTitle() + "' is not active");
        }

        validatePaymentMode(paymentMode);

        if (userSubscriptionPersistencePort.existsByUserIdAndPlanIdAndStatus(currentUser.getId(), planId, UserSubscriptionStatus.ACTIVE)) {
            throw new ActiveSubscriptionAlreadyExistsException("You already have an active subscription for plan '" + plan.getTitle() + "'");
        }

        BigDecimal originalPrice = resolvePrice(plan, billingFrequency);
        AppliedDiscount appliedDiscount = applyDiscountIfPresent(plan, originalPrice, discountCode);
        PaymentResult result = processPayment(plan, appliedDiscount.finalPrice(), billingFrequency, currentUser.getId(), paymentMode);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = computeEndDate(billingFrequency, startDate, plan.getDurationDays());
        UserSubscriptionStatus status = result.isSuccess() ? UserSubscriptionStatus.ACTIVE : UserSubscriptionStatus.FAILED;

        UserSubscription subscription = UserSubscription.create(
                currentUser.getId(),
                planId,
                plan.getTitle(),
                appliedDiscount.finalPrice(),
                appliedDiscount.discountCodeUsed(),
                appliedDiscount.discountAmount(),
                plan.getCurrencyCode(),
                billingFrequency,
                paymentMode,
                result.getExternalPaymentId(),
                status,
                startDate,
                endDate,
                true
        );

        if (!result.isSuccess()) {
            log.warn("Payment failed for userId={}, planId={}: {}", currentUser.getId(), planId, result.getErrorMessage());
            notificationSenderPort.sendSubscriptionPaymentFailedNotification(currentUser, plan.getTitle());
            throw new PaymentProcessingException("Payment failed: " + result.getErrorMessage());
        }

        return userSubscriptionPersistencePort.save(subscription);
    }

    @Override
    public UserSubscription renew(Long subscriptionId) {
        User currentUser = resolveCurrentUser();
        log.debug("Renewing subscriptionId={} for userId={}", subscriptionId, currentUser.getId());

        UserSubscription existing = userSubscriptionPersistencePort.findById(subscriptionId)
                .orElseThrow(() -> new UserSubscriptionNotFoundException("Subscription not found with id: " + subscriptionId));

        if (!existing.getUserId().equals(currentUser.getId())) {
            throw new UserSubscriptionNotFoundException("Subscription not found with id: " + subscriptionId);
        }

        SubscriptionPlan plan = subscriptionPlanPersistencePort.findById(existing.getPlanId())
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found"));

        if (!plan.isActive()) {
            throw new SubscriptionPlanNotActiveException("Subscription plan '" + plan.getTitle() + "' is no longer active");
        }

        validatePaymentMode(existing.getPaymentMode());

        PaymentResult result = processPayment(plan, existing.getPricePaid(), existing.getBillingFrequency(), currentUser.getId(), existing.getPaymentMode());
        if (!result.isSuccess()) {
            throw new PaymentProcessingException("Renewal payment failed: " + result.getErrorMessage());
        }

        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = computeEndDate(existing.getBillingFrequency(), newStartDate, plan.getDurationDays());

        UserSubscription renewal = UserSubscription.create(
                currentUser.getId(),
                existing.getPlanId(),
                existing.getPlanTitle(),
                existing.getPricePaid(),
                existing.getDiscountCodeUsed(),
                existing.getDiscountAmount(),
                existing.getCurrencyCode(),
                existing.getBillingFrequency(),
                existing.getPaymentMode(),
                result.getExternalPaymentId(),
                UserSubscriptionStatus.ACTIVE,
                newStartDate,
                newEndDate,
                existing.isAutoRenew()
        );

        return userSubscriptionPersistencePort.save(renewal);
    }

    @Override
    public UserSubscription cancel(Long subscriptionId, boolean forceAdmin) {
        log.debug("Cancelling subscriptionId={}, forceAdmin={}", subscriptionId, forceAdmin);

        UserSubscription subscription = userSubscriptionPersistencePort.findById(subscriptionId)
                .orElseThrow(() -> new UserSubscriptionNotFoundException("Subscription not found with id: " + subscriptionId));

        if (!forceAdmin) {
            User currentUser = resolveCurrentUser();
            if (!subscription.getUserId().equals(currentUser.getId())) {
                throw new UserSubscriptionNotFoundException("Subscription not found with id: " + subscriptionId);
            }
        }

        subscription.cancel();
        return userSubscriptionPersistencePort.save(subscription);
    }

    @Override
    public UserSubscription getById(Long id) {
        return userSubscriptionPersistencePort.findById(id)
                .orElseThrow(() -> new UserSubscriptionNotFoundException("Subscription not found with id: " + id));
    }

    private BigDecimal resolvePrice(SubscriptionPlan plan, SubscriptionBillingFrequency billingFrequency) {
        BigDecimal price = switch (billingFrequency) {
            case MONTHLY -> plan.getMonthlyPrice();
            case YEARLY -> plan.getYearlyPrice();
            case LIFETIME -> plan.getLifetimePrice();
            case CUSTOM -> plan.getPrice();
        };
        if (price == null) {
            throw new SubscriptionBillingFrequencyNotSupportedException(
                    "Plan '" + plan.getTitle() + "' does not offer a " + billingFrequency.name().toLowerCase() + " billing cycle");
        }
        return price;
    }

    private AppliedDiscount applyDiscountIfPresent(SubscriptionPlan plan, BigDecimal originalPrice, @Nullable String discountCode) {
        if (StringUtils.isBlank(discountCode)) {
            return new AppliedDiscount(originalPrice, null, null);
        }

        DiscountCode code = discountCodePersistencePort.findByCode(discountCode)
                .orElseThrow(() -> new InvalidDiscountCodeException("Discount code is invalid"));
        validateDiscountCode(code, plan.getCurrencyCode());

        BigDecimal discountAmount = switch (code.getDiscountType()) {
            case PERCENTAGE ->
                    originalPrice.multiply(code.getDiscountValue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            case FIXED_AMOUNT -> code.getDiscountValue();
        };
        BigDecimal finalPrice = originalPrice.subtract(discountAmount).max(BigDecimal.ZERO);

        code.incrementUsage();
        discountCodePersistencePort.save(code);

        return new AppliedDiscount(finalPrice, code.getCode(), discountAmount);
    }

    private void validateDiscountCode(DiscountCode discountCode, String planCurrencyCode) {
        discountCode.validateForUse(LocalDate.now());
        if (discountCode.getDiscountType() == DiscountType.FIXED_AMOUNT && !planCurrencyCode.equals(discountCode.getCurrencyCode())) {
            throw new InvalidDiscountCodeException("Discount code currency does not match the plan currency");
        }
    }

    private User resolveCurrentUser() {
        String email = currentUserEmailPort.getCurrentUserEmail();
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    private void validatePaymentMode(String paymentMode) {
        if (!appConfigurationPersistencePort.existsActiveByCategoryAndCode(AppConfigurationCategory.PAYMENT_MODE, paymentMode)) {
            throw new InvalidPaymentModeException("Payment mode '" + paymentMode + "' is not a supported active payment mode");
        }
    }

    private PaymentResult processPayment(SubscriptionPlan plan,
                                         BigDecimal price,
                                         SubscriptionBillingFrequency billingFrequency,
                                         Long userId,
                                         String paymentMode) {
        Map<String, PaymentGatewayPort> gatewayMap = paymentGateways.stream()
                .collect(Collectors.toMap(PaymentGatewayPort::getModeCode, Function.identity()));

        PaymentGatewayPort gateway = gatewayMap.get(paymentMode);
        if (gateway == null) {
            throw new InvalidPaymentModeException("No payment gateway implementation found for mode: " + paymentMode);
        }

        PaymentRequest request = new PaymentRequest(price,
                                                    plan.getCurrencyCode(),
                                                    userId,
                                                    plan.getTitle(),
                                                    billingFrequency);
        return gateway.process(request);
    }

    private LocalDate computeEndDate(SubscriptionBillingFrequency frequency, LocalDate startDate, Integer durationDays) {
        return switch (frequency) {
            case MONTHLY -> startDate.plusMonths(1);
            case YEARLY -> startDate.plusYears(1);
            case LIFETIME -> null;
            case CUSTOM -> startDate.plusDays(durationDays);
        };
    }

    private record AppliedDiscount(BigDecimal finalPrice, @Nullable String discountCodeUsed,
                                   @Nullable BigDecimal discountAmount) {}
}
