package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Long> {
    Optional<AuthTokenEntity> findByRefreshToken(String refreshToken);

    Optional<AuthTokenEntity> findByAccessToken(String accessToken);

    void deleteAllByUserId(Long id);

    void deleteByAccessToken(String accessToken);
}
