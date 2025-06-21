package com.emart.controller;

import com.emart.model.Payment;
import com.emart.service.PaymentService;
import com.emart.service.JwtService;
import com.emart.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final JwtService jwtService;
    private final EmailService emailService;
    
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody Map<String, Object> paymentRequest) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtService.extractUsername(token);
            
            String orderId = (String) paymentRequest.get("orderId");
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf((String) paymentRequest.get("paymentMethod"));
            BigDecimal amount = new BigDecimal(paymentRequest.get("amount").toString());
            String gatewayResponse = (String) paymentRequest.get("gatewayResponse");
            
            Payment payment = paymentService.processPayment(orderId, paymentMethod, amount, gatewayResponse);
            
            // Send payment confirmation email if successful
            if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                emailService.sendPaymentConfirmation(
                    payment.getCustomerEmail(),
                    payment.getCustomerName(),
                    payment.getOrderNumber(),
                    payment.getAmount().toString()
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment processed successfully");
            response.put("payment", payment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomerPayments(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtService.extractUsername(token);
            
            List<Payment> payments = paymentService.getPaymentsByCustomer(customerId);
            
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get customer payments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentsByOrder(@PathVariable String orderId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByOrder(orderId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get payments by order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<?> refundPayment(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable String paymentId,
                                         @RequestBody Map<String, Object> refundRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            BigDecimal refundAmount = new BigDecimal(refundRequest.get("refundAmount").toString());
            String reason = (String) refundRequest.get("reason");
            
            Payment refundPayment = paymentService.refundPayment(paymentId, refundAmount, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Refund processed successfully");
            response.put("refund", refundPayment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Refund processing failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String paymentId,
                                               @RequestBody Map<String, String> statusRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            Payment.PaymentStatus status = Payment.PaymentStatus.valueOf(statusRequest.get("status"));
            Payment updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
            
            return ResponseEntity.ok(updatedPayment);
        } catch (Exception e) {
            log.error("Payment status update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // General endpoints
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get all payments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Failed to get payment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<?> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            Payment payment = paymentService.getPaymentByTransactionId(transactionId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Failed to get payment by transaction ID: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPaymentsByStatus(@PathVariable String status) {
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentService.getPaymentsByStatus(paymentStatus);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get payments by status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/method/{method}")
    public ResponseEntity<?> getPaymentsByMethod(@PathVariable String method) {
        try {
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(method.toUpperCase());
            List<Payment> payments = paymentService.getPaymentsByMethod(paymentMethod);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get payments by method: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getPaymentsByDateRange(@RequestParam String startDate,
                                                  @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<Payment> payments = paymentService.getPaymentsByDateRange(start, end);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to get payments by date range: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/total")
    public ResponseEntity<?> getTotalPayments() {
        try {
            BigDecimal totalPayments = paymentService.getTotalPayments();
            return ResponseEntity.ok(Map.of("totalPayments", totalPayments));
        } catch (Exception e) {
            log.error("Failed to get total payments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/refunds")
    public ResponseEntity<?> getTotalRefunds() {
        try {
            BigDecimal totalRefunds = paymentService.getTotalRefunds();
            return ResponseEntity.ok(Map.of("totalRefunds", totalRefunds));
        } catch (Exception e) {
            log.error("Failed to get total refunds: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/date-range")
    public ResponseEntity<?> getPaymentsByDateRange(@RequestParam String startDate,
                                                  @RequestParam String endDate,
                                                  @RequestParam(defaultValue = "false") boolean includeRefunds) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            BigDecimal revenue = paymentService.getTotalPaymentsByDateRange(start, end);
            
            Map<String, Object> response = new HashMap<>();
            response.put("revenue", revenue);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            if (includeRefunds) {
                BigDecimal refunds = paymentService.getTotalRefunds();
                response.put("refunds", refunds);
                response.put("netRevenue", revenue.subtract(refunds));
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get revenue by date range: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/methods/summary")
    public ResponseEntity<?> getPaymentMethodsSummary() {
        try {
            Map<String, Object> summary = new HashMap<>();
            
            for (Payment.PaymentMethod method : Payment.PaymentMethod.values()) {
                List<Payment> payments = paymentService.getPaymentsByMethod(method);
                BigDecimal total = payments.stream()
                        .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                summary.put(method.toString(), Map.of(
                    "count", payments.size(),
                    "total", total
                ));
            }
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Failed to get payment methods summary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 