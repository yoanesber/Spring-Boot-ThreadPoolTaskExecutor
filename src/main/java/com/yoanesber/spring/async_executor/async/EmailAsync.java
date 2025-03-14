package com.yoanesber.spring.async_executor.async;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.yoanesber.spring.async_executor.service.EmailService;

@Component
public class EmailAsync {

    private final EmailService emailService;

    // Maximum number of attempts
    private static final int maxAttemptsRetry = 3;

    // Delay between attempts
    private static final long initialIntervalRetry = 2000; // 2 seconds

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public EmailAsync(EmailService emailService) {
        this.emailService = emailService;
    }

    // Asynchronous method to send email
    @Async
    @Retryable( // Retry sending email if RuntimeException occurs
        retryFor = {RuntimeException.class},
        maxAttempts = maxAttemptsRetry,
        backoff = @Backoff(delay = initialIntervalRetry)
    )
    public void sendEmail(String email, String message, String subject) throws RuntimeException {
        logger.info("Started asynchronous task (sendEmail) in thread: " + Thread.currentThread().getName() + " at: " + LocalDateTime.now());

        try {
            emailService.sendEmail(email, message, subject);
        } catch (Exception e) {
            logger.error("Error occurred while sending email: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            logger.info("Finished asynchronous task (sendEmail) in thread: " + Thread.currentThread().getName() + " at: " + LocalDateTime.now());
        }
    }

    // Recover method to handle the exception after maxAttemptsRetry
    // The parameters of the recover method must match the parameters of the sendEmail method
    @Recover
    public void recover(RuntimeException e, String email, String message, String subject) {
        logger.error("Failed to send email after " + maxAttemptsRetry + " attempts. Email: " + email);

        // Continue with other tasks ...
        // For example, log the error, send a notification, etc.
    }
}
