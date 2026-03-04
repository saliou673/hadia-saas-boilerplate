package com.maitrisetcf.infrastructure.adapter.out.query;

import com.maitrisetcf.domain.exceptions.DiscountCodeNotFoundException;
import com.maitrisetcf.domain.models.discountcode.DiscountCode;
import com.maitrisetcf.domain.models.discountcode.DiscountCodeFilter;
import com.maitrisetcf.domain.models.query.PagedResult;
import com.maitrisetcf.domain.ports.in.DiscountCodeQueryUseCase;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity_;
import com.maitrisetcf.infrastructure.adapter.out.persistence.mapper.DiscountCodeMapper;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.DiscountCodeRepository;
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
 * Query service implementing {@link DiscountCodeQueryUseCase}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountCodeQueryService extends QueryService<DiscountCodeEntity> implements DiscountCodeQueryUseCase {

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountCodeMapper discountCodeMapper;

    @Override
    public PagedResult<DiscountCode> findAll(DiscountCodeFilter filter, int page, int size) {
        log.debug("Finding discount codes by filter: {}", filter);
        Page<DiscountCodeEntity> entityPage = discountCodeRepository.findAll(
                createSpecification(filter),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"))
        );
        List<DiscountCode> items = entityPage.getContent().stream().map(discountCodeMapper::toDomain).toList();
        return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public DiscountCode getByCode(String code) {
        return discountCodeRepository.findByCode(code)
                .map(discountCodeMapper::toDomain)
                .orElseThrow(() -> new DiscountCodeNotFoundException("Discount code not found with code: " + code));
    }

    private Specification<DiscountCodeEntity> createSpecification(DiscountCodeFilter filter) {
        Specification<DiscountCodeEntity> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.getId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getId(), DiscountCodeEntity_.id));
        }
        if (filter.getCode() != null) {
            spec = spec.and(buildStringSpecification(filter.getCode(), DiscountCodeEntity_.code));
        }
        if (filter.getActive() != null) {
            spec = spec.and(buildSpecification(filter.getActive(), DiscountCodeEntity_.active));
        }
        if (filter.getDiscountType() != null) {
            spec = spec.and(buildEnumSpecification(filter.getDiscountType(), DiscountCodeEntity_.discountType));
        }

        return addAuditFieldsSpecifications(
                spec,
                filter,
                DiscountCodeEntity_.creationDate,
                DiscountCodeEntity_.lastUpdateDate,
                DiscountCodeEntity_.lastUpdatedBy
        );
    }
}
