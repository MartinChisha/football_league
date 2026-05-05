package com.backspacestudios.league_management.survey.repository;

import com.backspacestudios.league_management.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {
    long countBySurveyId(UUID surveyId);
    List<SurveyResponse> findBySurveyId(UUID surveyId);
}