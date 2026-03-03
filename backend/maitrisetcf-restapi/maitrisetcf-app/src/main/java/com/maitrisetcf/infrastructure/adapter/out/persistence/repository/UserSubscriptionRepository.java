package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.domain.enumerations.UserSubscriptionStatus;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscriptionEntity, Long>,
                                                    JpaSpecificationExecutor<UserSubscriptionEntity> {

    List<UserSubscriptionEntity> findByUserIdOrderByCreationDateDesc(Long userId);

    boolean existsByUserIdAndPlanIdAndStatus(Long userId, Long planId, UserSubscriptionStatus status);
}
