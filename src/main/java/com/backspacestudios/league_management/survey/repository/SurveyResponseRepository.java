package com.backspacestudios.league_management.survey.repository;

import com.backspacestudios.league_management.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {
    long countBySurveyId(UUID surveyId);
    List<SurveyResponse> findBySurveyId(UUID surveyId);
}