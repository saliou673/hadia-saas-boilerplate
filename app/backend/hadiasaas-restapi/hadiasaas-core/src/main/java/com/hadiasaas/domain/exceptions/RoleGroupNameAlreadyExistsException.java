package com.hadiasaas.domain.exceptions;

/**
 * Thrown when attempting to create or rename a role group to a name that is already taken.
 */
public class RoleGroupNameAlreadyExistsException extends FunctionalException {
    public RoleGroupNameAlreadyExistsException(String message) {
        super(message);
    }
}
