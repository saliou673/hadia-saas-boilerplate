package com.maitrisetcf.domain.models.auth;

import com.maitrisetcf.domain.models.user.User;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a pending two-factor authentication challenge.
 * Used for both login (PURPOSE=LOGIN) and 2FA setup verification (PURPOSE=SETUP).
 */
@Getter
public class TwoFactorChallenge {

    private final String id;
    private final User user;
    private final String code;
    private final TwoFactorMethodType type;
    private final TwoFactorChallengePurpose purpose;
    private final boolean rememberMe;
    private final Instant expiryDate;
    private final Instant creationDate;

    private TwoFactorChallenge(
            String id,
            User user,
            String code,
            TwoFactorMethodType type,
            TwoFactorChallengePurpose purpose,
            boolean rememberMe,
            Instant expiryDate,
            Instant creationDate
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.purpose = Objects.requireNonNull(purpose, "purpose must not be null");
        this.rememberMe = rememberMe;
        this.expiryDate = Objects.requireNonNull(expiryDate, "expiryDate must not be null");
        this.creationDate = creationDate != null ? creationDate : Instant.now();
    }

    public static TwoFactorChallenge create(
            String id,
            User user,
            String code,
            TwoFactorMethodType type,
            TwoFactorChallengePurpose purpose,
            boolean rememberMe,
            Instant expiryDate
    ) {
        return new TwoFactorChallenge(id, user, code, type, purpose, rememberMe, expiryDate, Instant.now());
    }

    public static TwoFactorChallenge rehydrate(
            String id,
            User user,
            String code,
            TwoFactorMethodType type,
            TwoFactorChallengePurpose purpose,
            boolean rememberMe,
            Instant expiryDate,
            Instant creationDate
    ) {
        return new TwoFactorChallenge(id, user, code, type, purpose, rememberMe, expiryDate, creationDate);
    }

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
