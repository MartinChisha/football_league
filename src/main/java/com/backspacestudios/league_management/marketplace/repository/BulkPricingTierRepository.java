package com.backspacestudios.league_management.marketplace.repository;

import com.backspacestudios.league_management.marketplace.entity.BulkPricingTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BulkPricingTierRepository extends JpaRepository<BulkPricingTier, UUID> {
    List<BulkPricingTier> findByProductProductIdOrderByMinQuantityAsc(UUID productId);
    void deleteByProductProductId(UUID productId);
}