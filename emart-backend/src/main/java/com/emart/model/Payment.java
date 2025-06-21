package com.emart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String transactionId;
    
    private String invoiceId;
    private String invoiceNumber;
    private String orderId;
    private String orderNumber;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String gatewayResponse;
    private String gatewayTransactionId;
    private String cardLastFourDigits;
    private String cardType;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String failureReason;
    private String refundReason;
    private LocalDateTime refundDate;
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CASH, DIGITAL_WALLET
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED
    }
} 