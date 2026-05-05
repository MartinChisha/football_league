package com.backspacestudios.league_management.survey.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AnswerRequest {
    private UUID questionId;
    private UUID optionId;       // for radio/checkbox
    private Integer likertValue; // for likert
    private String textAnswer;   // for text fields
}