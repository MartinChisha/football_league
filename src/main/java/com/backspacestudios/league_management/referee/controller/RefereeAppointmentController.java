package com.backspacestudios.league_management.referee.controller;

import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.referee.dto.RefereeAppointmentResponse;
import com.backspacestudios.league_management.referee.service.RefereeAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/referee/appointments")
@RequiredArgsConstructor
public class RefereeAppointmentController {

    private final RefereeAssignmentService assignmentService;
    private final UserService userService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<List<RefereeAppointmentResponse>> getMyAppointments() {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(assignmentService.getMyAppointments(userId));
    }

    @PutMapping("/{assignmentId}/notified")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<Void> markAsNotified(@PathVariable UUID assignmentId) {
        assignmentService.markAsNotified(assignmentId);
        return ResponseEntity.ok().build();
    }
}