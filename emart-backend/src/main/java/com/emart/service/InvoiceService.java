package com.emart.service;

import com.emart.model.Invoice;
import com.emart.model.Order;
import com.emart.model.User;
import com.emart.repository.InvoiceRepository;
import com.emart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final BarcodeService barcodeService;
    
    public Invoice generateInvoiceForOrder(Order order) {
        Invoice invoice = new Invoice();
        
        // Generate invoice number
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setOrderId(order.getId());
        invoice.setOrderNumber(order.getOrderNumber());
        
        // Set customer information
        invoice.setCustomerId(order.getCustomerId());
        invoice.setCustomerName(order.getCustomerName());
        invoice.setCustomerEmail(order.getCustomerEmail());
        invoice.setCustomerPhone(order.getCustomerPhone());
        // Note: Order doesn't have customerAddress, so we'll leave it null for now
        
        // Convert OrderItems to InvoiceItems
        List<Invoice.InvoiceItem> invoiceItems = order.getItems().stream()
                .map(this::convertToInvoiceItem)
                .collect(Collectors.toList());
        invoice.setItems(invoiceItems);
        
        // Set order details
        invoice.setSubtotal(order.getSubtotal());
        invoice.setTax(order.getTax());
        invoice.setTotal(order.getTotal());
        
        // Set invoice details
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setDueDate(LocalDate.now().plusDays(30)); // 30 days due
        
        // Set timestamps
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        
        return invoiceRepository.save(invoice);
    }
    
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
    
    public Invoice getInvoiceByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
    
    public Invoice getInvoiceByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
    
    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }
    
    public List<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }
    
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
    
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(LocalDate.now());
    }
    
    public Invoice updateInvoiceStatus(String invoiceId, Invoice.InvoiceStatus status) {
        Invoice invoice = getInvoiceById(invoiceId);
        invoice.setStatus(status);
        invoice.setUpdatedAt(LocalDateTime.now());
        
        return invoiceRepository.save(invoice);
    }
    
    public Invoice addDigitalSignature(String invoiceId, String adminId, String signatureData) {
        Invoice invoice = getInvoiceById(invoiceId);
        
        // Validate admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (admin.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }
        
        invoice.setDigitalSignature(signatureData);
        invoice.setSignedBy(adminId);
        invoice.setSignedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        
        return invoiceRepository.save(invoice);
    }
    
    public String generateInvoicePDF(String invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        // This would generate PDF and return file path
        // For now, return a placeholder
        return "invoices/" + invoice.getInvoiceNumber() + ".pdf";
    }
    
    private Invoice.InvoiceItem convertToInvoiceItem(Order.OrderItem orderItem) {
        Invoice.InvoiceItem invoiceItem = new Invoice.InvoiceItem();
        invoiceItem.setProductId(orderItem.getProductId());
        invoiceItem.setProductName(orderItem.getProductName());
        invoiceItem.setBarcode(orderItem.getBarcode());
        invoiceItem.setQuantity(orderItem.getQuantity());
        invoiceItem.setUnitPrice(orderItem.getUnitPrice());
        invoiceItem.setTotalPrice(orderItem.getTotalPrice());
        return invoiceItem;
    }
    
    private String generateInvoiceNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "INV-" + timestamp + random;
    }
    
    public BigDecimal getTotalInvoiceAmount() {
        return invoiceRepository.findAll().stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getPaidInvoiceAmount() {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.PAID)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getPendingInvoiceAmount() {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.DRAFT || 
                                 invoice.getStatus() == Invoice.InvoiceStatus.SENT)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 