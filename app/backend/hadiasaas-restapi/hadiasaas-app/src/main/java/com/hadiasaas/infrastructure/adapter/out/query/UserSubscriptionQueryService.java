package com.hadiasaas.infrastructure.adapter.out.query;

import com.hadiasaas.domain.exceptions.UserNotFoundException;
import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.subscription.UserSubscription;
import com.hadiasaas.domain.models.subscription.UserSubscriptionFilter;
import com.hadiasaas.domain.models.user.User;
import com.hadiasaas.domain.ports.in.UserSubscriptionQueryUseCase;
import com.hadiasaas.domain.ports.out.CurrentUserEmailPort;
import com.hadiasaas.domain.ports.out.persistenceport.UserPersistencePort;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserSubscriptionEntity_;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.UserSubscriptionMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.UserSubscriptionRepository;
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
 * Query service implementing {@link UserSubscriptionQueryUseCase} with JPA Specification-based filtering.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSubscriptionQueryService extends QueryService<UserSubscriptionEntity> implements UserSubscriptionQueryUseCase {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserSubscriptionMapper userSubscriptionMapper;
    private final CurrentUserEmailPort currentUserEmailPort;
    private final UserPersistencePort userPersistencePort;

    @Override
    public PagedResult<UserSubscription> findAll(UserSubscriptionFilter filter, int page, int size) {
        log.debug("Finding user subscriptions by filter: {}", filter);
        Page<UserSubscriptionEntity> entityPage = userSubscriptionRepository.findAll(
                createSpecification(filter),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"))
        );
        List<UserSubscription> items = entityPage.getContent().stream().map(userSubscriptionMapper::toDomain).toList();
        return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public PagedResult<UserSubscription> findMySubscriptions(int page, int size) {
        String email = currentUserEmailPort.getCurrentUserEmail();
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        log.debug("Finding subscriptions for userId={}", user.getId());
        Page<UserSubscriptionEntity> entityPage = userSubscriptionRepository.findByUserIdOrderByCreationDateDesc(
                user.getId(), PageRequest.of(page, size)
        );
        List<UserSubscription> items = entityPage.getContent().stream().map(userSubscriptionMapper::toDomain).toList();
        return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public long count(UserSubscriptionFilter filter) {
        return userSubscriptionRepository.count(createSpecification(filter));
    }

    private Specification<UserSubscriptionEntity> createSpecification(UserSubscriptionFilter filter) {
        Specification<UserSubscriptionEntity> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.getId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getId(), UserSubscriptionEntity_.id));
        }

        if (filter.getUserId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getUserId(), UserSubscriptionEntity_.userId));
        }

        if (filter.getPlanId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getPlanId(), UserSubscriptionEntity_.planId));
        }

        if (filter.getPaymentMode() != null) {
            spec = spec.and(buildStringSpecification(filter.getPaymentMode(), UserSubscriptionEntity_.paymentMode));
        }

        if (filter.getStatus() != null) {
            spec = spec.and(buildEnumSpecification(filter.getStatus(), UserSubscriptionEntity_.status));
        }

        spec = addAuditFieldsSpecifications(
                spec,
                filter,
                UserSubscriptionEntity_.creationDate,
                UserSubscriptionEntity_.lastUpdateDate,
                UserSubscriptionEntity_.lastUpdatedBy
        );

        return spec;
    }
}
