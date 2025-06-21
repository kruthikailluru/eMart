package com.emart.controller;

import com.emart.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/barcodes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BarcodeController {
    
    private final BarcodeService barcodeService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateBarcode(@RequestBody Map<String, String> request) {
        try {
            String productName = request.get("productName");
            
            if (productName == null || productName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product name is required"));
            }
            
            String barcode = barcodeService.generateBarcode(productName);
            String barcodeImage = barcodeService.generateBarcodeImage(barcode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("barcode", barcode);
            response.put("barcodeImage", barcodeImage);
            response.put("productName", productName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Barcode generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateBarcode(@RequestBody Map<String, String> request) {
        try {
            String barcode = request.get("barcode");
            
            if (barcode == null || barcode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Barcode is required"));
            }
            
            boolean isValid = barcodeService.validateBarcode(barcode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("barcode", barcode);
            response.put("valid", isValid);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Barcode validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/image/{barcode}")
    public ResponseEntity<?> getBarcodeImage(@PathVariable String barcode) {
        try {
            if (!barcodeService.validateBarcode(barcode)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid barcode format"));
            }
            
            String barcodeImage = barcodeService.generateBarcodeImage(barcode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("barcode", barcode);
            response.put("barcodeImage", barcodeImage);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Barcode image generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/bulk-generate")
    public ResponseEntity<?> generateBulkBarcodes(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<String> productNames = (java.util.List<String>) request.get("productNames");
            
            if (productNames == null || productNames.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product names list is required"));
            }
            
            java.util.List<Map<String, Object>> results = new java.util.ArrayList<>();
            
            for (String productName : productNames) {
                try {
                    String barcode = barcodeService.generateBarcode(productName);
                    String barcodeImage = barcodeService.generateBarcodeImage(barcode);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("productName", productName);
                    result.put("barcode", barcode);
                    result.put("barcodeImage", barcodeImage);
                    result.put("success", true);
                    
                    results.add(result);
                } catch (Exception e) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("productName", productName);
                    result.put("error", e.getMessage());
                    result.put("success", false);
                    
                    results.add(result);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("results", results);
            response.put("total", productNames.size());
            response.put("successful", results.stream().mapToInt(r -> (Boolean) r.get("success") ? 1 : 0).sum());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Bulk barcode generation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getBarcodeInfo() {
        try {
            Map<String, Object> info = new HashMap<>();
            info.put("supportedFormats", new String[]{"CODE_128"});
            info.put("maxLength", 50);
            info.put("minLength", 6);
            info.put("pattern", "^[A-Z0-9]+$");
            info.put("description", "Alphanumeric barcodes for product identification");
            
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            log.error("Failed to get barcode info: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 