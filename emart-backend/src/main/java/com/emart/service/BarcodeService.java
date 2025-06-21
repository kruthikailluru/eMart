package com.emart.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class BarcodeService {
    
    private final Random random = new Random();
    
    public String generateBarcode(String productName) {
        // Generate a unique barcode based on product name and timestamp
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Last 6 digits
        String productCode = productName.replaceAll("\\s+", "").toUpperCase().substring(0, Math.min(3, productName.length()));
        String randomNum = String.format("%03d", random.nextInt(1000));
        
        return productCode + timestamp + randomNum;
    }
    
    public BitMatrix generateBarcodeMatrix(String barcodeText) throws WriterException {
        Code128Writer writer = new Code128Writer();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        
        return writer.encode(barcodeText, BarcodeFormat.CODE_128, 300, 100, hints);
    }
    
    public String generateBarcodeImage(String barcodeText) {
        try {
            BitMatrix bitMatrix = generateBarcodeMatrix(barcodeText);
            // Convert BitMatrix to base64 image string
            // This is a simplified version - in production you'd convert to actual image
            return "barcode_" + barcodeText + "_" + System.currentTimeMillis();
        } catch (WriterException e) {
            log.error("Error generating barcode: {}", e.getMessage());
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }
    
    public boolean validateBarcode(String barcode) {
        // Basic validation - check if barcode follows expected format
        return barcode != null && barcode.length() >= 6 && barcode.matches("^[A-Z0-9]+$");
    }
} 