package com.emart.repository;

import com.emart.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    @Query("{'customerId': ?0}")
    List<Invoice> findByCustomerId(String customerId);
    
    @Query("{'status': ?0}")
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    
    @Query("{'orderId': ?0}")
    Optional<Invoice> findByOrderId(String orderId);
    
    @Query("{'customerId': ?0, 'status': ?1}")
    List<Invoice> findByCustomerIdAndStatus(String customerId, Invoice.InvoiceStatus status);
    
    @Query("{'dueDate': {$lt: ?0}, 'status': {$ne: 'PAID'}}")
    List<Invoice> findOverdueInvoices(LocalDate currentDate);
    
    @Query("{'dueDate': {$gte: ?0, $lte: ?1}}")
    List<Invoice> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Invoice> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'signedBy': ?0}")
    List<Invoice> findBySignedBy(String signedBy);
    
    @Query("{'status': 'SENT'}")
    List<Invoice> findSentInvoices();
    
    @Query("{'status': 'PAID'}")
    List<Invoice> findPaidInvoices();
} 