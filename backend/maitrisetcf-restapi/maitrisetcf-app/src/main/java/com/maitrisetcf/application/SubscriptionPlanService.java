package com.maitrisetcf.application;

import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.domain.exceptions.InvalidCurrencyException;
import com.maitrisetcf.domain.exceptions.SubscriptionPlanNotFoundException;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.ports.in.SubscriptionPlanUseCase;
import com.maitrisetcf.domain.ports.out.persistenceport.AppConfigurationPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.SubscriptionPlanPersistencePort;
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
public class SubscriptionPlanService implements SubscriptionPlanUseCase {

    private final SubscriptionPlanPersistencePort subscriptionPlanPersistencePort;
    private final AppConfigurationPersistencePort appConfigurationPersistencePort;

    @Override
    public SubscriptionPlan create(String title, String description, BigDecimal price, String currencyCode, List<String> features, int durationDays, boolean active, SubscriptionPlanType type) {
        log.debug("Creating subscription plan: title={}, type={}, currencyCode={}", title, type, currencyCode);
        validateCurrencyCode(currencyCode);
        SubscriptionPlan plan = SubscriptionPlan.create(title, description, price, currencyCode, features, durationDays, active, type);
        return subscriptionPlanPersistencePort.save(plan);
    }

    @Override
    public SubscriptionPlan update(Long id, String title, String description, BigDecimal price, String currencyCode, List<String> features, int durationDays, boolean active, SubscriptionPlanType type) {
        log.debug("Updating subscription plan id={}", id);
        SubscriptionPlan plan = subscriptionPlanPersistencePort.findById(id)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found with id: " + id));
        validateCurrencyCode(currencyCode);
        plan.update(title, description, price, currencyCode, features, durationDays, active, type);
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

    private void validateCurrencyCode(String currencyCode) {
        if (!appConfigurationPersistencePort.existsActiveByCategoryAndCode(AppConfigurationCategory.CURRENCY, currencyCode)) {
            throw new InvalidCurrencyException("Currency code '" + currencyCode + "' is not a valid active currency");
        }
    }
}
