package com.example.profilemanagement.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"name", "email"})
@ToString(exclude = {"password"})
public class UserProfileEntity implements UserProfile {

    private Long id;

    @NotNull
    @Length(max = 100)
    private String name;

    @NotNull
    @Email
    @Length(max = 100)
    private String email;

    @NotNull
    @Length(min = 8)
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}

interface UserProfile {
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
