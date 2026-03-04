package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link SubscriptionPlanEntity}.
 */
@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanEntity, Long>, JpaSpecificationExecutor<SubscriptionPlanEntity> {
}
