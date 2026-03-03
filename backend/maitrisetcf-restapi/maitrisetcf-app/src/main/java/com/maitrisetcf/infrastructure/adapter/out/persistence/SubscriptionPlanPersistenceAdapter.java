package com.maitrisetcf.infrastructure.adapter.out.persistence;

import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.ports.out.persistenceport.SubscriptionPlanPersistencePort;
import com.maitrisetcf.infrastructure.adapter.out.persistence.mapper.SubscriptionPlanMapper;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing {@link SubscriptionPlanPersistencePort}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionPlanPersistenceAdapter implements SubscriptionPlanPersistencePort {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    @Override
    public SubscriptionPlan save(SubscriptionPlan plan) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> subscriptionPlanMapper.toDomain(subscriptionPlanRepository.save(subscriptionPlanMapper.toEntity(plan))),
                "Error saving subscription plan: " + plan.getTitle()
        );
    }

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> subscriptionPlanRepository.findById(id).map(subscriptionPlanMapper::toDomain),
                "Error fetching subscription plan by id: " + id
        );
    }

    @Override
    public List<SubscriptionPlan> findAllActive() {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> subscriptionPlanRepository.findAllByActiveTrueOrderByMonthlyPriceAsc()
                        .stream()
                        .map(subscriptionPlanMapper::toDomain)
                        .toList(),
                "Error fetching active subscription plans"
        );
    }

    @Override
    public void remove(SubscriptionPlan plan) {
        AdapterPersistenceUtils.executeDbOperation(
                () -> subscriptionPlanRepository.delete(subscriptionPlanMapper.toEntity(plan)),
                "Error removing subscription plan with id: " + plan.getId()
        );
    }
}
