package com.example.profilemanagement.adapters.secondary;

import com.example.profilemanagement.application.dtos.UserProfileRequest;
import com.example.profilemanagement.application.dtos.UserProfileResponse;
import com.example.profilemanagement.domain.entities.UserProfileEntity;
import com.example.profilemanagement.domain.exceptions.UserProfileEntityException;
import com.example.profilemanagement.domain.ports.ProfileManagementPort;
import com.example.profilemanagement.domain.ports.UserProfileRepositoryPort;
import com.example.profilemanagement.infrastructure.exceptions.EmailServiceClientException;
import com.example.profilemanagement.infrastructure.security.TokenGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileManagementAdapter implements ProfileManagementPort {

    private final UserProfileRepositoryPort userRepository;
    private final EmailServiceClient emailServiceClient;
    private final TokenGenerationService tokenGenerationService;

    @Override
    public UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest) throws UserProfileEntityException {
        UserProfileEntity entity = userRepository.findById(userProfileRequest.getId())
                .orElseThrow(() -> new UserProfileEntityException("User profile not found."));

        if (!entity.getVersion().equals(userProfileRequest.getVersion())) {
            throw new UserProfileEntityException("Optimistic locking failed: versions do not match.");
        }

        entity.setName(userProfileRequest.getName());
        entity.setEmail(userProfileRequest.getEmail());
        entity.setPassword(userProfileRequest.getPassword());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setVersion(entity.getVersion() + 1);

        UserProfileEntity savedEntity = userRepository.save(entity);
        return new UserProfileResponse(savedEntity.getId(), savedEntity.getName(), savedEntity.getEmail());
    }

    @Override
    public void initiatePasswordReset(String email) throws EmailServiceClientException, UserProfileEntityException {
        UserProfileEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserProfileEntityException("User profile with email " + email + " not found."));

        String token = tokenGenerationService.generateToken();

        try {
            emailServiceClient.sendPasswordResetLink(email, token);
        } catch (EmailServiceClientException e) {
            log.error("Failed to send password reset link", e);
            throw e;
        }
    }

    @Override
    public void deleteUserProfile(Long id) throws UserProfileEntityException {
        userRepository.findById(id)
                .orElseThrow(() -> new UserProfileEntityException("User profile not found."));

        userRepository.deleteById(id);
    }

    @Override
    public UserProfileResponse getUserProfile(Long id) throws UserProfileEntityException {
        UserProfileEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserProfileEntityException("User profile not found."));

        return new UserProfileResponse(entity.getId(), entity.getName(), entity.getEmail());
    }
}
