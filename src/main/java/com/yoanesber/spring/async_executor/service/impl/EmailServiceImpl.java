package com.yoanesber.spring.async_executor.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yoanesber.spring.async_executor.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendEmail(String email, String message, String subject) {
        logger.info("Sending email to " + email);

        // Simulate sending email
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error occurred while sending email: " + e.getMessage());
        } finally {
            logger.info("Email sent to " + email);
        }
    }
}
