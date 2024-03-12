package com.example.profilemanagement.application.use_cases;

import com.example.profilemanagement.domain.ports.ProfileManagementPort;
import com.example.profilemanagement.domain.services.PasswordResetService;
import com.example.profilemanagement.application.exceptions.UserProfileUseCaseException;
import com.example.profilemanagement.domain.exceptions.InvalidPasswordException;
import com.example.profilemanagement.domain.exceptions.InvalidTokenException;
import com.example.profilemanagement.domain.services.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Component
public class PasswordResetUseCase {

    private final ProfileManagementPort profileManagementPort;
    private final PasswordResetService passwordResetService;
    private final UserVerificationService userVerificationService;
    private static final Logger log = LoggerFactory.getLogger(PasswordResetUseCase.class);

    public void initiatePasswordReset(Long userId, String email) throws UserProfileUseCaseException {
        if (!userVerificationService.existsById(userId)) {
            throw new UserProfileUseCaseException("User ID does not exist.");
        }

        if (!validateEmail(email)) {
            throw new UserProfileUseCaseException("Invalid email format.");
        }

        try {
            profileManagementPort.initiatePasswordReset(email);
            log.info("Password reset initiated for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to initiate password reset for user: {}", userId, e);
            throw new UserProfileUseCaseException("Failed to initiate password reset", e);
        }
    }

    public boolean validateResetToken(Long userId, String token) throws UserProfileUseCaseException {
        if (!userVerificationService.existsById(userId)) {
            throw new UserProfileUseCaseException("User ID does not exist.");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new UserProfileUseCaseException("Invalid reset token");
        }

        boolean isValid = passwordResetService.validateResetToken(token);
        if (!isValid) {
            log.error("Invalid reset token for user: {}", userId);
            throw new UserProfileUseCaseException("Invalid reset token");
        }

        log.info("Reset token validated for user: {}", userId);
        return isValid;
    }

    public void updatePassword(Long userId, String token, String newPassword) throws UserProfileUseCaseException {
        if (!userVerificationService.existsById(userId)) {
            throw new UserProfileUseCaseException("User ID does not exist.");
        }

        if (!validateResetToken(userId, token)) {
            throw new UserProfileUseCaseException("Invalid or expired reset token.");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new UserProfileUseCaseException("Password does not meet security requirements.");
        }

        try {
            passwordResetService.resetPassword(token, newPassword);
            log.info("Password updated for user: {}", userId);
        } catch (InvalidPasswordException | InvalidTokenException e) {
            log.error("Password update failed for user: {}", userId, e);
            throw new UserProfileUseCaseException("Password update failed: " + e.getMessage(), e);
        }
    }

    private boolean validateEmail(String email) {
        // Assuming a simple email validation logic for demonstration purposes
        return email != null && email.matches("[\w.%+-]+@[\w.-]+\.[a-zA-Z]{2,6}");
    }
}
