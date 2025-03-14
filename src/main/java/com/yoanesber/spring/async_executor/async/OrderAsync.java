package com.yoanesber.spring.async_executor.async;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.yoanesber.spring.async_executor.service.OrderService;

@Component
public class OrderAsync {

    private final OrderService orderService;

    // Maximum number of attempts
    private static final int maxAttemptsRetry = 3;

    // Delay between attempts
    private static final long initialIntervalRetry = 2000; // 2 seconds

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderAsync(OrderService orderService) {
        this.orderService = orderService;
    }

    // Asynchronous method to update stock
    @Async
    @Retryable( // Retry processing order if RuntimeException occurs
        retryFor = {RuntimeException.class},
        maxAttempts = maxAttemptsRetry,
        backoff = @Backoff(delay = initialIntervalRetry)
    )
    public void updateStock(String orderId) throws RuntimeException {
        logger.info("Started asynchronous task (updateStock) in thread: " + Thread.currentThread().getName() + " at: " + LocalDateTime.now());

        try {
            orderService.updateStockByOrderID(orderId);
        } catch (Exception e) {
            logger.error("Error occurred while updating stock: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            logger.info("Finished asynchronous task (updateStock) in thread: " + Thread.currentThread().getName() + " at: " + LocalDateTime.now());
        }
    }

    // Recover method to handle the exception after maxAttemptsRetry
    // The parameters of the recover method must match the parameters of the updateStock method
    @Recover
    public void recoverUpdateStock(RuntimeException ex, String input) {
        logger.error("Failed to update stock after " + maxAttemptsRetry + " attempts. Order ID: " + input);

        // Recovery logic here (e.g., logging, retrying, or notifying an admin)
    }

    // Asynchronous method to send order confirmation email
    @Async
    @Retryable( // Retry processing order if RuntimeException occurs
        retryFor = {RuntimeException.class},
        maxAttempts = maxAttemptsRetry,
        backoff = @Backoff(delay = initialIntervalRetry)
    )
    public void orderConfirmation(String orderId) throws RuntimeException {
        logger.info("Started asynchronous task (orderConfirmation) in thread: " + Thread.currentThread().getName() + " at: " + LocalDateTime.now());

        try {
            orderService.orderConfirmation(orderId);
        } catch (Exception e) {
            logger.error("Error occurred while confirming order: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            logger.info("Finished asynchronous task (orderConfirmation) in thread: " + Thread.currentThread().getName() + " at: " + LocalDateTime.now());
        }
    }

    // Recover method to handle the exception after maxAttemptsRetry
    // The parameters of the recover method must match the parameters of the updateStock method
    @Recover
    public void recoverOrderConfirmation(RuntimeException ex, String input) {
        logger.error("Failed to confirm order after " + maxAttemptsRetry + " attempts. Order ID: " + input);

        // Recovery logic here (e.g., logging, retrying, or notifying an admin)
    }
}
