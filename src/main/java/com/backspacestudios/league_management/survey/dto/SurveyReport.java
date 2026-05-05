package com.backspacestudios.league_management.survey.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class SurveyReport {
    private UUID surveyId;
    private String surveyTitle;
    private int totalResponses;
    private List<QuestionReport> questions;
}