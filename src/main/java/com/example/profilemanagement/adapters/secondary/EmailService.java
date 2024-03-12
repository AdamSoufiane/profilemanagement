package com.example.profilemanagement.adapters.secondary;

import com.example.profilemanagement.infrastructure.exceptions.EmailServiceClientException;

public interface EmailService {

    void sendEmail(String toAddress, String subject, String body) throws EmailServiceClientException;

    void sendPasswordResetLink(String toAddress, String token) throws EmailServiceClientException;

}