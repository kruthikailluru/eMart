package com.emart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class Invoice {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String invoiceNumber;
    
    private String orderId;
    private String orderNumber;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private List<InvoiceItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private InvoiceStatus status;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private String paymentMethod;
    private String transactionId;
    private String signedBy;
    private LocalDateTime signedAt;
    private String pdfPath;
    private String digitalSignature;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
    
    public enum InvoiceStatus {
        DRAFT, SENT, PAID, OVERDUE, CANCELLED
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItem {
        private String productId;
        private String productName;
        private String barcode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
} 