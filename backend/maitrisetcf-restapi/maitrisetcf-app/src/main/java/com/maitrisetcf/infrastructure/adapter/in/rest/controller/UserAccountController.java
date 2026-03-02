package com.maitrisetcf.infrastructure.adapter.in.rest.controller;


import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.models.user.UserInfoUpdate;
import com.maitrisetcf.domain.ports.in.AccountUseCase;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.PermissionDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto.UserSummaryDTO;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.CreateUserRequestMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.PermissionDtoMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.UpdateUserRequestMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.mapper.UserDtoMapper;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;


/**
 * REST controller for managing the current user's account.
 */
@Validated
@RestController
@Tag(name = "User account management")
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final AccountUseCase accountUseCase;
    private final CreateUserRequestMapper createUserRequestMapper;
    private final UserDtoMapper userDtoMapper;
    private final UpdateUserRequestMapper updateUserRequestMapper;
    private final PermissionDtoMapper permissionDtoMapper;

    /**
     * Creates a new public user account.
     *
     * @param request the request body containing the user information.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPublicUserAccount(@Valid @RequestBody CreateUserRequest request) {
        accountUseCase.createPublicUser(createUserRequestMapper.toDomain(request));
    }

    /**
     * Activate a registered user.
     *
     * @param code the activation code (OTP).
     */
    @GetMapping("/activation")
    public void activateAccount(@RequestParam String code) {
        accountUseCase.activateRegistration(code);
    }

    /**
     * Send new confirmation email to user.
     *
     * @param email the mail of the user.
     */
    @PostMapping(path = "/activation/resend", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void requestActivationCode(@RequestBody String email) {
        accountUseCase.sendActivationCode(email);
    }

    /**
     * Get the current user.
     */
    @GetMapping("/me")
    public UserSummaryDTO getUserDetails() {
        User user = accountUseCase.getCurrentUserWithAuthorities();
        return userDtoMapper.toSummaryDTO(user);
    }

    /**
     * Get resolved permissions of the current user (flattened from role groups).
     */
    @GetMapping("/me/permissions")
    public List<PermissionDTO> getCurrentUserPermissions() {
        return accountUseCase.getCurrentUserWithAuthorities()
                .resolvePermissions()
                .stream()
                .sorted(Comparator.comparing(Permission::code))
                .map(permissionDtoMapper::toDTO)
                .toList();
    }

    /**
     * Update the current user information.
     */
    @PutMapping("/me")
    public UserSummaryDTO updateAccount(@Valid @RequestBody UpdateUserRequest request) {
        UserInfoUpdate infoUpdate = updateUserRequestMapper.toDomain(request);
        return userDtoMapper.toSummaryDTO(accountUseCase.updateCurrentUser(infoUpdate));
    }

    /**
     * Soft-delete the current user account.
     */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentAccount() {
        accountUseCase.deleteCurrentUserAccount();
    }

    /**
     * Recover a previously soft-deleted account.
     *
     * @param request account credentials.
     */
    @PostMapping(path = "/recover")
    @ResponseStatus(HttpStatus.OK)
    public void recoverAccount(@Valid @RequestBody RecoverAccountRequest request) {
        accountUseCase.recoverAccount(request.email(), request.password());
    }


    /**
     * Changes the current user's password.
     *
     * @param request current and new password.
     */
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody PasswordChangeRequest request) {
        accountUseCase.changePassword(request.currentPassword(), request.newPassword());
    }

    /**
     * Send email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/reset-password/init", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void requestPasswordReset(@RequestBody String mail) {
        accountUseCase.requestPasswordReset(mail);
    }

    /**
     * Finish resetting the password of the user.
     *
     * @param request the generated code and the new password.
     */
    @PostMapping(path = "/reset-password/finish")
    @ResponseStatus(HttpStatus.OK)
    public void finishPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        accountUseCase.completePasswordReset(request.newPassword(), request.code());
    }

    /**
     * Complete a managed user invitation by setting the initial password.
     *
     * @param request the invitation code and the chosen password.
     */
    @PostMapping(path = "/invitation/complete")
    @ResponseStatus(HttpStatus.OK)
    public void completeInvitation(@RequestBody @Valid InvitationCompleteRequest request) {
        accountUseCase.completeInvitation(request.code(), request.newPassword());
    }

}
