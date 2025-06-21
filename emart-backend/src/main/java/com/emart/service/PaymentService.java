package com.emart.service;

import com.emart.model.Payment;
import com.emart.model.Order;
import com.emart.model.Invoice;
import com.emart.repository.PaymentRepository;
import com.emart.repository.OrderRepository;
import com.emart.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final OrderService orderService;
    private final InvoiceService invoiceService;
    
    @Transactional
    public Payment processPayment(String orderId, Payment.PaymentMethod paymentMethod, 
                                BigDecimal amount, String gatewayResponse) {
        // Validate order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Create payment record
        Payment payment = new Payment();
        payment.setTransactionId(generateTransactionId());
        payment.setOrderId(orderId);
        payment.setOrderNumber(order.getOrderNumber());
        payment.setCustomerId(order.getCustomerId());
        payment.setCustomerName(order.getCustomerName());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setGatewayResponse(gatewayResponse);
        payment.setGatewayTransactionId(generateGatewayTransactionId());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // Process payment (simulate payment gateway)
        boolean paymentSuccess = processPaymentWithGateway(payment);
        
        if (paymentSuccess) {
            // Update payment status
            savedPayment.setStatus(Payment.PaymentStatus.COMPLETED);
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            
            // Update order payment status
            orderService.updatePaymentStatus(orderId, Order.PaymentStatus.PAID);
            
            // Update invoice status if exists
            try {
                Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
                invoiceService.updateInvoiceStatus(invoice.getId(), Invoice.InvoiceStatus.PAID);
            } catch (Exception e) {
                log.warn("No invoice found for order: {}", orderId);
            }
            
            log.info("Payment processed successfully for order: {}", orderId);
        } else {
            // Payment failed
            savedPayment.setStatus(Payment.PaymentStatus.FAILED);
            savedPayment.setFailureReason("Payment gateway processing failed");
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            
            log.error("Payment failed for order: {}", orderId);
        }
        
        return savedPayment;
    }
    
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    public List<Payment> getPaymentsByOrder(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(List::of)
                .orElse(List.of());
    }
    
    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
    
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> getPaymentsByMethod(Payment.PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }
    
    public Payment updatePaymentStatus(String paymentId, Payment.PaymentStatus status) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    public Payment refundPayment(String paymentId, BigDecimal refundAmount, String refundReason) {
        Payment originalPayment = getPaymentById(paymentId);
        
        if (originalPayment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Cannot refund payment that is not completed");
        }
        
        if (refundAmount.compareTo(originalPayment.getAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed original payment amount");
        }
        
        // Create refund payment record
        Payment refundPayment = new Payment();
        refundPayment.setTransactionId(generateTransactionId());
        refundPayment.setOrderId(originalPayment.getOrderId());
        refundPayment.setOrderNumber(originalPayment.getOrderNumber());
        refundPayment.setCustomerId(originalPayment.getCustomerId());
        refundPayment.setCustomerName(originalPayment.getCustomerName());
        refundPayment.setCustomerEmail(originalPayment.getCustomerEmail());
        refundPayment.setAmount(refundAmount.negate()); // Negative amount for refund
        refundPayment.setPaymentMethod(originalPayment.getPaymentMethod());
        refundPayment.setGatewayResponse("Refund processed");
        refundPayment.setGatewayTransactionId(generateGatewayTransactionId());
        refundPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        refundPayment.setPaymentDate(LocalDateTime.now());
        refundPayment.setRefundReason(refundReason);
        refundPayment.setRefundDate(LocalDateTime.now());
        refundPayment.setCreatedAt(LocalDateTime.now());
        refundPayment.setUpdatedAt(LocalDateTime.now());
        
        // Update original payment status
        originalPayment.setStatus(Payment.PaymentStatus.REFUNDED);
        originalPayment.setRefundReason(refundReason);
        originalPayment.setRefundDate(LocalDateTime.now());
        originalPayment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(originalPayment);
        
        return paymentRepository.save(refundPayment);
    }
    
    private boolean processPaymentWithGateway(Payment payment) {
        // Simulate payment gateway processing
        // In a real application, this would integrate with actual payment gateways
        // like Stripe, PayPal, etc.
        
        try {
            // Simulate processing delay
            Thread.sleep(1000);
            
            // Simulate success/failure (90% success rate)
            return Math.random() > 0.1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private String generateTransactionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    private String generateGatewayTransactionId() {
        return "GTW-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    public BigDecimal getTotalPayments() {
        return paymentRepository.findCompletedPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalRefunds() {
        return paymentRepository.findRefundedPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();
    }
    
    public BigDecimal getTotalPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate).stream()
                .filter(payment -> payment.getStatus() == Payment.PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 