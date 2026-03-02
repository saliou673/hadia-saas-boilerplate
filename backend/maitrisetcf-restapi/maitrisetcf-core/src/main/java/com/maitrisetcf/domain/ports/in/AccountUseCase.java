package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.models.user.UserInfoUpdate;

import java.util.Set;

public interface AccountUseCase {

    void createPublicUser(User user);

    void createUser(User user, Set<String> roleGroupNames);

    User createManagedUser(User user, Set<String> roleGroupNames);

    void activateRegistration(String activationCode);

    void completePasswordReset(String newPassword, String resetCode);

    void completeInvitation(String invitationCode, String newPassword);

    void requestPasswordReset(String email);

    void sendActivationCode(String email);

    User updateUser(String email, UserInfoUpdate update);

    void changePassword(String currentPassword, String newPassword);

    User getCurrentUserWithAuthorities();

    User getUserWithAuthoritiesById(Long id);

    User updateCurrentUser(UserInfoUpdate update);

    User updateUserById(Long id, UserInfoUpdate update);

    void deleteUserById(Long id);

    void removeNotActivatedUsers();

    void deleteCurrentUserAccount();

    void recoverAccount(String email, String password);

    void removeSoftDeletedUsers();
}
