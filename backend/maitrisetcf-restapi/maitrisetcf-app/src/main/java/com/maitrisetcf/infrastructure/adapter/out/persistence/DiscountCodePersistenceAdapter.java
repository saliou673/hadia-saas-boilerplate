package com.maitrisetcf.infrastructure.adapter.out.persistence;

import com.maitrisetcf.domain.models.discountcode.DiscountCode;
import com.maitrisetcf.domain.ports.out.persistenceport.DiscountCodePersistencePort;
import com.maitrisetcf.infrastructure.adapter.out.persistence.mapper.DiscountCodeMapper;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.DiscountCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter implementing {@link DiscountCodePersistencePort}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DiscountCodePersistenceAdapter implements DiscountCodePersistencePort {

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountCodeMapper discountCodeMapper;

    @Override
    public DiscountCode save(DiscountCode discountCode) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> discountCodeMapper.toDomain(discountCodeRepository.save(discountCodeMapper.toEntity(discountCode))),
                "Error saving discount code: " + discountCode.getCode()
        );
    }

    @Override
    public Optional<DiscountCode> findById(Long id) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> discountCodeRepository.findById(id).map(discountCodeMapper::toDomain),
                "Error fetching discount code by id: " + id
        );
    }

    @Override
    public Optional<DiscountCode> findByCode(String code) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> discountCodeRepository.findByCode(code).map(discountCodeMapper::toDomain),
                "Error fetching discount code by code: " + code
        );
    }

    @Override
    public boolean existsByCode(String code) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> discountCodeRepository.existsByCode(code),
                "Error checking discount code existence for code: " + code
        );
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long excludeId) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> discountCodeRepository.existsByCodeAndIdNot(code, excludeId),
                "Error checking discount code existence for code: " + code
        );
    }

    @Override
    public void remove(DiscountCode discountCode) {
        AdapterPersistenceUtils.executeDbOperation(
                () -> discountCodeRepository.delete(discountCodeMapper.toEntity(discountCode)),
                "Error removing discount code with id: " + discountCode.getId()
        );
    }
}
