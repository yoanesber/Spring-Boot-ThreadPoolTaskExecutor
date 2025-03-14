package com.yoanesber.spring.async_executor.service;

public interface OrderService {
    // Update stock
    Boolean updateStock(String productId, int quantity);

    // Update stock by order ID
    void updateStockByOrderID(String orderId);

    // Send order confirmation email
    void orderConfirmation(String orderId);
}
