package com.backspacestudios.league_management.marketplace.controller;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.marketplace.dto.StoreResponse;
import com.backspacestudios.league_management.marketplace.dto.StoreUpdateRequest;
import com.backspacestudios.league_management.marketplace.entity.Store;
import com.backspacestudios.league_management.marketplace.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
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
    }

    // Public: list active stores for the customer-facing marketplace
    @GetMapping
    public ResponseEntity<List<StoreResponse>> listActiveStores() {
        List<StoreResponse> stores = storeRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }

    // Admin: list all stores (active and inactive)
    @GetMapping("/all")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<List<StoreResponse>> listAllStores() {
        List<StoreResponse> stores = storeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/my-store")
    public ResponseEntity<StoreResponse> getMyStore() {
        UUID userId = getCurrentUserId();
        Store store = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No store found for current user"));
        return ResponseEntity.ok(mapToResponse(store));
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
        return ResponseEntity.ok(mapToResponse(store));
    }

    // Public: single store detail
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return ResponseEntity.ok(mapToResponse(store));
    }

    // Admin: activate/deactivate a store
    @PatchMapping("/{storeId}/active")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<StoreResponse> setStoreActive(
            @PathVariable UUID storeId,
            @RequestParam boolean active) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        store.setIsActive(active);
        store = storeRepository.save(store);
        return ResponseEntity.ok(mapToResponse(store));
    }
}
