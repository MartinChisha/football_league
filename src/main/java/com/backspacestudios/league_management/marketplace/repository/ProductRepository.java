package com.backspacestudios.league_management.marketplace.repository;

import com.backspacestudios.league_management.marketplace.entity.Product;
import com.backspacestudios.league_management.marketplace.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByStoreId(UUID storeId, Pageable pageable);
    List<Product> findByStoreId(UUID storeId);
    Optional<Product> findByProductIdAndStoreId(UUID productId, UUID storeId);
    Page<Product> findByIsAvailableTrue(Pageable pageable);
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :delta WHERE p.productId = :productId")
    int adjustStock(@Param("productId") UUID productId, @Param("delta") int delta);
}