package com.maitrisetcf.domain.exceptions;

public class UserNotFoundException extends FunctionalException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
