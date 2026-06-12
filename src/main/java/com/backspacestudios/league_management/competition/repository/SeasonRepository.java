package com.backspacestudios.league_management.competition.repository;

import com.backspacestudios.league_management.competition.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeasonRepository extends JpaRepository<Season, UUID> {
    Optional<Season> findByDivisionIdAndSeasonYear(UUID divisionId, Integer year);
    List<Season> findByDivisionId(UUID divisionId);
    boolean existsByDivisionIdAndSeasonYear(UUID divisionId, Integer year);
}