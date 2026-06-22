package com.backspacestudios.league_management.referee.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RefereeAppointmentResponse {
    private UUID assignmentId;
    private UUID fixtureId;
    private int matchWeek;
    private UUID homeTeamId;
    private String homeTeamName;
    private UUID awayTeamId;
    private String awayTeamName;
    private LocalDate scheduledDate;
    private String role;
    private LocalDateTime assignedAt;
    private boolean isNotified;
}