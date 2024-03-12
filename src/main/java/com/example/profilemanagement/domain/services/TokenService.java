package com.example.profilemanagement.domain.services;

import java.util.UUID;

public class TokenService {

    public String generateResetToken(String email) {
        return UUID.randomUUID().toString();
    }
}
