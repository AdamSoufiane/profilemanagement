package com.example.profilemanagement.application.use_cases;

import com.example.profilemanagement.domain.ports.ProfileManagementPort;
import com.example.profilemanagement.domain.services.PasswordResetService;
import com.example.profilemanagement.application.exceptions.UserProfileUseCaseException;
import com.example.profilemanagement.domain.exceptions.InvalidEmailFormatException;
import com.example.profilemanagement.domain.exceptions.InvalidPasswordException;
import com.example.profilemanagement.domain.exceptions.InvalidTokenException;
import com.example.profilemanagement.domain.services.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PasswordResetUseCase {

    private final ProfileManagementPort profileManagementPort;
    private final PasswordResetService passwordResetService;
    private final UserVerificationService userVerificationService;

    public void initiatePasswordReset(UUID userId, String email) throws UserProfileUseCaseException {
        if (!userVerificationService.existsById(userId)) {
            throw new UserProfileUseCaseException("User ID does not exist.");
        }

        try {
            profileManagementPort.initiatePasswordReset(email);
            log.info("Password reset initiated for user: {}", userId);
        } catch (InvalidEmailFormatException e) {
            log.error("Invalid email format for user: {}", userId, e);
            throw new UserProfileUseCaseException("Invalid email format", e);
        } catch (Exception e) {
            log.error("Failed to initiate password reset for user: {}", userId, e);
            throw new UserProfileUseCaseException("Failed to initiate password reset", e);
        }
    }

    public boolean validateResetToken(UUID userId, String token) throws UserProfileUseCaseException {
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

    public void updatePassword(UUID userId, String token, String newPassword) throws UserProfileUseCaseException {
        if (!userVerificationService.existsById(userId)) {
            throw new UserProfileUseCaseException("User ID does not exist.");
        }
        if (!passwordResetService.validateResetToken(token)) {
            throw new InvalidTokenException("Invalid or expired reset token.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new InvalidPasswordException("Password does not meet security requirements.");
        }
        try {
            passwordResetService.resetPassword(token, newPassword);
            log.info("Password updated for user: {}", userId);
        } catch (InvalidPasswordException e) {
            log.error("Invalid password for user: {}", userId, e);
            throw new UserProfileUseCaseException("Invalid password: " + e.getMessage(), e);
        } catch (InvalidTokenException e) {
            log.error("Invalid token for user: {}", userId, e);
            throw new UserProfileUseCaseException("Invalid token: " + e.getMessage(), e);
        }
    }
}
