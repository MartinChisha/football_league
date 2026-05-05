package com.backspacestudios.league_management.marketplace.repository;

import com.backspacestudios.league_management.marketplace.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProductProductIdOrderByDisplayOrderAsc(UUID productId);
    void deleteByProductProductId(UUID productId);
}