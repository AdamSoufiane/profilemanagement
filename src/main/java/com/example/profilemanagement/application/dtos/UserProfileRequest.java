package com.example.profilemanagement.application.dtos;

import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;
import com.example.profilemanagement.application.validation.ValidPassword;

@Data
public class UserProfileRequest {

    @Nullable
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email should be valid and not null")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @ValidPassword
    private String password;

    @NotNull(message = "Creation timestamp is mandatory")
    private LocalDateTime createdAt;

    @NotNull(message = "Update timestamp is mandatory")
    private LocalDateTime updatedAt;

    private Long version;

    @Override
    public String toString() {
        return "UserProfileRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}