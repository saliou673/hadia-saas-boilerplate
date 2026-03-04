package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.DiscountCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link DiscountCodeEntity}.
 */
@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCodeEntity, Long>, JpaSpecificationExecutor<DiscountCodeEntity> {

    Optional<DiscountCodeEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
