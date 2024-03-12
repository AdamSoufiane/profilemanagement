package com.example.profilemanagement.adapters.primary;

import com.example.profilemanagement.adapters.secondary.EmailServiceClient;
import com.example.profilemanagement.application.dtos.PasswordUpdateRequest;
import com.example.profilemanagement.application.dtos.PasswordUpdateResponse;
import com.example.profilemanagement.application.dtos.UserProfileRequest;
import com.example.profilemanagement.application.dtos.UserProfileResponse;
import com.example.profilemanagement.application.exceptions.UserProfileUseCaseException;
import com.example.profilemanagement.application.use_cases.PasswordResetUseCase;
import com.example.profilemanagement.application.use_cases.UserProfileUseCase;
import com.example.profilemanagement.domain.entities.UserProfileEntity;
import com.example.profilemanagement.domain.ports.UserProfileRepositoryPort;
import com.example.profilemanagement.domain.services.TokenService;
import com.example.profilemanagement.adapters.primary.exceptions.ProfileControllerException;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Optional;

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
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) throws ProfileControllerException {
        Optional<UserProfileEntity> userProfile = userProfileRepositoryPort.findById(userId);
        return userProfile.map(profile -> ResponseEntity.ok(new UserProfileResponse(profile))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping(path = "/profiles/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest userProfileRequest) throws ProfileControllerException {
        if (!userId.equals(userProfileRequest.getUserId())) {
            return ResponseEntity.badRequest().body(new UserProfileResponse("User ID in the path and body do not match."));
        }
        try {
            UserProfileResponse updatedProfile = userProfileUseCase.updateUserProfile(userProfileRequest);
            return ResponseEntity.ok(updatedProfile);
        } catch (UserProfileUseCaseException e) {
            throw new ProfileControllerException(e.getMessage(), e);
        }
    }

    @PostMapping(path = "/password-reset", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> sendPasswordResetToken(@RequestParam String email) throws EmailServiceClientException {
        String token = tokenService.generateResetToken(email);
        emailServiceClient.sendPasswordResetLink(email, token);
        return ResponseEntity.accepted().body("Password reset token sent successfully.");
    }

    @PostMapping(path = "/password-update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PasswordUpdateResponse> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) throws ProfileControllerException {
        try {
            PasswordUpdateResponse response = passwordResetUseCase.updatePassword(passwordUpdateRequest.getUserId(), passwordUpdateRequest.getToken(), passwordUpdateRequest.getNewPassword());
            return ResponseEntity.ok(response);
        } catch (UserProfileUseCaseException e) {
            throw new ProfileControllerException(e.getMessage(), e);
        }
    }

    @ExceptionHandler(ProfileControllerException.class)
    public ResponseEntity<String> handleProfileControllerException(ProfileControllerException e) {
        logger.error("ProfileControllerException occurred", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(EmailServiceClientException.class)
    public ResponseEntity<String> handleEmailServiceClientException(EmailServiceClientException e) {
        logger.error("EmailServiceClientException occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email service error: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Unhandled exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}
