package com.backspacestudios.league_management.match.controller;

import com.backspacestudios.league_management.core.service.UserService;
import com.backspacestudios.league_management.match.dto.MatchEventDto;
import com.backspacestudios.league_management.match.dto.MatchReportRequest;
import com.backspacestudios.league_management.match.dto.MatchReportResponse;
import com.backspacestudios.league_management.match.service.MatchReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/match-reports")
@RequiredArgsConstructor
public class MatchReportController {

    private final MatchReportService reportService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<MatchReportResponse> getReport(@RequestParam UUID fixtureId) {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(reportService.getOrCreate(fixtureId, userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<MatchReportResponse> saveReport(@Valid @RequestBody MatchReportRequest request) {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(reportService.saveDraft(userId, request));
    }

    @PostMapping("/{reportId}/events")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<MatchReportResponse> addEvent(@PathVariable UUID reportId,
                                                        @RequestBody MatchEventDto event) {
        return ResponseEntity.ok(reportService.addEvent(reportId, event));
    }

    @DeleteMapping("/{reportId}/events/{eventId}")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID reportId,
                                            @PathVariable UUID eventId) {
        reportService.deleteEvent(reportId, eventId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reportId}/submit")
    @PreAuthorize("hasRole('referee')")
    public ResponseEntity<MatchReportResponse> submitReport(@PathVariable UUID reportId) {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(reportService.submit(reportId, userId));
    }

    @PutMapping("/{reportId}/verify")
    @PreAuthorize("hasRole('league_admin')")
    public ResponseEntity<MatchReportResponse> verifyReport(@PathVariable UUID reportId) {
        UUID userId = userService.getCurrentUser().getUserId();
        return ResponseEntity.ok(reportService.verify(reportId, userId));
    }
}