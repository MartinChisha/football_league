package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.referee.dto.RefereeRegistrationApprovalDTO;
import com.backspacestudios.league_management.referee.dto.RefereeRegistrationRequestDTO;
import com.backspacestudios.league_management.referee.dto.RefereeResponse;
import com.backspacestudios.league_management.referee.service.RefereeRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/referee/registrations")
public class RefereeRegistrationController {

    @Autowired
    private RefereeRegistrationService registrationService;

    @Autowired
    private UserService userService;

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
    @PreAuthorize("hasAnyRole('super_admin', 'referee')") // REFEREE role but we'll check branch admin inside service
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
}