package com.emart.repository;

import com.emart.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    boolean existsByOrderNumber(String orderNumber);
    
    @Query("{'customerId': ?0}")
    List<Order> findByCustomerId(String customerId);
    
    @Query("{'status': ?0}")
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("{'paymentStatus': ?0}")
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    @Query("{'customerId': ?0, 'status': ?1}")
    List<Order> findByCustomerIdAndStatus(String customerId, Order.OrderStatus status);
    
    @Query("{'orderDate': {$gte: ?0, $lte: ?1}}")
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'customerId': ?0, 'orderDate': {$gte: ?1, $lte: ?2}}")
    List<Order> findByCustomerIdAndOrderDateBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'status': 'PENDING'}")
    List<Order> findPendingOrders();
    
    @Query("{'paymentStatus': 'PENDING'}")
    List<Order> findPendingPayments();
} 