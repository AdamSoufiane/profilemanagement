package com.example.profilemanagement.application.exceptions;

public class UserProfileUseCaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserProfileUseCaseException(String message) {
        super(message);
    }

    public UserProfileUseCaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
