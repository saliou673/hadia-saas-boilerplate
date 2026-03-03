package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link SubscriptionPlanEntity}.
 */
@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanEntity, Long>, JpaSpecificationExecutor<SubscriptionPlanEntity> {

    List<SubscriptionPlanEntity> findAllByActiveTrueOrderByMonthlyPriceAsc();
}
