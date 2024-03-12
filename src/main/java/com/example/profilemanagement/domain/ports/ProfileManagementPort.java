package com.example.profilemanagement.domain.ports;

import com.example.profilemanagement.application.dtos.UserProfileRequest;
import com.example.profilemanagement.application.dtos.UserProfileResponse;
import com.example.profilemanagement.domain.exceptions.UserProfileEntityException;
import com.example.profilemanagement.domain.exceptions.InvalidEmailFormatException;

/**
 * Defines the contract for profile management operations such as updating user information
 * and initiating a password reset process.
 */
public interface ProfileManagementPort {

    UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest) throws UserProfileEntityException;

    void initiatePasswordReset(String email) throws InvalidEmailFormatException;
}
