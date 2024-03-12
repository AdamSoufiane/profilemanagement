package com.example.profilemanagement.adapters.primary;

import com.example.profilemanagement.adapters.secondary.EmailServiceClient;
import com.example.profilemanagement.application.dtos.PasswordUpdateRequest;
import com.example.profilemanagement.application.dtos.PasswordUpdateResponse;
import com.example.profilemanagement.application.dtos.UserProfileRequest;
import com.example.profilemanagement.application.dtos.UserProfileResponse;
import com.example.profilemanagement.application.exceptions.UserProfileUseCaseException;
import com.example.profilemanagement.application.use_cases.PasswordResetUseCase;
import com.example.profilemanagement.application.use_cases.UserProfileUseCase;
import com.example.profilemanagement.domain.ports.UserProfileRepositoryPort;
import com.example.profilemanagement.domain.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserProfileUseCase userProfileUseCase;
    private final PasswordResetUseCase passwordResetUseCase;
    private final EmailServiceClient emailServiceClient;
    private final TokenService tokenService;
    private final UserProfileRepositoryPort userProfileRepositoryPort;

    @GetMapping(path = "/profiles/{userId}", produces = "application/json")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        Optional<UserProfileResponse> userProfileResponse = userProfileRepositoryPort.findById(userId);
        return userProfileResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping(path = "/profiles/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest userProfileRequest) {
        // TODO: Implement authorization check
        if (!userId.equals(userProfileRequest.getUserId())) {
            return ResponseEntity.badRequest().body("User ID in the path and body do not match.");
        }
        UserProfileResponse updatedProfile = userProfileUseCase.updateUserProfile(userProfileRequest);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping(path = "/password-reset", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> sendPasswordResetToken(@RequestParam String email) {
        // TODO: Implement email format validation
        try {
            String token = tokenService.generateResetToken(email);
            emailServiceClient.sendPasswordResetLink(email, token);
            return ResponseEntity.accepted().body("Password reset token sent successfully.");
        } catch (EmailServiceClientException e) {
            logger.error("Failed to send password reset token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending password reset token.");
        }
    }

    @PostMapping(path = "/password-update/{userId}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PasswordUpdateResponse> updatePassword(@PathVariable Long userId, @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        // TODO: Implement authorization check
        if (!userId.equals(passwordUpdateRequest.getUserId())) {
            return ResponseEntity.badRequest().body("User ID in the path and body do not match.");
        }
        if (!isValidPassword(passwordUpdateRequest.getNewPassword())) {
            return ResponseEntity.badRequest().body("Password does not meet the security standards.");
        }
        PasswordUpdateResponse response = passwordResetUseCase.updatePassword(userId, passwordUpdateRequest.getNewPassword());
        return ResponseEntity.ok(response);
    }

    private boolean isValidPassword(String password) {
        return StringUtils.isNotBlank(password) &&
               password.length() >= 8 &&
               StringUtils.containsAny(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ") &&
               StringUtils.containsAny(password, "abcdefghijklmnopqrstuvwxyz") &&
               StringUtils.containsAny(password, "0123456789") &&
               StringUtils.containsAny(password, "!@#$%^&*()_+-=[]{}|;:',.<>/?");
    }

    @ExceptionHandler(UserProfileUseCaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUserProfileUseCaseException(UserProfileUseCaseException e) {
        logger.error("UserProfileUseCaseException occurred", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EmailServiceClientException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleEmailServiceClientException(EmailServiceClientException e) {
        logger.error("EmailServiceClientException occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email service error: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Unhandled exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}
