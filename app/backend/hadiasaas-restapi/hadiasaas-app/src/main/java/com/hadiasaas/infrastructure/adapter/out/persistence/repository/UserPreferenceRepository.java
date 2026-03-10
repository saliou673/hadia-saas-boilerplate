package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.infrastructure.adapter.out.persistence.entity.UserPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * Repository for user preference rows.
 */
public interface UserPreferenceRepository extends JpaRepository<UserPreferenceEntity, Long> {

    /**
     * Finds preferences for the given users.
     *
     * @param userIds user identifiers
     * @return matching rows
     */
    List<UserPreferenceEntity> findAllByUserIdIn(Set<Long> userIds);
}
