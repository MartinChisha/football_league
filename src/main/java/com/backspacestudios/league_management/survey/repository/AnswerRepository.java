package com.backspacestudios.league_management.survey.repository;

import com.backspacestudios.league_management.survey.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByResponseId(UUID responseId);
}