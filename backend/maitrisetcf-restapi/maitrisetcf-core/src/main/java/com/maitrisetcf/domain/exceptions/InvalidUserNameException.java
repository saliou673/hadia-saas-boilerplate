package com.maitrisetcf.domain.exceptions;

public class InvalidUserNameException extends RuntimeException {
    public InvalidUserNameException(String message) {
        super(message);
    }
}
