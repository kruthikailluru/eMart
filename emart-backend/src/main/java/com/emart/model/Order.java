package com.emart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String orderNumber;
    
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String invoiceId;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productId;
        private String productName;
        private String barcode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
} 