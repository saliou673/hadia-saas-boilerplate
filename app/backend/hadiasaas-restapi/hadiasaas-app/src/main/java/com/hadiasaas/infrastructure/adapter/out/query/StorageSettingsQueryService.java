package com.hadiasaas.infrastructure.adapter.out.query;

import com.hadiasaas.domain.models.query.PagedResult;
import com.hadiasaas.domain.models.storagesettings.StorageSettings;
import com.hadiasaas.domain.models.storagesettings.StorageSettingsFilter;
import com.hadiasaas.domain.ports.in.StorageSettingsQueryUseCase;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.StorageSettingsEntity;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.StorageSettingsEntity_;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.StorageSettingsMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.StorageSettingsRepository;
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
 * Query service implementing {@link StorageSettingsQueryUseCase} with JPA Specification-based filtering.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorageSettingsQueryService extends QueryService<StorageSettingsEntity> implements StorageSettingsQueryUseCase {

    private final StorageSettingsRepository storageSettingsRepository;
    private final StorageSettingsMapper storageSettingsMapper;

    @Override
    public PagedResult<StorageSettings> findAll(StorageSettingsFilter filter, int page, int size) {
        log.debug("Finding storage settings by filter: {}", filter);
        Page<StorageSettingsEntity> entityPage = storageSettingsRepository.findAll(
                createSpecification(filter),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"))
        );
        List<StorageSettings> items = entityPage.getContent().stream().map(storageSettingsMapper::toDomain).toList();
        return new PagedResult<>(items, entityPage.getTotalElements(), page, size, entityPage.getTotalPages());
    }

    @Override
    public long count(StorageSettingsFilter filter) {
        log.debug("Counting storage settings by filter: {}", filter);
        return storageSettingsRepository.count(createSpecification(filter));
    }

    private Specification<StorageSettingsEntity> createSpecification(StorageSettingsFilter filter) {
        Specification<StorageSettingsEntity> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.getId() != null) {
            spec = spec.and(buildRangeSpecification(filter.getId(), StorageSettingsEntity_.id));
        }

        if (filter.getProvider() != null) {
            spec = spec.and(buildEnumSpecification(filter.getProvider(), StorageSettingsEntity_.provider));
        }

        if (filter.getActive() != null) {
            spec = spec.and(buildSpecification(filter.getActive(), StorageSettingsEntity_.active));
        }

        spec = addAuditFieldsSpecifications(
                spec,
                filter,
                StorageSettingsEntity_.creationDate,
                StorageSettingsEntity_.lastUpdateDate,
                StorageSettingsEntity_.lastUpdatedBy
        );

        return spec;
    }
}
