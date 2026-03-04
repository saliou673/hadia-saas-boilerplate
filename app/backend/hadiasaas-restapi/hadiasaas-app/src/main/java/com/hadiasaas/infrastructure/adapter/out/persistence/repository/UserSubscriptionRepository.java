package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.domain.enumerations.UserSubscriptionStatus;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscriptionEntity, Long>,
                                                    JpaSpecificationExecutor<UserSubscriptionEntity> {

    Page<UserSubscriptionEntity> findByUserIdOrderByCreationDateDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndPlanIdAndStatus(Long userId, Long planId, UserSubscriptionStatus status);
}
