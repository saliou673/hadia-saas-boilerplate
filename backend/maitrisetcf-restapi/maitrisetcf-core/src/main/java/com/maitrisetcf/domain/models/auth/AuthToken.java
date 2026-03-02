package com.maitrisetcf.domain.models.auth;

import com.maitrisetcf.domain.exceptions.InvalidRefreshTokenExpiryDateException;
import com.maitrisetcf.domain.exceptions.InvalidRefreshTokenTokenException;
import com.maitrisetcf.domain.exceptions.InvalidRefreshTokenUserException;
import com.maitrisetcf.domain.models.user.User;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

@Getter
public class AuthToken {
    private final Long id;
    private String accessToken;
    private final String refreshToken;
    private final boolean rememberMe;
    private Instant expiryDate;
    private final User user;
    private final Instant creationDate;

    private AuthToken(Long id, String accessToken, String refreshToken, Boolean rememberMe, Instant expiryDate, User user, Instant creationDate) {
        if (StringUtils.isBlank(accessToken)) {
            throw new InvalidRefreshTokenTokenException("Access token must not be null or blank");
        }
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidRefreshTokenTokenException("Refresh token must not be null or blank");
        }
        if (expiryDate == null) {
            throw new InvalidRefreshTokenExpiryDateException("Refresh token expiry date must not be null");
        }
        if (user == null) {
            throw new InvalidRefreshTokenUserException("Refresh token user must not be null");
        }

        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.rememberMe = rememberMe != null && rememberMe;
        this.expiryDate = expiryDate;
        this.user = user;
        this.creationDate = creationDate;
    }

    public static AuthToken create(String accessToken, String refreshToken, boolean rememberMe, Instant expiryDate, User user) {
        return new AuthToken(null, accessToken, refreshToken, rememberMe, expiryDate, user, Instant.now());
    }

    public static AuthToken rehydrate(Long id, String accessToken, String refreshToken, boolean rememberMe, Instant expiryDate, User user, Instant creationDate) {
        return new AuthToken(id, accessToken, refreshToken, rememberMe, expiryDate, user, creationDate);
    }

    private boolean isExpired(Instant now) {
        return expiryDate.isBefore(now);
    }

    public boolean isValid(@NonNull Instant now) {
        return !isExpired(now);
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void updateExpiryDate(@NonNull Instant newExpiryDate) {
        if (newExpiryDate.isBefore(Instant.now())) {
            throw new InvalidRefreshTokenExpiryDateException("Refresh token expiry date must be in the future");
        }
        this.expiryDate = newExpiryDate;
    }

    public void updateAccessToken(String newAccessToken) {
        if (StringUtils.isBlank(newAccessToken)) {
            throw new InvalidRefreshTokenTokenException("Access token must not be null or blank");
        }
        this.accessToken = newAccessToken;
    }
}
