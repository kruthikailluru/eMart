package com.emart.controller;

import com.emart.model.Product;
import com.emart.model.User;
import com.emart.service.ProductService;
import com.emart.service.JwtService;
import com.emart.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    private final JwtService jwtService;
    private final EmailService emailService;
    
    // Supplier endpoints
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody Product product) {
        try {
            String token = authHeader.substring(7);
            String supplierId = jwtService.extractUsername(token);
            
            Product createdProduct = productService.createProduct(product, supplierId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product created successfully and pending approval");
            response.put("product", createdProduct);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Product creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable String productId,
                                         @RequestBody Product productDetails) {
        try {
            String token = authHeader.substring(7);
            String supplierId = jwtService.extractUsername(token);
            
            Product updatedProduct = productService.updateProduct(productId, productDetails, supplierId);
            
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("Product update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable String productId) {
        try {
            String token = authHeader.substring(7);
            String supplierId = jwtService.extractUsername(token);
            
            productService.deleteProduct(productId, supplierId);
            
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            log.error("Product deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/supplier")
    public ResponseEntity<?> getSupplierProducts(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String supplierId = jwtService.extractUsername(token);
            
            List<Product> products = productService.getProductsBySupplier(supplierId);
            
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Failed to get supplier products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Admin endpoints
    @PostMapping("/{productId}/approve")
    public ResponseEntity<?> approveProduct(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable String productId) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            Product approvedProduct = productService.approveProduct(productId, adminId);
            
            // Send notification to supplier
            if (approvedProduct.getSupplier() != null && approvedProduct.getSupplier().getEmail() != null) {
                emailService.sendProductApprovalNotification(
                    approvedProduct.getSupplier().getEmail(),
                    approvedProduct.getSupplierName(),
                    approvedProduct.getName()
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product approved successfully");
            response.put("product", approvedProduct);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Product approval failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{productId}/reject")
    public ResponseEntity<?> rejectProduct(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable String productId,
                                         @RequestBody Map<String, String> rejectionRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String reason = rejectionRequest.get("reason");
            
            Product rejectedProduct = productService.rejectProduct(productId, adminId, reason);
            
            // Send notification to supplier
            if (rejectedProduct.getSupplier() != null && rejectedProduct.getSupplier().getEmail() != null) {
                emailService.sendProductRejectionNotification(
                    rejectedProduct.getSupplier().getEmail(),
                    rejectedProduct.getSupplierName(),
                    rejectedProduct.getName(),
                    reason
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product rejected successfully");
            response.put("product", rejectedProduct);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Product rejection failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingProducts() {
        try {
            List<Product> pendingProducts = productService.getPendingProducts();
            return ResponseEntity.ok(pendingProducts);
        } catch (Exception e) {
            log.error("Failed to get pending products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedProducts() {
        try {
            List<Product> approvedProducts = productService.getApprovedProducts();
            return ResponseEntity.ok(approvedProducts);
        } catch (Exception e) {
            log.error("Failed to get approved products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableProducts() {
        try {
            List<Product> availableProducts = productService.getAvailableProducts();
            return ResponseEntity.ok(availableProducts);
        } catch (Exception e) {
            log.error("Failed to get available products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/expired")
    public ResponseEntity<?> getExpiredProducts() {
        try {
            List<Product> expiredProducts = productService.getExpiredProducts();
            return ResponseEntity.ok(expiredProducts);
        } catch (Exception e) {
            log.error("Failed to get expired products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        try {
            List<Product> lowStockProducts = productService.getLowStockProducts(threshold);
            return ResponseEntity.ok(lowStockProducts);
        } catch (Exception e) {
            log.error("Failed to get low stock products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{productId}/stock")
    public ResponseEntity<?> updateStock(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String productId,
                                       @RequestBody Map<String, Integer> stockRequest) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            
            int quantity = stockRequest.get("quantity");
            Product updatedProduct = productService.updateStock(productId, quantity);
            
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("Stock update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // General endpoints
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Failed to get all products: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Failed to get product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable String barcode) {
        try {
            Product product = productService.getProductByBarcode(barcode);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            log.error("Failed to get product by barcode: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        try {
            List<Product> products = productService.searchProducts(query);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Product search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<?> getProductsByPriceRange(@RequestParam BigDecimal minPrice,
                                                   @RequestParam BigDecimal maxPrice) {
        try {
            List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Price range search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<?> getProductsExpiringBetween(@RequestParam String startDate,
                                                      @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            List<Product> products = productService.getProductsExpiringBetween(start, end);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Expiring products search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 