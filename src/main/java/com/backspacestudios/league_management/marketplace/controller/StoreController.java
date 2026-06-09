package com.backspacestudios.league_management.marketplace.controller;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.marketplace.dto.StoreResponse;
import com.backspacestudios.league_management.marketplace.dto.StoreUpdateRequest;
import com.backspacestudios.league_management.marketplace.entity.Store;
import com.backspacestudios.league_management.marketplace.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    @GetMapping("/my-store")
    public ResponseEntity<StoreResponse> getMyStore() {
        UUID userId = getCurrentUserId();
        Store store = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No store found for current user"));
        StoreResponse response = StoreResponse.builder()
                .storeId(store.getStoreId())
                .userId(store.getUserId())
                .storeName(store.getStoreName())
                .storeDescription(store.getStoreDescription())
                .storeCategory(store.getStoreCategory())
                .contactEmail(store.getContactEmail())
                .contactPhone(store.getContactPhone())
                .logoImageUrl(store.getLogoImageUrl())
                .bannerImageUrl(store.getBannerImageUrl())
                .isActive(store.getIsActive())
                .commissionRate(store.getCommissionRate())
                .createdAt(store.getCreatedAt())
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/my-store")
public ResponseEntity<StoreResponse> updateMyStore(@RequestBody StoreUpdateRequest request) {
    UUID userId = getCurrentUserId();
    Store store = storeRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Store not found"));
    if (request.getStoreName() != null) store.setStoreName(request.getStoreName());
    if (request.getStoreDescription() != null) store.setStoreDescription(request.getStoreDescription());
    if (request.getContactEmail() != null) store.setContactEmail(request.getContactEmail());
    if (request.getContactPhone() != null) store.setContactPhone(request.getContactPhone());
    if (request.getLogoImageUrl() != null) store.setLogoImageUrl(request.getLogoImageUrl());
    if (request.getBannerImageUrl() != null) store.setBannerImageUrl(request.getBannerImageUrl());
    store = storeRepository.save(store);
    StoreResponse response = StoreResponse.builder()
            .storeId(store.getStoreId())
            .userId(store.getUserId())
            .storeName(store.getStoreName())
            .storeDescription(store.getStoreDescription())
            .storeCategory(store.getStoreCategory())
            .contactEmail(store.getContactEmail())
            .contactPhone(store.getContactPhone())
            .logoImageUrl(store.getLogoImageUrl())
            .bannerImageUrl(store.getBannerImageUrl())
            .isActive(store.getIsActive())
            .commissionRate(store.getCommissionRate())
            .createdAt(store.getCreatedAt())
            .build();
    return ResponseEntity.ok(response);
}
}