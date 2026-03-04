package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.discountcode.DiscountCode;

import java.util.Optional;

/**
 * Persistence port for discount codes.
 */
public interface DiscountCodePersistencePort {

    DiscountCode save(DiscountCode discountCode);

    Optional<DiscountCode> findById(Long id);

    Optional<DiscountCode> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long excludeId);

    void remove(DiscountCode discountCode);
}
