package com.maitrisetcf.infrastructure.adapter.out.persistence;

import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import com.maitrisetcf.domain.models.subscription.UserSubscription;
import com.maitrisetcf.domain.ports.out.persistenceport.UserSubscriptionPersistencePort;
import com.maitrisetcf.infrastructure.adapter.out.persistence.mapper.UserSubscriptionMapper;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing {@link UserSubscriptionPersistencePort}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserSubscriptionPersistenceAdapter implements UserSubscriptionPersistencePort {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserSubscriptionMapper userSubscriptionMapper;

    @Override
    public UserSubscription save(UserSubscription subscription) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> userSubscriptionMapper.toDomain(userSubscriptionRepository.save(userSubscriptionMapper.toEntity(subscription))),
                "Error saving user subscription for userId=" + subscription.getUserId()
        );
    }

    @Override
    public Optional<UserSubscription> findById(Long id) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> userSubscriptionRepository.findById(id).map(userSubscriptionMapper::toDomain),
                "Error fetching user subscription by id=" + id
        );
    }

    @Override
    public List<UserSubscription> findByUserId(Long userId) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> userSubscriptionRepository.findByUserIdOrderByCreationDateDesc(userId)
                        .stream()
                        .map(userSubscriptionMapper::toDomain)
                        .toList(),
                "Error fetching subscriptions for userId=" + userId
        );
    }

    @Override
    public boolean existsByUserIdAndPlanIdAndStatus(Long userId, Long planId, UserSubscriptionStatus status) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> userSubscriptionRepository.existsByUserIdAndPlanIdAndStatus(userId, planId, status),
                "Error checking active subscription for userId=" + userId + ", planId=" + planId
        );
    }

    @Override
    public void remove(UserSubscription subscription) {
        AdapterPersistenceUtils.executeDbOperation(
                () -> userSubscriptionRepository.delete(userSubscriptionMapper.toEntity(subscription)),
                "Error removing user subscription with id=" + subscription.getId()
        );
    }
}
