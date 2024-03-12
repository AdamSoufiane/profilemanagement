package com.example.profilemanagement.domain.services;

import com.example.profilemanagement.adapters.secondary.EmailServiceClient;
import com.example.profilemanagement.domain.entities.UserProfileEntity;
import com.example.profilemanagement.domain.exceptions.InvalidPasswordException;
import com.example.profilemanagement.domain.exceptions.InvalidTokenException;
import com.example.profilemanagement.domain.exceptions.UserNotFoundException;
import com.example.profilemanagement.domain.ports.UserProfileRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserProfileRepositoryPort userRepositoryPort;
    private final EmailServiceClient emailServiceClient;

    public String generateResetToken(String email) {
        Optional<UserProfileEntity> userOptional = userRepositoryPort.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        UserProfileEntity user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepositoryPort.save(user);
        return token;
    }

    public boolean validateResetToken(String token) {
        Optional<UserProfileEntity> userOptional = userRepositoryPort.findByResetToken(token);
        return userOptional.isPresent();
    }

    public void resetPassword(String token, String newPassword) {
        if (!validateResetToken(token)) {
            throw new InvalidTokenException("Invalid reset token.");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new InvalidPasswordException("Password does not meet security requirements.");
        }

        Optional<UserProfileEntity> userOptional = userRepositoryPort.findByResetToken(token);
        if (!userOptional.isPresent()) {
            throw new InvalidTokenException("Token not associated with any user.");
        }
        UserProfileEntity user = userOptional.get();
        user.setPassword(newPassword);
        user.setResetToken(null);
        userRepositoryPort.save(user);
    }

    // Additional methods related to password reset can be added here
}

// TODO: Define the findByResetToken method in the UserProfileRepositoryPort interface
// package com.example.profilemanagement.domain.ports;
// import java.util.Optional;
// import com.example.profilemanagement.domain.entities.UserProfileEntity;
// public interface UserProfileRepositoryPort {
//     Optional<UserProfileEntity> findByEmail(String email);
//     Optional<UserProfileEntity> findById(Long id);
//     UserProfileEntity save(UserProfileEntity userProfileEntity);
//     void deleteById(Long id);
//     List<UserProfileEntity> findAll();
//     Optional<UserProfileEntity> findByResetToken(String resetToken);
// }

// TODO: Ensure the UserProfileEntity class has setResetToken and setPassword methods
// package com.example.profilemanagement.domain.entities;
// import lombok.Data;
//
// @Data
// public class UserProfileEntity {
//     // Other fields...
//     private String resetToken;
//     private String password;
//     // Other methods...
//     public void setResetToken(String resetToken) {
//         this.resetToken = resetToken;
//     }
//     public void setPassword(String password) {
//         this.password = password;
//     }
// }
