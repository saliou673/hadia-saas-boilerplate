package com.maitrisetcf.domain.exceptions;

public class UserAlreadyExistsException extends FunctionalException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
