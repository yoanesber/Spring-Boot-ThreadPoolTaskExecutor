package com.yoanesber.spring.async_executor.service;

public interface EmailService {
    // Send email
    void sendEmail(String email, String message, String subject);
}
