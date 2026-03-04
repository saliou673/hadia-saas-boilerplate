package com.hadiasaas.infrastructure.adapter.out.query;

import com.hadiasaas.domain.enumerations.UserGroupConstants;
import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.user.User;
import com.hadiasaas.domain.models.user.UserFilter;
import com.hadiasaas.domain.ports.in.UserQueryUseCase;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.*;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
 * JPA adapter implementing {@link UserQueryUseCase}.
 * Translates domain {@link UserFilter} into JPA {@link Specification} predicates.
 * All filter operators (equals, contains, in, range, etc.) are fully supported.
 */

/**
 * Query service implementing {@link com.hadiasaas.domain.ports.in.UserQueryUseCase} with JPA Specification-based filtering.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserQueryService extends QueryService<UserEntity> implements UserQueryUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public PagedResult<User> findAll(UserFilter filter, int page, int size) {
        log.debug("Finding users by filter: {}", filter);
        Page<UserEntity> entityPage = userRepository.findAll(
                createSpecification(filter),
                PageRequest.of(page, size, Sort.by("id").ascending())
        );
        List<User> users = entityPage.getContent().stream().map(userMapper::toDomain).toList();
        return new PagedResult<>(users, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public long count(UserFilter filter) {
        log.debug("Counting users by filter: {}", filter);
        return userRepository.count(createSpecification(filter));
    }

    private Specification<UserEntity> createSpecification(UserFilter filter) {
        Specification<UserEntity> spec = Specification.unrestricted();

        // Always filter by Admin role group
        spec = spec.and(hasAdminRoleGroup());

        if (filter == null) {
            return spec;
        }

        if (filter.getEmail() != null) {
            spec = spec.and(buildSpecification(filter.getEmail(),
                                               root -> root.get(UserEntity_.userCredentials).get(EmbeddableCredentials_.email)));
        }

        if (filter.getFirstName() != null) {
            spec = spec.and(buildSpecification(filter.getFirstName(),
                                               root -> root.get(UserEntity_.userInfo).get(EmbeddableUserInfo_.firstName)));
        }

        if (filter.getLastName() != null) {
            spec = spec.and(buildSpecification(filter.getLastName(),
                                               root -> root.get(UserEntity_.userInfo).get(EmbeddableUserInfo_.lastName)));
        }

        if (filter.getGender() != null) {
            spec = spec.and(buildSpecification(filter.getGender(),
                                               root -> root.get(UserEntity_.userInfo).get(EmbeddableUserInfo_.gender)));
        }

        if (filter.getStatus() != null) {
            spec = spec.and(buildEnumSpecification(filter.getStatus(), UserEntity_.status));
        }

        if (filter.getPhoneNumber() != null) {
            spec = spec.and(buildSpecification(filter.getPhoneNumber(),
                                               root -> root.get(UserEntity_.userInfo).get(EmbeddableUserInfo_.phoneNumber)));
        }

        if (filter.getAddress() != null) {
            spec = spec.and(buildSpecification(filter.getAddress(),
                                               root -> root.get(UserEntity_.userInfo).get(EmbeddableUserInfo_.address)));
        }

        if (filter.getLanguageKey() != null) {
            spec = spec.and(buildSpecification(filter.getLanguageKey(),
                                               root -> root.get(UserEntity_.userInfo).get(EmbeddableUserInfo_.languageKey)));
        }

        // Add audit fields specifications
        spec = addAuditFieldsSpecifications(
                spec,
                filter,
                UserEntity_.creationDate,
                UserEntity_.lastUpdateDate,
                UserEntity_.lastUpdatedBy
        );

        return spec;
    }

    private Specification<UserEntity> hasAdminRoleGroup() {
        return (root, query, cb) -> {
            Join<UserEntity, RoleGroupEntity> join = root.join(UserEntity_.roleGroups, JoinType.INNER);
            query.distinct(true);
            return cb.or(cb.equal(join.get(RoleGroupEntity_.name), UserGroupConstants.SYS_ADMIN), cb.equal(join.get(RoleGroupEntity_.name), UserGroupConstants.ADMIN));
        };
    }
}
