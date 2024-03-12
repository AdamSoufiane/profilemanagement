package com.example.profilemanagement.domain.entities;

import java.time.LocalDateTime;

public interface UserProfile {
    Long getId();
    void setId(Long id);
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String email);
    String getPassword();
    void setPassword(String password);
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);
    Long getVersion();
    void setVersion(Long version);
}