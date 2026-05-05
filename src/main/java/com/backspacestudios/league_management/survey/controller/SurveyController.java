package com.backspacestudios.league_management.survey.controller;

import com.backspacestudios.league_management.survey.dto.SubmissionRequest;
import com.backspacestudios.league_management.survey.dto.SurveyReport;
import com.backspacestudios.league_management.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping("/submit")
    public ResponseEntity<Void> submit(@RequestBody SubmissionRequest request) {
        surveyService.submitResponses(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report/{surveyId}")
    public ResponseEntity<SurveyReport> getReport(@PathVariable UUID surveyId) {
        return ResponseEntity.ok(surveyService.getReport(surveyId));
    }
}