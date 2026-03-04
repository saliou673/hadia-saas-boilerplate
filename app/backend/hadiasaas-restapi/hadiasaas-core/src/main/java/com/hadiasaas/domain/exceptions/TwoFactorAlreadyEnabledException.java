package com.hadiasaas.domain.exceptions;

/**
 * Thrown when a user tries to set up 2FA but it is already enabled.
 */
public class TwoFactorAlreadyEnabledException extends FunctionalException {
    public TwoFactorAlreadyEnabledException() {
        super("Two-factor authentication is already enabled for this account");
    }
}
