package com.backspacestudios.league_management.marketplace.repository;

import com.backspacestudios.league_management.marketplace.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    List<Store> findByIsActiveTrue();
}