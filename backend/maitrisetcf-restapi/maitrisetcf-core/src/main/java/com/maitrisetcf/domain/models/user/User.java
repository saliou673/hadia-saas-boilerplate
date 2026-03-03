package com.maitrisetcf.domain.models.user;


import com.maitrisetcf.domain.enumerations.UserStatus;
import com.maitrisetcf.domain.exceptions.UserAlreadyActivatedException;
import com.maitrisetcf.domain.models.Auditable;
import com.maitrisetcf.domain.models.auth.TwoFactorMethodType;
import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.models.rbac.RoleGroup;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aggregate root representing an application user.
 */
@Getter
public class User extends Auditable<Long> {

    /**
     * Profile information (name, contact, locale).
     */
    private UserInfo userInfo;
    /**
     * Authentication credentials (email, password hash, codes).
     */
    private final UserCredentials userCredentials;
    /**
     * Current account lifecycle status.
     */
    private UserStatus status;
    /**
     * Role groups assigned to this user.
     */
    private final Set<RoleGroup> roleGroups;
    /**
     * Whether two-factor authentication is active.
     */
    private boolean twoFactorEnabled;
    /**
     * The 2FA method currently configured ({@code null} if 2FA is disabled).
     */
    private TwoFactorMethodType twoFactorMethod;
    /**
     * TOTP shared secret ({@code null} unless method is TOTP).
     */
    private String totpSecret;

    private User(
            Long id,
            UserInfo userInfo,
            UserCredentials userCredentials,
            UserStatus status,
            Set<RoleGroup> roleGroups,
            boolean twoFactorEnabled,
            TwoFactorMethodType twoFactorMethod,
            String totpSecret,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.userInfo = Objects.requireNonNull(userInfo, "userInfo must not be null");
        this.userCredentials = Objects.requireNonNull(userCredentials, "userCredentials must not be null");
        this.status = status;
        this.roleGroups = roleGroups == null ? new HashSet<>() : new HashSet<>(roleGroups);
        this.twoFactorEnabled = twoFactorEnabled;
        this.twoFactorMethod = twoFactorMethod;
        this.totpSecret = totpSecret;
    }

    public static User create(
            UserInfo userInfo,
            UserCredentials userCredentials
    ) {
        return new User(
                null,
                userInfo,
                userCredentials,
                UserStatus.NOT_ACTIVATED,
                new HashSet<>(),
                false,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static User rehydrate(
            Long id,
            UserInfo userInfo,
            UserCredentials userCredentials,
            UserStatus status,
            Set<RoleGroup> roleGroups,
            boolean twoFactorEnabled,
            TwoFactorMethodType twoFactorMethod,
            String totpSecret,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new User(
                id,
                userInfo,
                userCredentials,
                status,
                roleGroups,
                twoFactorEnabled,
                twoFactorMethod,
                totpSecret,
                creationDate,
                lastUpdateDate,
                lastUpdatedBy
        );
    }

    public Set<RoleGroup> getRoleGroups() {
        return Collections.unmodifiableSet(roleGroups);
    }

    /**
     * Returns the flat set of permissions resolved from all assigned role groups.
     */
    public Set<Permission> resolvePermissions() {
        return roleGroups.stream()
                .flatMap(rg -> rg.getPermissions().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public void updateInfo(UserInfoUpdate newUserInfo) {
        this.userInfo = this.userInfo.updateInfo(newUserInfo);
    }

    public boolean isActivationExpired(TemporalAmount nonActivatedUserRetentionPeriod) {
        return UserStatus.NOT_ACTIVATED.equals(this.status)
                && getCreationDate() != null
                && getCreationDate().isBefore(Instant.now().minus(nonActivatedUserRetentionPeriod));
    }

    public boolean isActive() {
        return UserStatus.ACTIVATED.equals(this.status);
    }

    public boolean isDeactivated() {
        return UserStatus.DEACTIVATED.equals(this.status);
    }

    public void activate(@NonNull Instant activationDate) {
        if (this.isActive()) {
            throw new UserAlreadyActivatedException();
        }

        this.userCredentials.activate(activationDate);
        this.status = UserStatus.ACTIVATED;
    }

    public void deactivate() {
        this.status = UserStatus.DEACTIVATED;
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVATED;
    }

    public void assignRoleGroups(Set<RoleGroup> roleGroups) {
        if (roleGroups == null || roleGroups.isEmpty()) {
            return;
        }
        this.roleGroups.addAll(roleGroups);
    }

    public void secureCredentials(String passwordHash) {
        this.userCredentials.hashPassword(passwordHash);
    }

    public void updateActivationCode(String activationCode) {
        this.userCredentials.updateActivationCode(activationCode);
    }

    public void updateResetCode(String resetCode, Instant resetDate) {
        this.userCredentials.updateResetCode(resetCode, resetDate);
    }

    public void changePassword(String newPasswordHash, Instant resetDate) {
        this.userCredentials.changePassword(newPasswordHash, resetDate);
    }

    public void enableTwoFactor(TwoFactorMethodType method) {
        this.twoFactorEnabled = true;
        this.twoFactorMethod = Objects.requireNonNull(method, "method must not be null");
    }

    public void setTotpSecret(String totpSecret) {
        this.totpSecret = totpSecret;
    }

    public void disableTwoFactor() {
        this.twoFactorEnabled = false;
        this.twoFactorMethod = null;
        this.totpSecret = null;
    }
}
