package com.yoanesber.spring.async_executor.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yoanesber.spring.async_executor.entity.Order;
import com.yoanesber.spring.async_executor.entity.OrderDetail;
import com.yoanesber.spring.async_executor.service.EmailService;
import com.yoanesber.spring.async_executor.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final EmailService emailService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    private Order getOrderByID (String orderId) {
        Order order = new Order();
        // Unique Order ID
        order.setOrderId(orderId);

        // Order Date (current timestamp)
        order.setOrderDate(LocalDateTime.now());

        // Order Status
        order.setOrderStatus("PENDING");

        // Order Total (e.g., total price of items)
        order.setOrderTotal(new BigDecimal("199.99"));

        // Currency
        order.setCurrency("IDR");

        // Customer Information
        order.setCustomerId("CUST1001");
        order.setCustomerName("Agus Yulianto");
        order.setCustomerEmail("agus_yulianto@example.com");
        order.setCustomerPhone("+62-811-222-3333");

        // Payment Information
        order.setPaymentMethod("CREDIT_CARD");
        order.setPaymentStatus("PAID");

        // Shipping Information
        order.setShippingAddress("Jl. Melati V No. 8, Solo, Jawa Tengah, Indonesia");
        order.setShippingMethod("STANDARD");
        order.setDeliveryDate(LocalDateTime.now().plusDays(5)); // Expected delivery in 5 days

        // Tax and Discount
        order.setTaxAmount(new BigDecimal("9.99"));
        order.setDiscountCode("DISCOUNT10");
        order.setDiscountAmount(new BigDecimal("10.00"));

        // Metadata
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setProcessedBy("AdminUser");

        // Order Details (list of items in the order)
        // For simplicity, we will add a single item
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductId("PROD1001");
        orderDetail.setProductName("Product A");
        orderDetail.setProductPrice(new BigDecimal("99.99"));
        orderDetail.setQuantity(2);
        orderDetail.setSubtotal(orderDetail.getProductPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
        orderDetail.setDiscountAmount(new BigDecimal("10.00"));
        orderDetail.setTotalPrice(orderDetail.getSubtotal().subtract(orderDetail.getDiscountAmount()));
        orderDetail.setProductImageUrl("https://example.com/product-a.jpg");
        orderDetail.setNotes("No special notes");

        // Set the order details
        order.setOrderDetails(List.of(orderDetail));

        return order;
    }

    @Override
    public Boolean updateStock(String productId, int quantity) {
        logger.info("Updating stock for product ID: " + productId);

        // Simulate updating stock for a product
        try {
            // Get current stock for the product
            int currentStock = 100; // Assume initial stock is 100

            // Check if there is enough stock to fulfill the order
            if (currentStock < quantity) {
                throw new RuntimeException("Insufficient stock for product ID: " + productId);
            }

            // Update stock (decrease by the quantity ordered)
            currentStock -= quantity;

            // Simulate processing time
            Thread.sleep(2000);

            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException("Error processing update stock: " + e.getMessage());
        } finally {
            logger.info("Finished updating stock for product ID: " + productId);
        }
    }

    @Override
    public void updateStockByOrderID(String orderId) {
        logger.info("Updating stock for order ID: " + orderId);

        // Simulate updating stock
        try {
            // Get order details by order ID
            Order order = this.getOrderByID(orderId);

            // Update stock for each item in the order
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                updateStock(orderDetail.getProductId(), orderDetail.getQuantity());
            }

            // Simulate processing time
            Thread.sleep(5000);
            
        } catch (InterruptedException e) {
            throw new RuntimeException("Error processing update stock by order ID: " + e.getMessage());
        } finally {
            logger.info("Finished updating stock for order ID: " + orderId);
        }
    }

    @Override
    public void orderConfirmation(String orderId) {
        logger.info("Sending order confirmation email for order ID: " + orderId);

        // Simulate sending order confirmation email
        try {
            // Get order details by order ID
            Order order = this.getOrderByID(orderId);

            // Send order confirmation email to customer
            String email = order.getCustomerEmail();
            String subject = "Order Confirmation: " + order.getOrderId();
            String message = "Dear " + order.getCustomerName() + ",\n\n"
                    + "Thank you for your order. Your order has been confirmed and is being processed.\n\n"
                    + "Order ID: " + order.getOrderId() + "\n"
                    + "Order Date: " + order.getOrderDate() + "\n"
                    + "Total Amount: " + order.getOrderTotal() + " " + order.getCurrency() + "\n\n"
                    + "We will notify you once your order has been shipped.\n\n"
                    + "Thank you for shopping with us!\n\n"
                    + "Best regards,\n"
                    + "The Store Team";

            // Send email
            emailService.sendEmail(email, message, subject);

            // Simulate sending email
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error sending order confirmation email: " + e.getMessage());
        } finally {
            logger.info("Finished sending order confirmation email for order ID: " + orderId);
        }
    }
}
