package com.example.profilemanagement.application.dtos;

import lombok.Getter;
import lombok.Setter;

/**
 * This class serves as a Data Transfer Object (DTO) for responses related to password update requests.
 * It encapsulates the status message of the password update process.
 */
@Getter
@Setter
public class PasswordUpdateResponse {

    /**
     * Message indicating the result of the password update operation.
     */
    private String message;

    /**
     * Constructor for creating a PasswordUpdateResponse with a message.
     *
     * @param message The message indicating the result of the password update operation.
     */
    public PasswordUpdateResponse(String message) {
        this.message = message;
    }
}
