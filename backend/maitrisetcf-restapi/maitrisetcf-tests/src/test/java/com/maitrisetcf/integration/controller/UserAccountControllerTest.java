package com.maitrisetcf.integration.controller;

import com.maitrisetcf.domain.enumerations.UserGender;
import com.maitrisetcf.domain.enumerations.UserGroupConstants;
import com.maitrisetcf.domain.enumerations.UserStatus;
import com.maitrisetcf.domain.ports.out.NotificationSenderPort;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.UserSummaryDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.*;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.RoleGroupEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.PermissionRepository;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.RoleGroupRepository;
import com.maitrisetcf.integration.IntegrationTest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class UserAccountControllerTest extends IntegrationTest {
    private static final String API_ACCOUNTS_BASE_URL = "/api/v1/accounts";
    private static final String API_REGISTER = API_ACCOUNTS_BASE_URL + "/register";
    private static final String API_ACTIVATE = API_ACCOUNTS_BASE_URL + "/activation";
    private static final String API_RESEND_ACTIVATION = API_ACCOUNTS_BASE_URL + "/activation/resend";
    private static final String API_ACCOUNT = API_ACCOUNTS_BASE_URL + "/me";
    private static final String API_ACCOUNT_RECOVER = API_ACCOUNTS_BASE_URL + "/recover";
    private static final String API_ACCOUNT_PERMISSIONS = API_ACCOUNTS_BASE_URL + "/me/permissions";
    private static final String API_CHANGE_PASSWORD = API_ACCOUNTS_BASE_URL + "/me/password";
    private static final String API_RESET_PASSWORD_INIT = API_ACCOUNTS_BASE_URL + "/reset-password/init";
    private static final String API_RESET_PASSWORD_FINISH = API_ACCOUNTS_BASE_URL + "/reset-password/finish";
    private static final String API_INVITATION_COMPLETE = API_ACCOUNTS_BASE_URL + "/invitation/complete";

    @MockitoBean
    private NotificationSenderPort notificationSenderPort;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        CreateUserRequest request = createValidUserRequest();

        doNothing().when(notificationSenderPort).sendActivationNotification(any());

        post(API_REGISTER, request, status().isCreated());

        // Verify the user was saved in the database
        Optional<UserEntity> savedUser = userRepository.findOneByUserCredentialsEmailIgnoreCase(request.email());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUserCredentials().getEmail())
                .isEqualTo(request.email().toLowerCase());
        assertThat(savedUser.get().getStatus()).isEqualTo(UserStatus.NOT_ACTIVATED);
        assertThat(savedUser.get().getUserInfo().getFirstName()).isEqualTo(request.firstName());
        assertThat(savedUser.get().getUserInfo().getLastName()).isEqualTo(request.lastName());

        // Check that a mail has been sent
        verify(notificationSenderPort).sendActivationNotification(any());
    }

    @Test
    void shouldFailToRegisterWithInvalidPassword() throws Exception {
        CreateUserRequest request = new CreateUserRequest("test@example.com",
                                                          "weakpassword",
                                                          "Mamadou",
                                                          "Diallo",
                                                          null,
                                                          UserGender.MALE,
                                                          null,
                                                          null,
                                                          null,
                                                          null);

        post(API_REGISTER, request, status().isBadRequest());

        // Verify no user was created
        assertThat(userRepository.count()).isZero();
        verify(notificationSenderPort, never()).sendActivationNotification(any());
    }

    @Test
    void shouldFailToRegisterWithDuplicateEmail() throws Exception {
        // Create an existing activated user
        createUser("test@example.com");

        CreateUserRequest request = createValidUserRequest();

        post(API_REGISTER, request, status().isConflict());

        // Verify only one user exists
        assertThat(userRepository.count()).isEqualTo(1);
        verify(notificationSenderPort, never()).sendActivationNotification(any());
    }

    @Test
    void shouldReplaceNonActivatedUserWithSameEmail() throws Exception {
        // Create an existing non-activated user
        UserEntity existingUser = createNonActiveUser("test1@example.com", Instant.now().minus(4, ChronoUnit.DAYS));

        Long existingUserId = existingUser.getId();

        CreateUserRequest request = createValidUserRequest();

        // Check that the activation email is sent to the existing user
        doNothing().when(notificationSenderPort).sendActivationNotification(any());

        post(API_REGISTER, request, status().isCreated());

        UserEntity createdUser = userRepository.findOneByUserCredentialsEmailIgnoreCase(request.email()).orElseThrow();
        assertThat(createdUser.getId()).as("The new user is created").isNotEqualTo(existingUserId);
        assertThat(userRepository.count()).as("The old user is deleted").isEqualTo(2); // The old user is deleted and the new one is created

        verify(notificationSenderPort).sendActivationNotification(any());
    }

    @Test
    void shouldFailToRegisterWithInvalidData() throws Exception {
        // Test with null email
        CreateUserRequest request = new CreateUserRequest(null,
                                                          "validPassword123!",
                                                          "Mamadou",
                                                          "Diallo",
                                                          null,
                                                          UserGender.MALE,
                                                          null,
                                                          null,
                                                          null,
                                                          null);
        post(API_REGISTER, request, status().isBadRequest());

        // Test with blank email
        request = new CreateUserRequest("",
                                        "validPassword123!",
                                        "Mamadou",
                                        "Diallo",
                                        null,
                                        UserGender.MALE,
                                        null,
                                        null,
                                        null,
                                        null);
        post(API_REGISTER, request, status().isBadRequest());

        // Test with a null password
        request = new CreateUserRequest("test@example.com",
                                        null,
                                        "Mamadou",
                                        "Diallo",
                                        null,
                                        UserGender.MALE,
                                        null,
                                        null,
                                        null,
                                        null);
        post(API_REGISTER, request, status().isBadRequest());

        assertThat(userRepository.count()).as("No user should be created").isZero();
        verify(notificationSenderPort, never()).sendActivationNotification(any()); // Any mail should not be sent
    }

    @Test
    void shouldActivateAccountSuccessfully() throws Exception {
        // Create a non-activated user
        UserEntity user = createNonActiveUser("test@example.com");
        String activationCode = "55MZ0EZ";
        user.getUserCredentials().setActivationCode(activationCode);
        user.setStatus(UserStatus.NOT_ACTIVATED);
        userRepository.save(user);

        get(API_ACTIVATE + "?code=" + activationCode, status().isOk());

        // Verify user is activated
        UserEntity activatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(activatedUser.isActivated()).isTrue();
        assertThat(activatedUser.getUserCredentials().getActivationDate()).isNotNull();
        assertThat(activatedUser.getUserCredentials().getActivationCode()).isNull();
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:read:own")
    void shouldGetCurrentAccountSuccessfully() throws Exception {
        UserEntity user = createUser("test@example.com", Set.of(UserGroupConstants.ADMIN));

        UserSummaryDTO result = get(API_ACCOUNT, new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.getUserCredentials().getEmail());
        assertThat(result.firstName()).isEqualTo(user.getUserInfo().getFirstName());
        assertThat(result.lastName()).isEqualTo(user.getUserInfo().getLastName());
    }

    // region UserAccountController.getCurrentUserPermissions

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:read:own")
    void shouldGetCurrentUserPermissionsSuccessfully() throws Exception {
        UserEntity user = createUserWithoutRole("test@example.com");
        RoleGroupEntity roleGroup = createRoleGroupWithPermissions("TestGroup", Set.of("user:read"));
        user.getRoleGroups().add(roleGroup);
        userRepository.save(user);

        List<PermissionDTO> result = get(API_ACCOUNT_PERMISSIONS, new TypeReference<>() {}, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result).extracting(PermissionDTO::code)
                .containsExactlyInAnyOrder("user:read");
        assertThat(result).extracting(PermissionDTO::code).isSorted();
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:read:own")
    void shouldReturnEmptyPermissionsWhenUserHasNoRoleGroups() throws Exception {
        createUserWithoutRole("test@example.com");

        List<PermissionDTO> result = get(API_ACCOUNT_PERMISSIONS, new TypeReference<>() {}, status().isOk());

        assertThat(result).isEmpty();
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:read:own")
    void shouldFlattenPermissionsFromMultipleRoleGroups() throws Exception {
        UserEntity user = createUserWithoutRole("test@example.com");
        RoleGroupEntity groupA = createRoleGroupWithPermissions("GroupA", Set.of("user:read"));
        RoleGroupEntity groupB = createRoleGroupWithPermissions("GroupB", Set.of("role-group:read", "role-group:manage"));
        user.getRoleGroups().add(groupA);
        user.getRoleGroups().add(groupB);
        userRepository.save(user);

        List<PermissionDTO> result = get(API_ACCOUNT_PERMISSIONS, new TypeReference<>() {}, status().isOk());

        assertThat(result).extracting(PermissionDTO::code)
                .containsExactlyInAnyOrder("user:read", "role-group:read", "role-group:manage");
        assertThat(result).extracting(PermissionDTO::code).isSorted();
    }

    @Test
    void shouldRejectUnauthenticatedRequestForCurrentUserPermissions() throws Exception {
        get(API_ACCOUNT_PERMISSIONS, status().isUnauthorized());
    }

    // endregion

    @Test
    @WithMockUser(username = "nonexistent@example.com", authorities = "user:read:own")
    void shouldFailToGetAccountWhenUserNotFound() throws Exception {
        get(API_ACCOUNT, status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:update:own")
    void shouldUpdateAccountSuccessfully() throws Exception {
        UserEntity user = createUser("test@example.com");

        UpdateUserRequest request = createValidUpdateRequest();

        UserSummaryDTO result = put(API_ACCOUNT, request, UserSummaryDTO.class, status().isOk());

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo(request.firstName());
        assertThat(result.lastName()).isEqualTo(request.lastName());

        // Verify the user was updated in database
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserInfo().getFirstName()).isEqualTo(request.firstName());
        assertThat(updatedUser.getUserInfo().getLastName()).isEqualTo(request.lastName());
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:update:own")
    void shouldSoftDeleteCurrentAccount() throws Exception {
        UserEntity user = createUser("test@example.com");

        doNothing().when(notificationSenderPort).sendAccountDeletionNotification(any());

        delete(API_ACCOUNT, status().isNoContent());

        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.DEACTIVATED);
        verify(notificationSenderPort).sendAccountDeletionNotification(any());
    }

    @Test
    void shouldRecoverSoftDeletedAccount() throws Exception {
        UserEntity user = createUser("test@example.com");
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);

        RecoverAccountRequest request = new RecoverAccountRequest("test@example.com", DEFAULT_USER_PASSWORD);
        post(API_ACCOUNT_RECOVER, request, status().isOk());

        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVATED);
    }

    @Test
    void shouldFailToRecoverSoftDeletedAccountWithInvalidPassword() throws Exception {
        UserEntity user = createUser("test@example.com");
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);

        RecoverAccountRequest request = new RecoverAccountRequest("test@example.com", "WrongPass123!");
        post(API_ACCOUNT_RECOVER, request, status().isBadRequest());

        UserEntity unchangedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(unchangedUser.getStatus()).isEqualTo(UserStatus.DEACTIVATED);
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:update:own")
    void shouldChangePasswordSuccessfully() throws Exception {
        String currentPassword = "currentPassword123!";
        UserEntity user = createUser("test@example.com");
        user.getUserCredentials().setPasswordHash(passwordEncoder.encode(currentPassword));
        userRepository.save(user);

        PasswordChangeRequest request = new PasswordChangeRequest(currentPassword, "newPassword123!");

        patch(API_CHANGE_PASSWORD, request, status().isOk());

        // Verify password was changed
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(request.newPassword(), updatedUser.getUserCredentials().getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getUserCredentials().getPasswordHash())).isFalse();
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:update:own")
    void shouldFailToChangePasswordWithWrongCurrentPassword() throws Exception {
        UserEntity user = createUser("test@example.com");
        user.getUserCredentials().setPasswordHash(passwordEncoder.encode("actualPassword123!"));
        userRepository.save(user);

        PasswordChangeRequest request = new PasswordChangeRequest("wrongPassword123!", "newPassword123!");

        patch(API_CHANGE_PASSWORD, request, status().isBadRequest());

        // Verify that the password was not changed
        UserEntity unchangedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("actualPassword123!", unchangedUser.getUserCredentials().getPasswordHash())).isTrue();
    }

    @Test
    @WithMockUser(username = "test@example.com", authorities = "user:update:own")
    void shouldFailToChangePasswordWithInvalidNewPassword() throws Exception {
        createUser("test@example.com");

        PasswordChangeRequest request = new PasswordChangeRequest("currentPassword123!", "weak");

        patch(API_CHANGE_PASSWORD, request, status().isBadRequest());
    }

    @Test
    void shouldRequestActivationCodeSuccessfully() throws Exception {
        UserEntity user = createNonActiveUser("test@example.com");
        String activationCode = user.getUserCredentials().getActivationCode();

        doNothing().when(notificationSenderPort).sendActivationNotification(any());

        postText(API_RESEND_ACTIVATION, "test@example.com", status().isOk());

        // Verify the new activation code was generated
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserCredentials().getActivationCode()).isNotEqualTo(activationCode);
        verify(notificationSenderPort).sendActivationNotification(any());
    }

    @Test
    void shouldNotSendActivationKeyForActivatedUser() throws Exception {
        createUser("test@example.com");

        postText(API_RESEND_ACTIVATION,
                 "test@example.com",
                 status().isOk()); // Should still return OK for security

        verify(notificationSenderPort, never()).sendActivationNotification(any());
    }

    @Test
    void shouldHandleRequestActivationCodeForNonExistentEmail() throws Exception {
        postText(API_RESEND_ACTIVATION,
                 "nonexistent@example.com",
                 status().isBadRequest());

        verify(notificationSenderPort, never()).sendActivationNotification(any());
    }

    @Test
    void shouldRequestPasswordResetSuccessfully() throws Exception {
        UserEntity user = createUser("test@example.com");

        doNothing().when(notificationSenderPort).sendPasswordResetNotification(any());

        postText(API_RESET_PASSWORD_INIT, "test@example.com", status().isOk());

        // Verify that the reset code was set
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getUserCredentials().getResetCode()).isNotNull();
        assertThat(updatedUser.getUserCredentials().getResetDate()).isNotNull();

        verify(notificationSenderPort).sendPasswordResetNotification(any());
    }

    @Test
    void shouldNotRequestPasswordResetForNonActivatedUser() throws Exception {
        createNonActiveUser("test@example.com");

        postText(API_RESET_PASSWORD_INIT,
                 "test@example.com",
                 status().isOk()); // Should still return OK for security

        verify(notificationSenderPort, never()).sendPasswordResetNotification(any());
    }

    @Test
    void shouldHandlePasswordResetForNonExistentEmail() throws Exception {
        postText(API_RESET_PASSWORD_INIT,
                 "nonexistent@example.com",
                 status().isOk()); // Should still return OK for security

        verify(notificationSenderPort, never()).sendPasswordResetNotification(any());
    }

    @Test
    void shouldFinishPasswordResetSuccessfully() throws Exception {
        UserEntity user = createUser("test@example.com");
        user.getUserCredentials().setResetCode("C0DEAZ");
        user.getUserCredentials().setResetDate(Instant.now());
        userRepository.save(user);

        PasswordResetRequest request = new PasswordResetRequest(user.getUserCredentials().getResetCode(), "newPassword123!");

        post(API_RESET_PASSWORD_FINISH, request, status().isOk());

        // Verify that the password was reset and keys cleared
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(request.newPassword(), updatedUser.getUserCredentials().getPasswordHash())).isTrue();
        assertThat(updatedUser.getUserCredentials().getResetCode()).isNull();
        assertThat(updatedUser.getUserCredentials().getResetDate()).isNotNull();
    }

    @Test
    void shouldFailToFinishPasswordResetWithInvalidCode() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest("invalid-code", "newPassword123!");

        post(API_RESET_PASSWORD_FINISH, request, status().isBadRequest());
    }

    @Test
    void shouldFailToFinishPasswordResetWithExpiredCode() throws Exception {
        UserEntity user = createUser("test@example.com");
        user.getUserCredentials().setResetCode("expired-code");
        user.getUserCredentials().setResetDate(Instant.now().minus(2, ChronoUnit.DAYS)); // Expired
        userRepository.save(user);

        PasswordResetRequest request = new PasswordResetRequest(user.getUserCredentials().getResetCode(), "newPassword123!");

        post(API_RESET_PASSWORD_FINISH, request, status().isBadRequest());
    }

    @Test
    void shouldFailToFinishPasswordResetWithInvalidPassword() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest("valid-code", "weak");

        post(API_RESET_PASSWORD_FINISH, request, status().isBadRequest());
    }

    @Test
    void shouldPerformCompleteRegistrationAndActivationFlow() throws Exception {
        // 1. Register user
        CreateUserRequest registerRequest = createValidUserRequest();
        doNothing().when(notificationSenderPort).sendActivationNotification(any());

        post(API_REGISTER, registerRequest, status().isCreated());

        UserEntity createdUser = userRepository.findOneByUserCredentialsEmailIgnoreCase(registerRequest.email()).orElseThrow();
        assertThat(createdUser.getStatus()).isEqualTo(UserStatus.NOT_ACTIVATED);

        // 2. Activate user
        String activationCode = createdUser.getUserCredentials().getActivationCode();
        get(API_ACTIVATE + "?code=" + activationCode, status().isOk());

        // Verify user is activated
        UserEntity activatedUser = userRepository.findOneByUserCredentialsEmailIgnoreCase(registerRequest.email()).orElseThrow();
        assertThat(activatedUser.isActivated()).isTrue();
        assertThat(activatedUser.getUserCredentials().getActivationCode()).isNull();
        assertThat(activatedUser.getUserCredentials().getActivationDate()).isNotNull();

        verify(notificationSenderPort).sendActivationNotification(any());
    }

    @Test
    void shouldPerformCompletePasswordResetFlow() throws Exception {
        UserEntity user = createUser("test@example.com");

        // 1. Request password reset
        doNothing().when(notificationSenderPort).sendPasswordResetNotification(any());

        postText(API_RESET_PASSWORD_INIT, "test@example.com", status().isOk());

        // Get the reset code that was generated
        UserEntity userWithResetCode = userRepository.findById(user.getId()).orElseThrow();
        String resetCode = userWithResetCode.getUserCredentials().getResetCode();

        // 2. Finish password reset
        PasswordResetRequest resetRequest = new PasswordResetRequest(resetCode, "newPassword123!");

        post(API_RESET_PASSWORD_FINISH, resetRequest, status().isOk());

        // Verify password was changed
        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword123!", updatedUser.getUserCredentials().getPasswordHash())).isTrue();
        assertThat(updatedUser.getUserCredentials().getResetCode()).isNull();
        assertThat(updatedUser.getUserCredentials().getResetDate()).isNotNull();

        verify(notificationSenderPort).sendPasswordResetNotification(any());
    }

    @Test
    void shouldCompleteInvitationSuccessfully() throws Exception {
        UserEntity user = createNonActiveUser("invited@example.com");
        user.getUserCredentials().setActivationCode(null);
        user.getUserCredentials().setResetCode("INVIT00A");
        user.getUserCredentials().setResetDate(Instant.now());
        userRepository.save(user);

        InvitationCompleteRequest request = new InvitationCompleteRequest("INVIT00A", "NewSecurePass1!");

        post(API_INVITATION_COMPLETE, request, status().isOk());

        UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.ACTIVATED);
        assertThat(passwordEncoder.matches("NewSecurePass1!", updatedUser.getUserCredentials().getPasswordHash())).isTrue();
        assertThat(updatedUser.getUserCredentials().getResetCode()).isNull();
        assertThat(updatedUser.getUserCredentials().getActivationDate()).isNotNull();
    }

    @Test
    void shouldFailToCompleteInvitationWithInvalidCode() throws Exception {
        InvitationCompleteRequest request = new InvitationCompleteRequest("BADCODE1", "NewSecurePass1!");

        post(API_INVITATION_COMPLETE, request, status().isBadRequest());
    }

    @Test
    void shouldFailToCompleteInvitationWithExpiredCode() throws Exception {
        UserEntity user = createNonActiveUser("invited@example.com");
        user.getUserCredentials().setActivationCode(null);
        user.getUserCredentials().setResetCode("EXPCODE1");
        user.getUserCredentials().setResetDate(Instant.now().minus(2, ChronoUnit.DAYS));
        userRepository.save(user);

        InvitationCompleteRequest request = new InvitationCompleteRequest("EXPCODE1", "NewSecurePass1!");

        post(API_INVITATION_COMPLETE, request, status().isBadRequest());

        UserEntity unchanged = userRepository.findById(user.getId()).orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(UserStatus.NOT_ACTIVATED);
    }

    @Test
    void shouldFailToCompleteInvitationForAlreadyActiveUser() throws Exception {
        UserEntity user = createUser("active@example.com");
        user.getUserCredentials().setResetCode("ACTCODE1");
        user.getUserCredentials().setResetDate(Instant.now());
        userRepository.save(user);

        InvitationCompleteRequest request = new InvitationCompleteRequest("ACTCODE1", "NewSecurePass1!");

        post(API_INVITATION_COMPLETE, request, status().isBadRequest());

        UserEntity unchanged = userRepository.findById(user.getId()).orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(UserStatus.ACTIVATED);
    }

    @Test
    void shouldFailToCompleteInvitationWithInvalidPassword() throws Exception {
        InvitationCompleteRequest request = new InvitationCompleteRequest("ANYCODE1", "weak");

        post(API_INVITATION_COMPLETE, request, status().isBadRequest());
    }

    @Test
    void shouldPerformCompleteInvitationFlow() throws Exception {
        // 1. Setup: managed user with invitation code (no activation code, reset code set)
        UserEntity user = createNonActiveUser("managed@example.com");
        user.getUserCredentials().setActivationCode(null);
        user.getUserCredentials().setResetCode("FLOW0001");
        user.getUserCredentials().setResetDate(Instant.now());
        userRepository.save(user);

        assertThat(userRepository.findById(user.getId()).orElseThrow().getStatus())
                .isEqualTo(UserStatus.NOT_ACTIVATED);

        // 2. Complete invitation by setting the chosen password
        String chosenPassword = "MyNewPass1!";
        InvitationCompleteRequest request = new InvitationCompleteRequest("FLOW0001", chosenPassword);
        post(API_INVITATION_COMPLETE, request, status().isOk());

        // 3. Verify account is now activated and password is usable
        UserEntity activatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(activatedUser.getStatus()).isEqualTo(UserStatus.ACTIVATED);
        assertThat(activatedUser.getUserCredentials().getResetCode()).isNull();
        assertThat(activatedUser.getUserCredentials().getActivationDate()).isNotNull();
        assertThat(passwordEncoder.matches(chosenPassword, activatedUser.getUserCredentials().getPasswordHash())).isTrue();
    }

    private CreateUserRequest createValidUserRequest() {

        return new CreateUserRequest("test@example.com",
                                     "validPassword123!",
                                     "Mamadou",
                                     "Diallo",
                                     java.time.LocalDate.of(1990, 1, 1),
                                     UserGender.MALE,
                                     null,
                                     null,
                                     null,
                                     null);
    }

    private UpdateUserRequest createValidUpdateRequest() {
        return new UpdateUserRequest("Updated",
                                     "User",
                                     null,
                                     null,
                                     null,
                                     null);
    }

    private RoleGroupEntity createRoleGroupWithPermissions(String name, Set<String> permissionCodes) {
        Set<PermissionEntity> permissions = new HashSet<>();
        for (String code : permissionCodes) {
            permissions.add(permissionRepository.findById(code).orElseThrow());
        }
        RoleGroupEntity entity = new RoleGroupEntity(null, name, name + " description", permissions);
        entity.setCreationDate(Instant.now());
        entity.setLastUpdateDate(Instant.now());
        entity.setLastUpdatedBy("test");
        return roleGroupRepository.save(entity);
    }
}
