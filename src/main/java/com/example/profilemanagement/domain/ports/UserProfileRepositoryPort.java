package com.example.profilemanagement.domain.ports;

import com.example.profilemanagement.domain.entities.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserProfileRepositoryPort extends JpaRepository<UserProfileEntity, Long> {

    Optional<UserProfileEntity> findByEmail(String email);

    UserProfileEntity save(UserProfileEntity userProfileEntity);

    Optional<UserProfileEntity> findById(Long id);

    void deleteById(Long id);

    List<UserProfileEntity> findAll();
}