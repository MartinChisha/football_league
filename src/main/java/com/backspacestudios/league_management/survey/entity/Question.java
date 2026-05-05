package com.backspacestudios.league_management.survey.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "questions", schema = "survey")
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionId;

    @Column(name = "survey_id")
    private UUID surveyId;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String questionType; // likert, radio, checkbox, text, select
    private Integer sortOrder = 0;
}