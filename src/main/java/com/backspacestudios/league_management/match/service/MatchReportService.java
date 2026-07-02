package com.backspacestudios.league_management.match.service;

import com.backspacestudios.league_management.match.dto.*;
import com.backspacestudios.league_management.match.entity.MatchEvent;
import com.backspacestudios.league_management.match.entity.MatchReport;
import com.backspacestudios.league_management.match.repository.MatchEventRepository;
import com.backspacestudios.league_management.match.repository.MatchReportRepository;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.repository.RefereeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchReportService {

    private final MatchReportRepository reportRepository;
    private final MatchEventRepository eventRepository;
    private final RefereeRepository refereeRepository;
    private final StatisticsService statisticsService;

    /**
     * Retrieves an existing report for a fixture, or creates a new blank one
     * if none exists.  The referee's identity is obtained from their user ID.
     */
    @Transactional
    public MatchReportResponse getOrCreate(UUID fixtureId, UUID refereeUserId) {
        Referee referee = refereeRepository.findByUserId(refereeUserId)
                .orElseThrow(() -> new RuntimeException("Referee profile not found"));
        MatchReport report = reportRepository.findByFixtureId(fixtureId)
                .orElseGet(() -> {
                    MatchReport r = new MatchReport();
                    r.setFixtureId(fixtureId);
                    r.setRefereeId(referee.getRefereeId());
                    r.setCreatedBy(refereeUserId);
                    return reportRepository.save(r);
                });
        return mapToResponse(report);
    }

    /**
     * Saves a draft report (may be called multiple times).
     */
    @Transactional
    public MatchReportResponse saveDraft(UUID userId, MatchReportRequest request) {
        MatchReport report = reportRepository.findByFixtureId(request.getFixtureId())
                .orElseThrow(() -> new RuntimeException("Report not initialised"));
        updateFields(report, request);
        report.setUpdatedBy(userId);
        report.setUpdatedAt(LocalDateTime.now());
        report.setReportStatus(request.getReportStatus() != null ? request.getReportStatus() : "draft");
        return mapToResponse(reportRepository.save(report));
    }

    /**
     * Adds a single event to a report.
     */
    @Transactional
    public MatchReportResponse addEvent(UUID reportId, MatchEventDto dto) {
        MatchReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        MatchEvent event = new MatchEvent();
        event.setReport(report);
        event.setEventType(dto.getEventType());
        event.setMinute(dto.getMinute());
        event.setPlayerId(dto.getPlayerId());
        event.setSecondaryPlayerId(dto.getSecondaryPlayerId());
        event.setTeamId(dto.getTeamId());
        event.setDescription(dto.getDescription());
        event.setAdditionalData(dto.getAdditionalData());
        eventRepository.save(event);
        return mapToResponse(report);
    }

    /**
     * Removes an event from a report.
     */
    @Transactional
    public void deleteEvent(UUID reportId, UUID eventId) {
        MatchEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (!event.getReport().getReportId().equals(reportId)) {
            throw new RuntimeException("Event does not belong to the specified report");
        }
        eventRepository.delete(event);
    }

    /**
     * Submits the report (referee action).  Status becomes 'submitted'.
     */
    @Transactional
    public MatchReportResponse submit(UUID reportId, UUID userId) {
        MatchReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setReportStatus("submitted");
        report.setUpdatedBy(userId);
        report.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(reportRepository.save(report));
    }

    /**
     * League admin verifies the report.
     * Triggers real‑time statistics update for the corresponding season.
     */
    @Transactional
    public MatchReportResponse verify(UUID reportId, UUID adminUserId) {
        MatchReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setReportStatus("verified");
        report.setUpdatedBy(adminUserId);
        report.setUpdatedAt(LocalDateTime.now());
        MatchReport saved = reportRepository.save(report);

        // Recalculate player / team / standings statistics
        statisticsService.updateSeasonStats(saved.getFixtureId());
        return mapToResponse(saved);
    }

    // ---------- Private helpers ----------
    private void updateFields(MatchReport report, MatchReportRequest req) {
        report.setHomeScore(req.getHomeScore());
        report.setAwayScore(req.getAwayScore());
        report.setMatchStartTime(req.getMatchStartTime());
        report.setMatchEndTime(req.getMatchEndTime());
        report.setAttendance(req.getAttendance());
        report.setWeatherConditions(req.getWeatherConditions());
        report.setHomePossession(req.getHomePossession());
        report.setAwayPossession(req.getAwayPossession());
        report.setHomeShots(req.getHomeShots());
        report.setAwayShots(req.getAwayShots());
        report.setHomeShotsOnTarget(req.getHomeShotsOnTarget());
        report.setAwayShotsOnTarget(req.getAwayShotsOnTarget());
        report.setHomeFouls(req.getHomeFouls());
        report.setAwayFouls(req.getAwayFouls());
        report.setHomeCorners(req.getHomeCorners());
        report.setAwayCorners(req.getAwayCorners());
        report.setHomeOffsides(req.getHomeOffsides());
        report.setAwayOffsides(req.getAwayOffsides());
    }

    private MatchReportResponse mapToResponse(MatchReport r) {
        List<MatchEventDto> eventDtos = r.getEvents().stream().map(e -> {
            MatchEventDto dto = new MatchEventDto();
            dto.setEventType(e.getEventType());
            dto.setMinute(e.getMinute());
            dto.setPlayerId(e.getPlayerId());
            dto.setSecondaryPlayerId(e.getSecondaryPlayerId());
            dto.setTeamId(e.getTeamId());
            dto.setDescription(e.getDescription());
            dto.setAdditionalData(e.getAdditionalData());
            return dto;
        }).toList();

        return MatchReportResponse.builder()
                .reportId(r.getReportId())
                .fixtureId(r.getFixtureId())
                .homeScore(r.getHomeScore())
                .awayScore(r.getAwayScore())
                .reportStatus(r.getReportStatus())
                .matchStartTime(r.getMatchStartTime())
                .matchEndTime(r.getMatchEndTime())
                .attendance(r.getAttendance())
                .weatherConditions(r.getWeatherConditions())
                .homePossession(r.getHomePossession())
                .awayPossession(r.getAwayPossession())
                .homeShots(r.getHomeShots())
                .awayShots(r.getAwayShots())
                .homeShotsOnTarget(r.getHomeShotsOnTarget())
                .awayShotsOnTarget(r.getAwayShotsOnTarget())
                .homeFouls(r.getHomeFouls())
                .awayFouls(r.getAwayFouls())
                .homeCorners(r.getHomeCorners())
                .awayCorners(r.getAwayCorners())
                .homeOffsides(r.getHomeOffsides())
                .awayOffsides(r.getAwayOffsides())
                .events(eventDtos)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}