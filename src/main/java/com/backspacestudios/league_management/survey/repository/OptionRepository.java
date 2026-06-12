package com.backspacestudios.league_management.survey.repository;

import com.backspacestudios.league_management.survey.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
    List<Option> findByQuestionId(UUID questionId);
}