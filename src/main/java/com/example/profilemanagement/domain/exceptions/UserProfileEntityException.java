package com.example.profilemanagement.domain.exceptions;

import java.io.Serializable;

public class UserProfileEntityException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserProfileEntityException() {
        super();
    }

    public UserProfileEntityException(String message) {
        super(message);
    }

    public UserProfileEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
