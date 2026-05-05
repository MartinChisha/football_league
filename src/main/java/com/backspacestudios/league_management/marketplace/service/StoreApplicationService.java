package com.backspacestudios.league_management.marketplace.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.marketplace.dto.*;
import com.backspacestudios.league_management.marketplace.entity.Store;
import com.backspacestudios.league_management.marketplace.entity.StoreApplication;
import com.backspacestudios.league_management.marketplace.enums.ApplicationStatus;
import com.backspacestudios.league_management.marketplace.repository.StoreApplicationRepository;
import com.backspacestudios.league_management.marketplace.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreApplicationService {

    private final StoreApplicationRepository applicationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // Current logged-in user ID helper
    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    @Transactional
    public StoreApplicationResponse applyForStore(StoreApplicationRequest request) {
        UUID userId = getCurrentUserId();

        // Check if user already has a pending application or an approved store
        if (applicationRepository.existsByUserIdAndStatus(userId, ApplicationStatus.pending)) {
            throw new IllegalStateException("You already have a pending store application");
        }
        if (storeRepository.existsByUserId(userId)) {
            throw new IllegalStateException("You already own a store");
        }

        StoreApplication application = new StoreApplication();
        application.setUserId(userId);
        application.setStoreName(request.getStoreName());
        application.setStoreDescription(request.getStoreDescription());
        application.setStoreCategory(request.getStoreCategory());
        application.setContactEmail(request.getContactEmail());
        application.setContactPhone(request.getContactPhone());
        application.setTaxId(request.getTaxId());
        application.setStatus(ApplicationStatus.pending);

        StoreApplication saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StoreApplicationResponse> getPendingApplications() {
        return applicationRepository.findByStatus(ApplicationStatus.pending)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreApplicationResponse approveApplication(UUID applicationId, ApprovalRequest approvalRequest, UUID adminUserId) {
        StoreApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getStatus() != ApplicationStatus.pending) {
            throw new IllegalStateException("Application is not in pending state");
        }

        if (approvalRequest.getApproved()) {
            // Approve: create store
            application.setStatus(ApplicationStatus.approved);
            application.setApprovedAt(LocalDateTime.now());
            application.setReviewedByUserId(adminUserId);
            application.setReviewNotes(approvalRequest.getReviewNotes());

            Store store = new Store();
            store.setUserId(application.getUserId());
            store.setStoreName(application.getStoreName());
            store.setStoreDescription(application.getStoreDescription());
            store.setStoreCategory(application.getStoreCategory());
            store.setContactEmail(application.getContactEmail());
            store.setContactPhone(application.getContactPhone());
            store.setTaxId(application.getTaxId());
            store.setIsActive(true);
            storeRepository.save(store);
        } else {
            // Reject
            application.setStatus(ApplicationStatus.rejected);
            application.setRejectedAt(LocalDateTime.now());
            application.setReviewedByUserId(adminUserId);
            application.setReviewNotes(approvalRequest.getReviewNotes());
        }

        StoreApplication updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public StoreApplicationResponse getMyApplication() {
        UUID userId = getCurrentUserId();
        StoreApplication application = applicationRepository.findByUserIdAndStatus(userId, ApplicationStatus.pending)
                .orElseThrow(() -> new RuntimeException("No pending application found"));
        return mapToResponse(application);
    }

    private StoreApplicationResponse mapToResponse(StoreApplication app) {
        return StoreApplicationResponse.builder()
                .applicationId(app.getApplicationId())
                .userId(app.getUserId())
                .storeName(app.getStoreName())
                .storeDescription(app.getStoreDescription())
                .storeCategory(app.getStoreCategory())
                .contactEmail(app.getContactEmail())
                .contactPhone(app.getContactPhone())
                .taxId(app.getTaxId())
                .status(app.getStatus())
                .reviewNotes(app.getReviewNotes())
                .approvedAt(app.getApprovedAt())
                .rejectedAt(app.getRejectedAt())
                .createdAt(app.getCreatedAt())
                .build();
    }
}