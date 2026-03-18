package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a user attempts to log in but 2FA is globally required
 * and the user has not yet configured 2FA on their account.
 */
public class TwoFactorSetupRequiredException extends AuthFunctionalException {
    public TwoFactorSetupRequiredException(String message) {
        super(message);
    }
}
