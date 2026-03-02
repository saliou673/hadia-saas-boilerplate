package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanPersistencePort {

    SubscriptionPlan save(SubscriptionPlan plan);

    Optional<SubscriptionPlan> findById(Long id);

    List<SubscriptionPlan> findAllActive();

    void remove(SubscriptionPlan plan);
}
