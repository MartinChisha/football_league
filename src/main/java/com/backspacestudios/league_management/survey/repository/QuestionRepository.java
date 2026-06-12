package com.backspacestudios.league_management.survey.repository;

import com.backspacestudios.league_management.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findBySurveyIdOrderBySortOrderAsc(UUID surveyId);
}