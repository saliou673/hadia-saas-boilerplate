package com.maitrisetcf.domain.exceptions;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
        super("User already activated");
    }
}
