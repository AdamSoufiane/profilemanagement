package com.example.profilemanagement.domain.ports;

import com.example.profilemanagement.application.dtos.UserProfileRequest;
import com.example.profilemanagement.application.dtos.UserProfileResponse;
import com.example.profilemanagement.domain.exceptions.InvalidEmailFormatException;
import com.example.profilemanagement.domain.exceptions.UserProfileEntityException;

/**
 * Defines the contract for profile management operations such as updating user information
 * and initiating a password reset process.
 */
public interface ProfileManagementPort {

    /**
     * Synchronously updates the user profile with the provided information and returns the updated profile.
     * Before processing, validates the UserProfileRequest to ensure all necessary information is present and correct.
     * The user must already exist in the system to update the profile.
     *
     * @param userProfileRequest the user profile information to update
     * @return the updated user profile
     * @throws UserProfileEntityException if the validation of the user profile data fails
     */
    UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest) throws UserProfileEntityException;

    /**
     * Synchronously initiates the password reset process for the user associated with the given email address.
     * Validates the email format before initiating the process.
     * The email must be registered in the system to initiate a password reset.
     *
     * @param email the email address to initiate the password reset process for
     * @throws InvalidEmailFormatException if the email format is invalid
     */
    void initiatePasswordReset(String email) throws InvalidEmailFormatException;
}
