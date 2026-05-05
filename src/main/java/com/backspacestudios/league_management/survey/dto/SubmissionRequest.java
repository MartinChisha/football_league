package com.backspacestudios.league_management.survey.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class SubmissionRequest {
    private UUID surveyId;
    private String respondentType;
    private String respondentEmail;
    private List<AnswerRequest> answers;
}