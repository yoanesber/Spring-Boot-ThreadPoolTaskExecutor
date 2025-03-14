package com.yoanesber.spring.async_executor.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {
    String orderId; // Unique Order ID (e.g., "ORD123456789")
    
    private LocalDateTime orderDate;

    private String orderStatus;  // PENDING, SHIPPED, DELIVERED, etc.

    private BigDecimal orderTotal;  // Total amount of the order

    private String currency;  // USD, EUR, etc.

    private String customerId;
    
    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String paymentMethod;  // CREDIT_CARD, PAYPAL, etc.

    private String paymentStatus;  // PAID, FAILED, PENDING

    private String shippingAddress;

    private String shippingMethod;  // STANDARD, EXPRESS

    private LocalDateTime deliveryDate;

    private BigDecimal taxAmount;
    
    private String discountCode;

    private BigDecimal discountAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String processedBy;  // Admin or system user processing the order

    private List<OrderDetail> orderDetails = new ArrayList<>();
}
