package com.maitrisetcf.application;

import com.maitrisetcf.config.ApplicationProperties;
import com.maitrisetcf.domain.enumerations.AppConfigurationCategory;
import com.maitrisetcf.domain.enumerations.UserGroupConstants;
import com.maitrisetcf.domain.enumerations.UserStatus;
import com.maitrisetcf.domain.exceptions.*;
import com.maitrisetcf.domain.models.auth.TwoFactorMethodType;
import com.maitrisetcf.domain.models.rbac.RoleGroup;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.models.user.UserInfoUpdate;
import com.maitrisetcf.domain.ports.in.AccountUseCase;
import com.maitrisetcf.domain.ports.out.CurrentUserEmailPort;
import com.maitrisetcf.domain.ports.out.NotificationSenderPort;
import com.maitrisetcf.domain.ports.out.PasswordHasherPort;
import com.maitrisetcf.domain.ports.out.persistenceport.AppConfigurationPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.AuthTokenPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.RoleGroupPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.UserPersistencePort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.function.Predicate;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService implements AccountUseCase {

    public static final int OTP_CODE_SIZE = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HexFormat HEX_FORMAT = HexFormat.of();
    private static final int MAX_CODE_GENERATION_ATTEMPTS = 10;

    private final UserPersistencePort userPersistencePort;
    private final CurrentUserEmailPort currentUserEmailPort;
    private final RoleGroupPersistencePort roleGroupPersistencePort;
    private final NotificationSenderPort notificationSenderPort;
    private final PasswordHasherPort passwordHasherPort;
    private final ApplicationProperties applicationProperties;
    private final AuthTokenPersistencePort authTokenPersistencePort;
    private final AppConfigurationPersistencePort appConfigurationPersistencePort;

    @Override
    public void createPublicUser(User user) {
        createUser(user, Set.of(UserGroupConstants.USER));
    }

    @Override
    public void createUser(User user, Set<String> roleGroupNames) {
        ensureEmailUniqueness(user);
        assignRoleGroups(user, roleGroupNames);
        secureCredentials(user);
        assignActivationCode(user);
        User userCreated = userPersistencePort.save(user);
        notificationSenderPort.sendActivationNotification(userCreated);
    }

    @Override
    public User createManagedUser(User user, Set<String> roleGroupNames) {
        ensureEmailUniqueness(user);
        assignRoleGroups(user, roleGroupNames);
        // Defined a random password to avoid empty value
        user.secureCredentials(passwordHasherPort.hash(UUID.randomUUID().toString()));
        assignInvitationCodeToManagedUser(user);
        if (!isUserOnly(roleGroupNames)) {
            resolveDefaultTwoFactorMethod().ifPresent(user::enableTwoFactor);
        }
        User savedUser = userPersistencePort.save(user);
        notificationSenderPort.sendManagedUserInvitationNotification(savedUser);
        return savedUser;
    }

    @Override
    public void completeInvitation(String invitationCode, String newPassword) {
        User user = userPersistencePort.findByResetCode(invitationCode)
                .filter(u -> u.getStatus() == UserStatus.NOT_ACTIVATED)
                .filter(u -> u.getUserCredentials().getResetDate() != null)
                .filter(u -> u.getUserCredentials().getResetDate()
                        .isAfter(Instant.now().minus(getManagedUserInvitationCodeValidityPeriod())))
                .orElseThrow(() -> new InvalidResetCodeException("Invitation code invalid or expired"));

        user.changePassword(passwordHasherPort.hash(newPassword), Instant.now());
        user.activate(Instant.now());
        userPersistencePort.save(user);
    }

    @Override
    public void activateRegistration(String activationCode) {
        User user = userPersistencePort.findByActivationCode(activationCode)
                .orElseThrow(() -> new ActivationCodeNotFoundException("User with activation code " + activationCode + " not found"));

        user.activate(Instant.now());

        userPersistencePort.save(user);
        notificationSenderPort.sendCreationNotification(user);
    }

    @Override
    public void completePasswordReset(String newPassword, String resetCode) {
        User user = userPersistencePort.findByResetCode(resetCode)
                .filter(u -> u.getUserCredentials().getResetDate() != null)
                .filter(u -> u.getUserCredentials().getResetDate()
                        .isAfter(Instant.now().minus(getResetCodeValidityPeriod())))
                .orElseThrow(() -> new InvalidResetCodeException("Reset code invalid or expired"));

        String newPasswordHash = passwordHasherPort.hash(newPassword);
        user.changePassword(newPasswordHash, Instant.now());
        userPersistencePort.save(user);
    }

    @Override
    public void requestPasswordReset(String email) {
        userPersistencePort.findByEmail(email)
                .filter(User::isActive)
                .ifPresentOrElse(user -> {
                    String resetCode = generateUniqueCode(userPersistencePort::existsByResetCode, "reset");
                    user.updateResetCode(resetCode, Instant.now());
                    User savedUser = userPersistencePort.save(user);
                    notificationSenderPort.sendPasswordResetNotification(savedUser);
                }, () -> log.warn("Cannot reset password for unactivated or not found user {}", email));
    }

    @Override
    public void sendActivationCode(String email) {
        User existingUser = getUserByEmailOrThrow(email);

        if (existingUser.isActive()) {
            log.warn("User with email {} is already active. No activation code sent.", email);
            return;
        }

        assignActivationCode(existingUser);
        userPersistencePort.save(existingUser);
        notificationSenderPort.sendActivationNotification(existingUser);
    }

    @Override
    public User updateUser(String email, UserInfoUpdate userInfoUpdate) {
        User existingUser = getUserByEmailOrThrow(email);
        existingUser.updateInfo(userInfoUpdate);
        return userPersistencePort.save(existingUser);
    }

    @Override
    public void changePassword(String currentPassword, String newPassword) {
        String email = getCurrentUserEmail();
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (!passwordHasherPort.matches(currentPassword, user.getUserCredentials().getPasswordHash())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        user.secureCredentials(passwordHasherPort.hash(newPassword));
        userPersistencePort.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getCurrentUserWithAuthorities() {
        String email = getCurrentUserEmail();
        return userPersistencePort.findWithAuthoritiesByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserWithAuthoritiesById(Long id) {
        return getUserByIdOrThrow(id);
    }

    @Override
    public User updateCurrentUser(UserInfoUpdate userInfoUpdate) {
        String email = getCurrentUserEmail();
        return updateUser(email, userInfoUpdate);
    }

    @Override
    public User updateUserById(Long id, UserInfoUpdate userInfoUpdate) {
        User existingUser = getUserByIdOrThrow(id);
        existingUser.updateInfo(userInfoUpdate);
        return userPersistencePort.save(existingUser);
    }

    @Override
    public void deleteUserById(Long id) {
        User existingUser = getUserByIdOrThrow(id);
        userPersistencePort.remove(existingUser);
    }

    @Override
    public void removeNotActivatedUsers() {
        Instant date = Instant.now().minus(getNonActivatedUserRetentionPeriod());
        log.debug("Deleting not activated users since {}", date);
        int count = userPersistencePort.deleteInactiveUsersWithExpiredActivationCode(UserStatus.ACTIVATED, date);
        log.debug("{} not activated users deleted", count);
    }

    @Override
    public void deleteCurrentUserAccount() {
        String email = getCurrentUserEmail();
        User user = getUserByEmailOrThrow(email);

        if (!user.isActive()) {
            throw new AccountNotActivatedException("User with email " + email + " is not activated");
        }

        user.deactivate();
        User deletedUser = userPersistencePort.save(user);
        authTokenPersistencePort.deleteAllForUser(deletedUser);
        notificationSenderPort.sendAccountDeletionNotification(deletedUser);
    }

    @Override
    public void recoverAccount(String email, String password) {
        User user = getUserByEmailOrThrow(email);

        if (!user.isDeactivated()) {
            throw new FunctionalException("Account recovery is available only for deactivated accounts");
        }

        Instant cutoff = Instant.now().minus(getSoftDeletedUserRetentionPeriod());
        if (user.getLastUpdateDate() != null && user.getLastUpdateDate().isBefore(cutoff)) {
            throw new FunctionalException("Recovery period expired. Your account has been permanently deleted.");
        }

        if (!passwordHasherPort.matches(password, user.getUserCredentials().getPasswordHash())) {
            throw new InvalidCurrentPasswordException("Invalid credentials for account recovery");
        }

        user.reactivate();
        userPersistencePort.save(user);
    }

    @Override
    public void removeSoftDeletedUsers() {
        // TODO: Improve this method to check if the user is linked to any data (table) and then really anonymize the user and the set the status to DELETED.
        Instant date = Instant.now().minus(getSoftDeletedUserRetentionPeriod());
        log.info("Deleting soft-deleted users since {}", date);
        int count = userPersistencePort.deleteByStatusAndLastUpdateDateBefore(UserStatus.DEACTIVATED, date);
        log.info("{} soft-deleted users permanently removed", count);
    }

    private String getCurrentUserEmail() {
        return currentUserEmailPort.getCurrentUserEmail();
    }

    private void assignActivationCode(User user) {
        user.updateActivationCode(generateUniqueCode(userPersistencePort::existsByActivationCode, "activation"));
    }

    private void assignInvitationCodeToManagedUser(User user) {
        int codeLength = applicationProperties.getAccount().managedUserInvitationCodeLength();
        String code = generateUniqueCode(userPersistencePort::existsByResetCode, "invitation", codeLength);
        user.updateResetCode(code, Instant.now());
    }

    private String generateUniqueCode(Predicate<String> existsCodePredicate, String codeType) {
        return generateUniqueCode(existsCodePredicate, codeType, OTP_CODE_SIZE - 1);
    }

    private String generateUniqueCode(Predicate<String> existsCodePredicate, String codeType, int size) {
        for (int attempt = 1; attempt <= MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
            String generatedCode = getRandomCode(size);
            if (!existsCodePredicate.test(generatedCode)) {
                return generatedCode;
            }
            log.warn("Collision on {} code generation at attempt {}", codeType, attempt);
        }
        throw new TechnicalException(
                "Unable to generate a unique " + codeType + " code after " + MAX_CODE_GENERATION_ATTEMPTS + " attempts"
        );
    }

    private static String getRandomCode(int size) {
        int byteCount = (size / 2) + 1;
        byte[] randomBytes = new byte[byteCount];
        SECURE_RANDOM.nextBytes(randomBytes);
        return HEX_FORMAT.formatHex(randomBytes).substring(0, size).toUpperCase(Locale.ROOT);
    }

    private void ensureEmailUniqueness(User user) {
        String email = user.getUserCredentials().getEmail();

        userPersistencePort.findByEmail(email).ifPresent(existingUser -> {
            if (existingUser.isActivationExpired(getNonActivatedUserRetentionPeriod())) {
                log.warn(
                        "User with email {} already exists but is not activated. Deleting existing user.",
                        email
                );
                userPersistencePort.remove(existingUser);
            } else {
                throw new UserAlreadyExistsException(
                        "User with email " + email + " already exists"
                );
            }
        });
    }

    private void assignRoleGroups(User user, Set<String> roleGroupNames) {
        Set<RoleGroup> roleGroups = roleGroupPersistencePort.findByNames(roleGroupNames);

        if (roleGroups.isEmpty()) {
            throw new IllegalArgumentException("No role groups found for names: " + roleGroupNames);
        }

        user.assignRoleGroups(roleGroups);
    }

    private void secureCredentials(User user) {
        Objects.requireNonNull(user.getUserCredentials());
        user.secureCredentials(passwordHasherPort.hash(
                user.getUserCredentials().getPasswordHash()
        ));
    }

    private User getUserByEmailOrThrow(String email) {
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    private TemporalAmount getNonActivatedUserRetentionPeriod() {
        return applicationProperties.getAccount().nonActivatedUserRetentionPeriod();
    }

    private TemporalAmount getSoftDeletedUserRetentionPeriod() {
        return applicationProperties.getAccount().softDeletedUserRetentionPeriod();
    }

    private TemporalAmount getResetCodeValidityPeriod() {
        return applicationProperties.getAccount().resetCodeValidityPeriod();
    }

    private TemporalAmount getManagedUserInvitationCodeValidityPeriod() {
        return applicationProperties.getAccount().managedUserInvitationCodeValidityPeriod();
    }

    private boolean isUserOnly(Set<String> roleGroupNames) {
        return roleGroupNames.size() == 1 && roleGroupNames.contains(UserGroupConstants.USER);
    }

    private Optional<TwoFactorMethodType> resolveDefaultTwoFactorMethod() {
        for (TwoFactorMethodType method : TwoFactorMethodType.values()) {
            if (appConfigurationPersistencePort.existsActiveByCategoryAndCode(
                    AppConfigurationCategory.TWO_FACTOR, method.name())) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    private User getUserByIdOrThrow(Long id) {
        return userPersistencePort.findWithAuthoritiesById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }
}
