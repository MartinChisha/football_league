package com.backspacestudios.league_management.survey.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "responses", schema = "survey")
@Data
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID responseId;

    @Column(name = "survey_id")
    private UUID surveyId;

    private String respondentType;   // team_manager, referee, player
    private String respondentEmail;
    @CreationTimestamp
    private LocalDateTime submittedAt;
}