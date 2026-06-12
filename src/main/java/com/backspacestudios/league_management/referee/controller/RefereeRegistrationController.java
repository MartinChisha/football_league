package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.referee.dto.RefereeRegistrationApprovalDTO;
import com.backspacestudios.league_management.referee.dto.RefereeRegistrationRequestDTO;
import com.backspacestudios.league_management.referee.dto.RefereeResponse;
import com.backspacestudios.league_management.referee.entity.RefereeRegistrationRequest;
import com.backspacestudios.league_management.referee.service.RefereeRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/referee/registrations")
@RequiredArgsConstructor
public class RefereeRegistrationController {

    private final RefereeRegistrationService registrationService;
    private final UserService userService;

    // Current logged-in user submits a request
    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> submitRequest(@RequestBody RefereeRegistrationRequestDTO dto) {
        UUID currentUserId = userService.getCurrentUser().getUserId();
        registrationService.submitRequest(currentUserId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Super admin or branch admin approves/rejects
    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('super_admin', 'referee')")
    public ResponseEntity<Void> processApproval(@RequestBody RefereeRegistrationApprovalDTO approvalDto) {
        UUID currentUserId = userService.getCurrentUser().getUserId();
        registrationService.processApproval(currentUserId, approvalDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<RefereeResponse> getMyRefereeProfile() {
        UUID currentUserId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(registrationService.getRefereeByUserId(currentUserId));
    }

    @GetMapping("/my-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RefereeRegistrationRequest> getMyRegistrationRequest() {
        UUID userId = userService.getCurrentUser().getUserId();
        RefereeRegistrationRequest request = registrationService.getPendingRequestByUserId(userId);
        return ResponseEntity.ok(request);
    }
}