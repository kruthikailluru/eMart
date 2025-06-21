package com.emart.controller;

import com.emart.model.Order;
import com.emart.service.OrderService;
import com.emart.service.ProductService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    private final ProductService productService;
    private final JwtService jwtService;
    private final EmailService emailService;
    
    // Customer endpoints
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody Order order) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtService.extractUsername(token);
            
            Order createdOrder = orderService.createOrder(order, customerId);
            
            // Send order confirmation email
            emailService.sendOrderConfirmation(
                createdOrder.getCustomerEmail(),
                createdOrder.getCustomerName(),
                createdOrder.getOrderNumber(),
                createdOrder.getTotal().toString()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order created successfully");
            response.put("order", createdOrder);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomerOrders(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtService.extractUsername(token);
            
            List<Order> orders = orderService.getOrdersByCustomer(customerId);
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Failed to get customer orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String orderId) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtService.extractUsername(token);
            
            orderService.cancelOrder(orderId, customerId);
            
            return ResponseEntity.ok(Map.of("message", "Order cancelled successfully"));
        } catch (Exception e) {
            log.error("Order cancellation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Admin endpoints
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrders() {
        try {
            List<Order> pendingOrders = orderService.getPendingOrders();
            return ResponseEntity.ok(pendingOrders);
        } catch (Exception e) {
            log.error("Failed to get pending orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/pending-payments")
    public ResponseEntity<?> getPendingPayments() {
        try {
            List<Order> pendingPayments = orderService.getPendingPayments();
            return ResponseEntity.ok(pendingPayments);
        } catch (Exception e) {
            log.error("Failed to get pending payments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable String orderId,
                                             @RequestBody Map<String, String> statusRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusRequest.get("status"));
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("Order status update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String orderId,
                                               @RequestBody Map<String, String> paymentStatusRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            Order.PaymentStatus paymentStatus = Order.PaymentStatus.valueOf(paymentStatusRequest.get("paymentStatus"));
            Order updatedOrder = orderService.updatePaymentStatus(orderId, paymentStatus);
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("Payment status update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // General endpoints
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Failed to get all orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Failed to get order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            Order order = orderService.getOrderByOrderNumber(orderNumber);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Failed to get order by number: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Failed to get orders by status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<?> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        try {
            Order.PaymentStatus status = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
            List<Order> orders = orderService.getOrdersByPaymentStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Failed to get orders by payment status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getOrdersByDateRange(@RequestParam String startDate,
                                                @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<Order> orders = orderService.getOrdersByDateRange(start, end);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Failed to get orders by date range: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/customer/{customerId}/date-range")
    public ResponseEntity<?> getCustomerOrdersByDateRange(@PathVariable String customerId,
                                                        @RequestParam String startDate,
                                                        @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<Order> orders = orderService.getCustomerOrdersByDateRange(customerId, start, end);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Failed to get customer orders by date range: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/total")
    public ResponseEntity<?> getTotalRevenue() {
        try {
            BigDecimal totalRevenue = orderService.getTotalRevenue();
            return ResponseEntity.ok(Map.of("totalRevenue", totalRevenue));
        } catch (Exception e) {
            log.error("Failed to get total revenue: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/date-range")
    public ResponseEntity<?> getRevenueByDateRange(@RequestParam String startDate,
                                                 @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            BigDecimal revenue = orderService.getTotalRevenueByDateRange(start, end);
            return ResponseEntity.ok(Map.of("revenue", revenue));
        } catch (Exception e) {
            log.error("Failed to get revenue by date range: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/check-stock/{productId}")
    public ResponseEntity<?> checkStockAvailability(@PathVariable String productId,
                                                  @RequestParam int quantity) {
        try {
            boolean available = productService.checkStockAvailability(productId, quantity);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            log.error("Stock availability check failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 