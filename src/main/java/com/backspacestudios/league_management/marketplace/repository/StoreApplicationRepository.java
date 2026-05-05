package com.backspacestudios.league_management.marketplace.repository;

import com.backspacestudios.league_management.marketplace.entity.StoreApplication;
import com.backspacestudios.league_management.marketplace.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreApplicationRepository extends JpaRepository<StoreApplication, UUID> {
    Optional<StoreApplication> findByUserIdAndStatus(UUID userId, ApplicationStatus status);
    List<StoreApplication> findByStatus(ApplicationStatus status);
    boolean existsByUserIdAndStatus(UUID userId, ApplicationStatus status);
}