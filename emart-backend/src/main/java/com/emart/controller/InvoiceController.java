package com.emart.controller;

import com.emart.model.Invoice;
import com.emart.service.InvoiceService;
import com.emart.service.JwtService;
import com.emart.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    private final JwtService jwtService;
    private final EmailService emailService;
    
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomerInvoices(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String customerId = jwtService.extractUsername(token);
            
            List<Invoice> invoices = invoiceService.getInvoicesByCustomer(customerId);
            
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Failed to get customer invoices: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{invoiceId}/sign")
    public ResponseEntity<?> addDigitalSignature(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String invoiceId,
                                               @RequestBody Map<String, String> signatureRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String signatureData = signatureRequest.get("signatureData");
            
            Invoice signedInvoice = invoiceService.addDigitalSignature(invoiceId, adminId, signatureData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invoice signed successfully");
            response.put("invoice", signedInvoice);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Invoice signing failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<?> downloadInvoicePDF(@PathVariable String invoiceId) {
        try {
            String pdfPath = invoiceService.generateInvoicePDF(invoiceId);
            
            // In a real implementation, you would read the actual PDF file
            // For now, we'll return a placeholder response
            String pdfContent = "PDF content for invoice " + invoiceId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + invoiceId + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent.getBytes());
        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{invoiceId}/status")
    public ResponseEntity<?> updateInvoiceStatus(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable String invoiceId,
                                               @RequestBody Map<String, String> statusRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            Invoice.InvoiceStatus status = Invoice.InvoiceStatus.valueOf(statusRequest.get("status"));
            Invoice updatedInvoice = invoiceService.updateInvoiceStatus(invoiceId, status);
            
            return ResponseEntity.ok(updatedInvoice);
        } catch (Exception e) {
            log.error("Invoice status update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{invoiceId}/send")
    public ResponseEntity<?> sendInvoiceNotification(@RequestHeader("Authorization") String authHeader,
                                                   @PathVariable String invoiceId) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            
            // Send invoice notification email
            emailService.sendInvoiceNotification(
                invoice.getCustomerEmail(),
                invoice.getCustomerName(),
                invoice.getInvoiceNumber(),
                invoice.getTotal().toString()
            );
            
            return ResponseEntity.ok(Map.of("message", "Invoice notification sent successfully"));
        } catch (Exception e) {
            log.error("Invoice notification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // General endpoints
    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Failed to get all invoices: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            log.error("Failed to get invoice: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        try {
            Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            log.error("Failed to get invoice by number: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getInvoiceByOrderId(@PathVariable String orderId) {
        try {
            Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            log.error("Failed to get invoice by order ID: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getInvoicesByStatus(@PathVariable String status) {
        try {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            List<Invoice> invoices = invoiceService.getInvoicesByStatus(invoiceStatus);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Failed to get invoices by status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueInvoices() {
        try {
            List<Invoice> overdueInvoices = invoiceService.getOverdueInvoices();
            return ResponseEntity.ok(overdueInvoices);
        } catch (Exception e) {
            log.error("Failed to get overdue invoices: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/total")
    public ResponseEntity<?> getTotalInvoiceAmount() {
        try {
            BigDecimal totalAmount = invoiceService.getTotalInvoiceAmount();
            return ResponseEntity.ok(Map.of("totalInvoiceAmount", totalAmount));
        } catch (Exception e) {
            log.error("Failed to get total invoice amount: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/paid")
    public ResponseEntity<?> getPaidInvoiceAmount() {
        try {
            BigDecimal paidAmount = invoiceService.getPaidInvoiceAmount();
            return ResponseEntity.ok(Map.of("paidInvoiceAmount", paidAmount));
        } catch (Exception e) {
            log.error("Failed to get paid invoice amount: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/pending")
    public ResponseEntity<?> getPendingInvoiceAmount() {
        try {
            BigDecimal pendingAmount = invoiceService.getPendingInvoiceAmount();
            return ResponseEntity.ok(Map.of("pendingInvoiceAmount", pendingAmount));
        } catch (Exception e) {
            log.error("Failed to get pending invoice amount: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/summary")
    public ResponseEntity<?> getInvoiceSummary() {
        try {
            BigDecimal totalAmount = invoiceService.getTotalInvoiceAmount();
            BigDecimal paidAmount = invoiceService.getPaidInvoiceAmount();
            BigDecimal pendingAmount = invoiceService.getPendingInvoiceAmount();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalAmount", totalAmount);
            summary.put("paidAmount", paidAmount);
            summary.put("pendingAmount", pendingAmount);
            summary.put("paidPercentage", totalAmount.compareTo(BigDecimal.ZERO) > 0 ? 
                paidAmount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                BigDecimal.ZERO);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Failed to get invoice summary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 