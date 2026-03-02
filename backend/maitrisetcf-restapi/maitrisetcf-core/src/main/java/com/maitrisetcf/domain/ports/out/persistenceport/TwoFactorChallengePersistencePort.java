package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.auth.TwoFactorChallenge;
import com.maitrisetcf.domain.models.auth.TwoFactorChallengePurpose;

import java.util.Optional;

/**
 * Persistence port for two-factor authentication challenges.
 */
public interface TwoFactorChallengePersistencePort {

    TwoFactorChallenge save(TwoFactorChallenge challenge);

    Optional<TwoFactorChallenge> findById(String id);

    Optional<TwoFactorChallenge> findByUserIdAndPurpose(Long userId, TwoFactorChallengePurpose purpose);

    void deleteById(String id);

    void deleteByUserId(Long userId);
}
