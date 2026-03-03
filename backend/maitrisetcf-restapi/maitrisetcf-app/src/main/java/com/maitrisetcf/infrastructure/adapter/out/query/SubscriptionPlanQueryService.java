package com.maitrisetcf.infrastructure.adapter.out.query;

import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlan;
import com.maitrisetcf.domain.models.subscriptionplan.SubscriptionPlanFilter;
import com.maitrisetcf.domain.ports.in.SubscriptionPlanQueryUseCase;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.SubscriptionPlanEntity_;
import com.maitrisetcf.infrastructure.adapter.out.persistence.mapper.SubscriptionPlanMapper;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Query service implementing {@link SubscriptionPlanQueryUseCase} with JPA Specification-based filtering.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionPlanQueryService extends QueryService<SubscriptionPlanEntity> implements SubscriptionPlanQueryUseCase {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    @Override
    public PagedResult<SubscriptionPlan> findAll(SubscriptionPlanFilter filter, int page, int size) {
        log.debug("Finding subscription plans by filter: {}", filter);
        Page<SubscriptionPlanEntity> entityPage = subscriptionPlanRepository.findAll(
                createSpecification(filter),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "monthlyPrice"))
        );
        List<SubscriptionPlan> items = entityPage.getContent().stream().map(subscriptionPlanMapper::toDomain).toList();
        return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public List<SubscriptionPlan> findAllActive() {
        log.debug("Finding all active subscription plans");
        return subscriptionPlanRepository.findAllByActiveTrueOrderByMonthlyPriceAsc()
                .stream()
                .map(subscriptionPlanMapper::toDomain)
                .toList();
    }

    @Override
    public long count(SubscriptionPlanFilter filter) {
        log.debug("Counting subscription plans by filter: {}", filter);
        return subscriptionPlanRepository.count(createSpecification(filter));
    }

    private Specification<SubscriptionPlanEntity> createSpecification(SubscriptionPlanFilter filter) {
        Specification<SubscriptionPlanEntity> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.getId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getId(), SubscriptionPlanEntity_.id));
        }

        if (filter.getTitle() != null) {
            spec = spec.and(buildStringSpecification(filter.getTitle(), SubscriptionPlanEntity_.title));
        }

        if (filter.getCurrencyCode() != null) {
            spec = spec.and(buildStringSpecification(filter.getCurrencyCode(), SubscriptionPlanEntity_.currencyCode));
        }

        if (filter.getActive() != null) {
            spec = spec.and(buildSpecification(filter.getActive(), SubscriptionPlanEntity_.active));
        }

        if (filter.getType() != null) {
            spec = spec.and(buildEnumSpecification(filter.getType(), SubscriptionPlanEntity_.type));
        }

        spec = addAuditFieldsSpecifications(
                spec,
                filter,
                SubscriptionPlanEntity_.creationDate,
                SubscriptionPlanEntity_.lastUpdateDate,
                SubscriptionPlanEntity_.lastUpdatedBy
        );

        return spec;
    }
}
