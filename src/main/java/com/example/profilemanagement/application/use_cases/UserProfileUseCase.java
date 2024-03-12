package com.example.profilemanagement.application.use_cases;

import com.example.profilemanagement.application.dtos.UserProfileRequest;
import com.example.profilemanagement.application.dtos.UserProfileResponse;
import com.example.profilemanagement.application.exceptions.UserProfileUseCaseException;
import com.example.profilemanagement.domain.exceptions.UserProfileEntityException;
import com.example.profilemanagement.domain.ports.ProfileManagementPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service coordinating the actions for managing user profiles, such as updating personal information.
 * This class provides a high-level abstraction for user profile operations, ensuring that
 * the domain logic is properly encapsulated and that the system remains flexible to adapt to future changes.
 */
@Service
@RequiredArgsConstructor
public class UserProfileUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileUseCase.class);

    private final ProfileManagementPort profileManagementPort;

    /**
     * Updates the user profile with the provided information from userProfileRequest and returns the updated profile.
     * It may throw a UserProfileUseCaseException if the update process fails due to validation errors, persistence issues, or other update-related problems.
     *
     * @param userProfileRequest The user profile information to update
     * @return The updated user profile
     * @throws UserProfileUseCaseException if the update process fails
     */
    public UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest) throws UserProfileUseCaseException {
        if (userProfileRequest == null) {
            logger.error("updateUserProfile was called with null userProfileRequest");
            throw new IllegalArgumentException("UserProfileRequest cannot be null");
        }

        try {
            UserProfileResponse response = profileManagementPort.updateUserProfile(userProfileRequest);
            logger.info("User profile updated successfully for user: {}", userProfileRequest.getUserId());
            return response;
        } catch (UserProfileEntityException e) {
            logger.error("Failed to update user profile for user: {}", userProfileRequest.getUserId(), e);
            throw new UserProfileUseCaseException("Failed to update user profile", e);
        }
    }
}
