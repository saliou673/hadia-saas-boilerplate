package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;

import java.math.BigDecimal;
import java.util.List;

public interface SubscriptionPlanUseCase {

    SubscriptionPlan create(String title, String description, BigDecimal price, String currencyCode, List<String> features, int durationDays, boolean active, SubscriptionPlanType type);

    SubscriptionPlan update(Long id, String title, String description, BigDecimal price, String currencyCode, List<String> features, int durationDays, boolean active, SubscriptionPlanType type);

    void delete(Long id);

    SubscriptionPlan getById(Long id);
}
