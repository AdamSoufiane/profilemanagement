package com.example.profilemanagement.domain.services;

import com.example.profilemanagement.domain.ports.UserProfileRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {

    private final UserProfileRepositoryPort userProfileRepository;

    @Autowired
    public UserVerificationService(UserProfileRepositoryPort userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public boolean existsById(Long userId) {
        return userProfileRepository.findById(userId).isPresent();
    }
}
