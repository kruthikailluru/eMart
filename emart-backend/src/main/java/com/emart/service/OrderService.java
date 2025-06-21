package com.emart.service;

import com.emart.model.Order;
import com.emart.model.Product;
import com.emart.model.User;
import com.emart.repository.OrderRepository;
import com.emart.repository.ProductRepository;
import com.emart.repository.UserRepository;
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
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final InvoiceService invoiceService;
    
    @Transactional
    public Order createOrder(Order order, String customerId) {
        // Validate customer exists
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if (customer.getRole() != User.UserRole.CUSTOMER) {
            throw new RuntimeException("User is not a customer");
        }
        
        // Validate and process order items
        validateOrderItems(order.getItems());
        
        // Calculate totals
        calculateOrderTotals(order);
        
        // Set order details
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(customerId);
        order.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        order.setCustomerEmail(customer.getEmail());
        order.setCustomerPhone(customer.getPhone());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Update inventory
        updateInventoryForOrder(order.getItems());
        
        // Generate invoice
        invoiceService.generateInvoiceForOrder(savedOrder);
        
        return savedOrder;
    }
    
    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    public Order updatePaymentStatus(String orderId, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setPaymentStatus(paymentStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public List<Order> getPendingOrders() {
        return orderRepository.findPendingOrders();
    }
    
    public List<Order> getPendingPayments() {
        return orderRepository.findPendingPayments();
    }
    
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }
    
    public List<Order> getCustomerOrdersByDateRange(String customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndOrderDateBetween(customerId, startDate, endDate);
    }
    
    public void cancelOrder(String orderId, String customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check if order belongs to customer
        if (!order.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Order does not belong to this customer");
        }
        
        // Only allow cancellation if order is pending
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Cannot cancel order that is not pending");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // Restore inventory
        restoreInventoryForOrder(order.getItems());
    }
    
    private void validateOrderItems(List<Order.OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }
        
        for (Order.OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            
            // Check if product is approved and available
            if (product.getStatus() != Product.ProductStatus.APPROVED) {
                throw new RuntimeException("Product is not available: " + product.getName());
            }
            
            // Check stock availability
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            // Set product details
            item.setProductName(product.getName());
            item.setBarcode(product.getBarcode());
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
    }
    
    private void calculateOrderTotals(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(Order.OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate tax (10%)
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
        BigDecimal total = subtotal.add(tax);
        
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);
    }
    
    private void updateInventoryForOrder(List<Order.OrderItem> items) {
        for (Order.OrderItem item : items) {
            productService.updateStock(item.getProductId(), -item.getQuantity());
        }
    }
    
    private void restoreInventoryForOrder(List<Order.OrderItem> items) {
        for (Order.OrderItem item : items) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }
    }
    
    private String generateOrderNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp + random;
    }
    
    public BigDecimal getTotalRevenue() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getPaymentStatus() == Order.PaymentStatus.PAID)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .filter(order -> order.getPaymentStatus() == Order.PaymentStatus.PAID)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 