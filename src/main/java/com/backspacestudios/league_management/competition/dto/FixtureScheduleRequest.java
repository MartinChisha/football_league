package com.backspacestudios.league_management.competition.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FixtureScheduleRequest {
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String venue;
}