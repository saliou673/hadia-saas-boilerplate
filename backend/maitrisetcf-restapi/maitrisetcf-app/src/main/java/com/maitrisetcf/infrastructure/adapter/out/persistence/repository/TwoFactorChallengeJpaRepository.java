package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.domain.models.auth.TwoFactorChallengePurpose;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.TwoFactorChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwoFactorChallengeJpaRepository extends JpaRepository<TwoFactorChallengeEntity, String> {

    Optional<TwoFactorChallengeEntity> findByUserIdAndPurpose(Long userId, TwoFactorChallengePurpose purpose);

    void deleteByUserId(Long userId);
}
