package com.yoanesber.spring.async_executor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoanesber.spring.async_executor.async.OrderAsync;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderAsync orderAsync;

    public OrderController(OrderAsync orderAsync) {
        this.orderAsync = orderAsync;
    }

    @PostMapping("/process/{orderId}")
    public ResponseEntity<Object> processOrders(@PathVariable String orderId) {
        // Validate request
        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.badRequest().body("Order ID is required");
        }

        try {
            // Update stock
            orderAsync.updateStock(orderId);

            // Send order confirmation email
            orderAsync.orderConfirmation(orderId);

            // Return response
            return ResponseEntity.ok().body("Order processed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process order: " + e.getMessage());
        }
    }   
}
