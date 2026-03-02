package com.maitrisetcf.domain.models.user;

import com.maitrisetcf.domain.exceptions.UserAlreadyActivatedException;
import com.maitrisetcf.domain.models.DomainValidation;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;

import static com.maitrisetcf.domain.models.DomainValidation.checkRequiredField;

@Getter
public class UserCredentials {

    private final Email email;
    private String passwordHash;
    private String activationCode;
    private Instant activationDate;
    private String resetCode;
    private Instant resetDate;

    public UserCredentials(
            String email,
            String passwordHash,
            String activationCode,
            Instant activationDate,
            String resetCode,
            Instant resetDate
    ) {
        this.email = new Email(email);

        checkRequiredField(passwordHash, "passwordHash");

        this.passwordHash = passwordHash;
        this.activationCode = activationCode;
        this.activationDate = activationDate;
        this.resetCode = resetCode;
        this.resetDate = resetDate;
    }

    protected void changePassword(String newPasswordHash, Instant resetDate) {
        checkRequiredField(newPasswordHash, "passwordHash");
        this.passwordHash = newPasswordHash;
        this.resetDate = resetDate;
        this.resetCode = null;
    }

    protected void hashPassword(String newPasswordHash) {
        checkRequiredField(newPasswordHash, "passwordHash");
        this.passwordHash = newPasswordHash;
    }

    protected void activate(Instant activationDate) {
        if (this.activationDate != null) {
            throw new UserAlreadyActivatedException();
        }
        this.activationDate = activationDate;
        this.activationCode = null;
    }

    protected void updateActivationCode(String newActivationCode) {
        DomainValidation.checkRequiredField(newActivationCode, "activationCode");
        this.activationCode = newActivationCode;
        this.activationDate = null;
    }

    protected void updateResetCode(String newResetCode, @NonNull Instant resetDate) {
        checkRequiredField(newResetCode, "resetCode");
        this.resetCode = newResetCode;
        this.resetDate = resetDate;
    }

    public String getEmail() {
        return email.value();
    }

}
