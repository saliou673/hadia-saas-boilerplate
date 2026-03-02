package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.auth.AuthToken;
import com.maitrisetcf.domain.models.user.User;

import java.util.Optional;

public interface AuthTokenPersistencePort {

    Optional<com.maitrisetcf.domain.models.auth.AuthToken> findByRefreshToken(String refreshToken);

    Optional<AuthToken> findByAccessToken(String accessToken);

    void save(AuthToken authToken);

    void deleteAllForUser(User user);

    void deleteByAccessToken(String accessToken);
}
