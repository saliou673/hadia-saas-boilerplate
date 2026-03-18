package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.infrastructure.adapter.out.persistence.entity.EnterpriseProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EnterpriseProfileEntity}.
 */
@Repository
public interface EnterpriseProfileRepository extends JpaRepository<EnterpriseProfileEntity, Long> {
}
