package com.backspacestudios.league_management.marketplace.controller;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.marketplace.dto.ApprovalRequest;
import com.backspacestudios.league_management.marketplace.dto.StoreApplicationRequest;
import com.backspacestudios.league_management.marketplace.dto.StoreApplicationResponse;
import com.backspacestudios.league_management.marketplace.service.StoreApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace/store-applications")
@RequiredArgsConstructor
public class StoreApplicationController {

    private final StoreApplicationService applicationService;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    @PostMapping("/apply")
    public ResponseEntity<StoreApplicationResponse> applyForStore(@Valid @RequestBody StoreApplicationRequest request) {
        StoreApplicationResponse response = applicationService.applyForStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-application")
    public ResponseEntity<StoreApplicationResponse> getMyApplication() {
        return ResponseEntity.ok(applicationService.getMyApplication());
    }

    // Super admin endpoints
    @GetMapping("/pending")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<List<StoreApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(applicationService.getPendingApplications());
    }

    @PutMapping("/{applicationId}/approve")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<StoreApplicationResponse> approveApplication(
            @PathVariable UUID applicationId,
            @Valid @RequestBody ApprovalRequest request) {
        UUID adminId = getCurrentUserId();
        StoreApplicationResponse response = applicationService.approveApplication(applicationId, request, adminId);
        return ResponseEntity.ok(response);
    }
}