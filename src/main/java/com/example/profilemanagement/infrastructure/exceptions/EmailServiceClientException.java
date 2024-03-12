package com.example.profilemanagement.infrastructure.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailServiceClientException extends RuntimeException {

    public EmailServiceClientException(String message) {
        super(message);
        log.error(message);
    }

    public EmailServiceClientException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }

}