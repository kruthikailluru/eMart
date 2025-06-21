package com.emart.service;

import com.emart.model.Product;
import com.emart.model.User;
import com.emart.repository.ProductRepository;
import com.emart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BarcodeService barcodeService;
    
    public Product createProduct(Product product, String supplierId) {
        // Validate supplier exists
        User supplier = userRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        if (supplier.getRole() != User.UserRole.SUPPLIER) {
            throw new RuntimeException("User is not a supplier");
        }
        
        // Generate unique barcode
        String barcode = generateUniqueBarcode(product.getName());
        product.setBarcode(barcode);
        
        // Set supplier information
        product.setSupplier(supplier);
        product.setSupplierName(supplier.getFirstName() + " " + supplier.getLastName());
        product.setSupplierEmail(supplier.getEmail());
        
        // Set initial status and timestamps
        product.setStatus(Product.ProductStatus.PENDING);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product approveProduct(String productId, String adminId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Validate admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (admin.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }
        
        product.setStatus(Product.ProductStatus.APPROVED);
        product.setApprovedBy(adminId);
        product.setApprovedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product rejectProduct(String productId, String adminId, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Validate admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (admin.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }
        
        product.setStatus(Product.ProductStatus.REJECTED);
        product.setApprovedBy(adminId);
        product.setApprovedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(String productId, Product productDetails, String supplierId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if product belongs to supplier
        if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplierId)) {
            throw new RuntimeException("Product does not belong to this supplier");
        }
        
        // Only allow updates if product is pending
        if (product.getStatus() != Product.ProductStatus.PENDING) {
            throw new RuntimeException("Cannot update approved/rejected product");
        }
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setBestBefore(productDetails.getBestBefore());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(String productId, String supplierId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if product belongs to supplier
        if (product.getSupplier() == null || !product.getSupplier().getId().equals(supplierId)) {
            throw new RuntimeException("Product does not belong to this supplier");
        }
        
        // Only allow deletion if product is pending
        if (product.getStatus() != Product.ProductStatus.PENDING) {
            throw new RuntimeException("Cannot delete approved/rejected product");
        }
        
        productRepository.delete(product);
    }
    
    public Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public Product getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        return productRepository.findByStatus(status);
    }
    
    public List<Product> getProductsBySupplier(String supplierId) {
        User supplier = userRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return productRepository.findBySupplier(supplier);
    }
    
    public List<Product> getPendingProducts() {
        return productRepository.findByStatus(Product.ProductStatus.PENDING);
    }
    
    public List<Product> getApprovedProducts() {
        return productRepository.findByStatus(Product.ProductStatus.APPROVED);
    }
    
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }
    
    public List<Product> getExpiredProducts() {
        return productRepository.findExpiredProducts(LocalDate.now());
    }
    
    public List<Product> getProductsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return productRepository.findProductsExpiringBetween(startDate, endDate);
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
    
    public Product updateStock(String productId, int quantity) {
        Product product = getProductById(productId);
        
        if (product.getStatus() != Product.ProductStatus.APPROVED) {
            throw new RuntimeException("Cannot update stock for non-approved product");
        }
        
        int newQuantity = product.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        product.setQuantity(newQuantity);
        
        // Update status if out of stock
        if (newQuantity == 0) {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public boolean checkStockAvailability(String productId, int requestedQuantity) {
        Product product = getProductById(productId);
        return product.getStatus() == Product.ProductStatus.APPROVED && 
               product.getQuantity() >= requestedQuantity;
    }
    
    private String generateUniqueBarcode(String productName) {
        String baseBarcode = barcodeService.generateBarcode(productName);
        String barcode = baseBarcode;
        int counter = 1;
        
        // Ensure uniqueness
        while (productRepository.existsByBarcode(barcode)) {
            barcode = baseBarcode + "_" + counter;
            counter++;
        }
        
        return barcode;
    }
    
    public List<Product> searchProducts(String searchTerm) {
        // This would typically use a more sophisticated search
        // For now, we'll search by name containing the term
        return productRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }
    
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findAll().stream()
                .filter(product -> product.getStatus() == Product.ProductStatus.APPROVED)
                .filter(product -> product.getPrice().compareTo(minPrice) >= 0 && 
                                 product.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }
} 