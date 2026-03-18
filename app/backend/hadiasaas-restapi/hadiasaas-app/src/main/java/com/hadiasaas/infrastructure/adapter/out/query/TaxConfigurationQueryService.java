package com.hadiasaas.infrastructure.adapter.out.query;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfiguration;
import com.hadiasaas.domain.models.taxconfiguration.TaxConfigurationFilter;
import com.hadiasaas.domain.ports.in.TaxConfigurationQueryUseCase;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TaxConfigurationEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TaxConfigurationEntity_;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.TaxConfigurationMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.TaxConfigurationRepository;
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
 * Query service implementing {@link TaxConfigurationQueryUseCase} with JPA Specification-based filtering.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxConfigurationQueryService extends QueryService<TaxConfigurationEntity> implements TaxConfigurationQueryUseCase {

    private final TaxConfigurationRepository taxConfigurationRepository;
    private final TaxConfigurationMapper taxConfigurationMapper;

    @Override
    public PagedResult<TaxConfiguration> findAll(TaxConfigurationFilter filter, int page, int size) {
        log.debug("Finding tax configurations by filter: {}", filter);
        Page<TaxConfigurationEntity> entityPage = taxConfigurationRepository.findAll(
                createSpecification(filter),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"))
        );
        List<TaxConfiguration> items = entityPage.getContent().stream().map(taxConfigurationMapper::toDomain).toList();
        return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public long count(TaxConfigurationFilter filter) {
        log.debug("Counting tax configurations by filter: {}", filter);
        return taxConfigurationRepository.count(createSpecification(filter));
    }

    private Specification<TaxConfigurationEntity> createSpecification(TaxConfigurationFilter filter) {
        Specification<TaxConfigurationEntity> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.getId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getId(), TaxConfigurationEntity_.id));
        }

        if (filter.getCode() != null) {
            spec = spec.and(buildStringSpecification(filter.getCode(), TaxConfigurationEntity_.code));
        }

        if (filter.getName() != null) {
            spec = spec.and(buildStringSpecification(filter.getName(), TaxConfigurationEntity_.name));
        }

        if (filter.getActive() != null) {
            spec = spec.and(buildSpecification(filter.getActive(), TaxConfigurationEntity_.active));
        }

        spec = addAuditFieldsSpecifications(
                spec,
                filter,
                TaxConfigurationEntity_.creationDate,
                TaxConfigurationEntity_.lastUpdateDate,
                TaxConfigurationEntity_.lastUpdatedBy
        );

        return spec;
    }
}
