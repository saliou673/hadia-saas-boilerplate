package com.maitrisetcf.domain.exceptions;

/**
 * Thrown when a role group cannot be found by the given identifier or name.
 */
public class RoleGroupNotFoundException extends FunctionalException {
    public RoleGroupNotFoundException(String message) {
        super(message);
    }
}
