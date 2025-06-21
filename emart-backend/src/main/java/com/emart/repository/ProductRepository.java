package com.emart.repository;

import com.emart.model.Product;
import com.emart.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    Optional<Product> findByBarcode(String barcode);
    
    boolean existsByBarcode(String barcode);
    
    @Query("{'status': ?0}")
    List<Product> findByStatus(Product.ProductStatus status);
    
    List<Product> findBySupplier(User supplier);
    
    @Query("{'supplier._id': ?0, 'status': ?1}")
    List<Product> findBySupplierAndStatus(User supplier, Product.ProductStatus status);
    
    @Query("{'status': 'APPROVED', 'quantity': {$gt: 0}}")
    List<Product> findAvailableProducts();
    
    @Query("{'bestBefore': {$lt: ?0}}")
    List<Product> findExpiredProducts(LocalDate date);
    
    @Query("{'bestBefore': {$gte: ?0, $lte: ?1}}")
    List<Product> findProductsExpiringBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("{'quantity': {$lte: ?0}}")
    List<Product> findLowStockProducts(int threshold);
} 