package com.hadiasaas.application;

import com.hadiasaas.domain.enumerations.AppConfigurationCategory;
import com.hadiasaas.domain.exceptions.InvalidCurrencyException;
import com.hadiasaas.domain.exceptions.RequiredFieldException;
import com.hadiasaas.domain.exceptions.SubscriptionPlanNotFoundException;
import com.hadiasaas.domain.models.subscriptionplan.SubscriptionPlan;
import com.hadiasaas.domain.ports.in.SubscriptionPlanUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.AppConfigurationPersistencePort;
import com.hadiasaas.domain.ports.out.persistenceport.SubscriptionPlanPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link SubscriptionPlanUseCase}: CRUD for subscription plans with currency validation. */
public class SubscriptionPlanService implements SubscriptionPlanUseCase {

    private final SubscriptionPlanPersistencePort subscriptionPlanPersistencePort;
    private final AppConfigurationPersistencePort appConfigurationPersistencePort;

    @Override
    public SubscriptionPlan create(String title, String description, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, List<String> features, boolean active) {
        log.debug("Creating subscription plan: title={}, currencyCode={}", title, currencyCode);
        validatePrices(monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays);
        validateCurrencyCode(currencyCode);
        SubscriptionPlan plan = SubscriptionPlan.create(title, description, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, features, active);
        return subscriptionPlanPersistencePort.save(plan);
    }

    @Override
    public SubscriptionPlan update(Long id, String title, String description, BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays, String currencyCode, List<String> features, boolean active) {
        log.debug("Updating subscription plan id={}", id);
        SubscriptionPlan plan = subscriptionPlanPersistencePort.findById(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found with id: " + id));
        validatePrices(monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays);
        validateCurrencyCode(currencyCode);
        plan.update(title, description, monthlyPrice, yearlyPrice, lifetimePrice, price, durationDays, currencyCode, features, active);
        return subscriptionPlanPersistencePort.save(plan);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting subscription plan id={}", id);
        SubscriptionPlan plan = subscriptionPlanPersistencePort.findById(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found with id: " + id));
        subscriptionPlanPersistencePort.remove(plan);
    }

    @Override
    public SubscriptionPlan getById(Long id) {
        return subscriptionPlanPersistencePort.findById(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found with id: " + id));
    }

    private void validatePrices(BigDecimal monthlyPrice, BigDecimal yearlyPrice, BigDecimal lifetimePrice, BigDecimal price, Integer durationDays) {
        if (monthlyPrice == null && yearlyPrice == null && lifetimePrice == null && price == null) {
            throw new RequiredFieldException("At least one price (monthlyPrice, yearlyPrice, lifetimePrice, or price) must be provided");
        }
        if (price != null && durationDays == null) {
            throw new RequiredFieldException("durationDays is required when a custom price is provided");
        }
        if (durationDays != null && price == null) {
            throw new RequiredFieldException("price is required when durationDays is provided");
        }
    }

    private void validateCurrencyCode(String currencyCode) {
        if (!appConfigurationPersistencePort.existsActiveByCategoryAndCode(AppConfigurationCategory.CURRENCY, currencyCode)) {
            throw new InvalidCurrencyException("Currency code '" + currencyCode + "' is not a valid active currency");
        }
    }
}
