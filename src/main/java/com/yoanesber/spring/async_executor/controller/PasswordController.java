package com.yoanesber.spring.async_executor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.spring.async_executor.dto.ForgotPasswordRequestDTO;
import com.yoanesber.spring.async_executor.async.EmailAsync;

@RestController
@RequestMapping("/api/v1/password")
public class PasswordController {

    private final EmailAsync emailAsync;

    private static final String DEFAULT_PASSWORD = "P@ssw0rd";

    public PasswordController(EmailAsync emailAsync) {
        this.emailAsync = emailAsync;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        // Validate request
        if (request == null || request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            String message = "You have requested to reset your password. A temporary password has been generated for you: " + DEFAULT_PASSWORD + ".\n" +
                "For security reasons, please log in and change your password immediately.\n" +
                "If you did not request this password reset, please ignore this email or contact our support team.";

            // Send email
            emailAsync.sendEmail(request.getEmail(), message, "Forgot Password");
            
            return ResponseEntity.ok().body("Password reset email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }
    
}
