package com.emart.repository;

import com.emart.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    boolean existsByTransactionId(String transactionId);
    
    @Query("{'customerId': ?0}")
    List<Payment> findByCustomerId(String customerId);
    
    @Query("{'status': ?0}")
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    @Query("{'paymentMethod': ?0}")
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    @Query("{'invoiceId': ?0}")
    Optional<Payment> findByInvoiceId(String invoiceId);
    
    @Query("{'orderId': ?0}")
    Optional<Payment> findByOrderId(String orderId);
    
    @Query("{'customerId': ?0, 'status': ?1}")
    List<Payment> findByCustomerIdAndStatus(String customerId, Payment.PaymentStatus status);
    
    @Query("{'paymentDate': {$gte: ?0, $lte: ?1}}")
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'amount': {$gte: ?0}}")
    List<Payment> findByAmountGreaterThanEqual(BigDecimal amount);
    
    @Query("{'status': 'COMPLETED'}")
    List<Payment> findCompletedPayments();
    
    @Query("{'status': 'FAILED'}")
    List<Payment> findFailedPayments();
    
    @Query("{'status': 'REFUNDED'}")
    List<Payment> findRefundedPayments();
    
    @Query("{'gatewayTransactionId': ?0}")
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
} 