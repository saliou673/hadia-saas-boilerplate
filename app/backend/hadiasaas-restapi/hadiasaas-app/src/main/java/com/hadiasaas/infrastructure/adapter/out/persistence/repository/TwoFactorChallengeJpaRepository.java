package com.hadiasaas.infrastructure.adapter.out.persistence.repository;

import com.hadiasaas.domain.models.auth.TwoFactorChallengePurpose;
import com.hadiasaas.infrastructure.adapter.out.persistence.entity.TwoFactorChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TwoFactorChallengeEntity}.
 */
public interface TwoFactorChallengeJpaRepository extends JpaRepository<TwoFactorChallengeEntity, String> {

    Optional<TwoFactorChallengeEntity> findByUserIdAndPurpose(Long userId, TwoFactorChallengePurpose purpose);

    void deleteByUserId(Long userId);
}
