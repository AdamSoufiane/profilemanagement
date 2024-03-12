package com.example.profilemanagement.adapters.secondary;

import com.example.profilemanagement.infrastructure.exceptions.EmailServiceClientException;
import lombok.extern.slf4j.Slf4j;

interface EmailService {
    void sendEmail(String toAddress, String subject, String body) throws EmailServiceClientException;
    void sendPasswordResetLink(String toAddress, String token) throws EmailServiceClientException;
}

@Slf4j
public class EmailServiceClient implements EmailService {

    @Override
    public void sendEmail(String toAddress, String subject, String body) throws EmailServiceClientException {
        if (toAddress == null || toAddress.isEmpty()) {
            throw new IllegalArgumentException("To address cannot be null or empty");
        }
        if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("Body cannot be null or empty");
        }

        try {
            // Integrate with the actual email service API here
            log.info("Email sent to {} with subject: {}", toAddress, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}", toAddress, e);
            throw new EmailServiceClientException("Failed to send email", e);
        }
    }

    @Override
    public void sendPasswordResetLink(String toAddress, String token) throws EmailServiceClientException {
        if (toAddress == null || toAddress.isEmpty()) {
            throw new IllegalArgumentException("To address cannot be null or empty");
        }
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        String resetLink = "http://example.com/reset-password?token=" + token;
        String subject = "Password Reset";
        String body = "Please use the following link to reset your password: " + resetLink;
        sendEmail(toAddress, subject, body);
    }

}