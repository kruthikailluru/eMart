package com.emart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String barcode;
    
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private LocalDate bestBefore;
    
    @DBRef
    private User supplier;
    
    private String supplierName;
    private String supplierEmail;
    
    private ProductStatus status;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum ProductStatus {
        PENDING, APPROVED, REJECTED, OUT_OF_STOCK
    }
} 