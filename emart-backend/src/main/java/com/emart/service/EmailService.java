package com.emart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    public void sendProductApprovalNotification(String customerEmail, String customerName, String productName) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send approval notification to: {}", customerEmail);
            return;
        }
        
        String subject = "Product Approval Notification";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your product '%s' has been approved and is now available in our inventory.\n" +
            "You will be notified when the goods arrive from the warehouse to the shop.\n\n" +
            "Best regards,\n" +
            "EMart Team",
            customerName, productName
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendProductRejectionNotification(String supplierEmail, String supplierName, String productName, String reason) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send rejection notification to: {}", supplierEmail);
            return;
        }
        
        String subject = "Product Rejection Notification";
        String message = String.format(
            "Dear %s,\n\n" +
            "Your product '%s' has been rejected for the following reason:\n" +
            "%s\n\n" +
            "Please review and resubmit if necessary.\n\n" +
            "Best regards,\n" +
            "EMart Team",
            supplierName, productName, reason
        );
        
        sendEmail(supplierEmail, subject, message);
    }
    
    public void sendOrderConfirmation(String customerEmail, String customerName, String orderNumber, String total) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send order confirmation to: {}", customerEmail);
            return;
        }
        
        String subject = "Order Confirmation - " + orderNumber;
        String message = String.format(
            "Dear %s,\n\n" +
            "Thank you for your order! Your order has been confirmed.\n\n" +
            "Order Number: %s\n" +
            "Total Amount: $%s\n\n" +
            "We will notify you when your order is ready for pickup.\n\n" +
            "Best regards,\n" +
            "EMart Team",
            customerName, orderNumber, total
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendPaymentConfirmation(String customerEmail, String customerName, String orderNumber, String amount) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send payment confirmation to: {}", customerEmail);
            return;
        }
        
        String subject = "Payment Confirmation - " + orderNumber;
        String message = String.format(
            "Dear %s,\n\n" +
            "Your payment has been processed successfully.\n\n" +
            "Order Number: %s\n" +
            "Amount Paid: $%s\n\n" +
            "Thank you for your business!\n\n" +
            "Best regards,\n" +
            "EMart Team",
            customerName, orderNumber, amount
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendInvoiceNotification(String customerEmail, String customerName, String invoiceNumber, String amount) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send invoice notification to: {}", customerEmail);
            return;
        }
        
        String subject = "Invoice Generated - " + invoiceNumber;
        String message = String.format(
            "Dear %s,\n\n" +
            "An invoice has been generated for your order.\n\n" +
            "Invoice Number: %s\n" +
            "Amount: $%s\n\n" +
            "You can download the invoice from your account dashboard.\n\n" +
            "Best regards,\n" +
            "EMart Team",
            customerName, invoiceNumber, amount
        );
        
        sendEmail(customerEmail, subject, message);
    }
    
    public void sendLowStockAlert(String adminEmail, String productName, int currentStock) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send low stock alert to: {}", adminEmail);
            return;
        }
        
        String subject = "Low Stock Alert";
        String message = String.format(
            "Dear Admin,\n\n" +
            "The following product is running low on stock:\n\n" +
            "Product: %s\n" +
            "Current Stock: %d\n\n" +
            "Please reorder soon to avoid stockouts.\n\n" +
            "Best regards,\n" +
            "EMart System",
            productName, currentStock
        );
        
        sendEmail(adminEmail, subject, message);
    }
    
    public void sendExpiryAlert(String adminEmail, String productName, String expiryDate) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send expiry alert to: {}", adminEmail);
            return;
        }
        
        String subject = "Product Expiry Alert";
        String message = String.format(
            "Dear Admin,\n\n" +
            "The following product is approaching its expiry date:\n\n" +
            "Product: %s\n" +
            "Expiry Date: %s\n\n" +
            "Please take appropriate action.\n\n" +
            "Best regards,\n" +
            "EMart System",
            productName, expiryDate
        );
        
        sendEmail(adminEmail, subject, message);
    }
    
    public void sendWelcomeEmail(String userEmail, String userName, String userRole) {
        if (!emailEnabled) {
            log.info("Email disabled. Would send welcome email to: {}", userEmail);
            return;
        }
        
        String subject = "Welcome to EMart";
        String message = String.format(
            "Dear %s,\n\n" +
            "Welcome to EMart! Your account has been successfully created.\n\n" +
            "Role: %s\n" +
            "Email: %s\n\n" +
            "You can now log in to your account and start using our services.\n\n" +
            "Best regards,\n" +
            "EMart Team",
            userName, userRole, userEmail
        );
        
        sendEmail(userEmail, subject, message);
    }
    
    private void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
    
    public void sendCustomEmail(String to, String subject, String message) {
        sendEmail(to, subject, message);
    }
} 