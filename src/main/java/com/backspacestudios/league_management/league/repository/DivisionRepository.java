package com.backspacestudios.league_management.league.repository;

import com.backspacestudios.league_management.league.entity.Division;
import com.backspacestudios.league_management.league.enums.DivisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DivisionRepository extends JpaRepository<Division, UUID> {
    List<Division> findByLeagueId(UUID leagueId);
    List<Division> findByStatus(DivisionStatus status);
    List<Division> findByLeagueIdAndStatus(UUID leagueId, DivisionStatus status);
    Optional<Division> findByLeagueIdAndDivisionCode(UUID leagueId, String divisionCode);
    boolean existsByLeagueIdAndDivisionCode(UUID leagueId, String divisionCode);
    List<Division> findByParentDivisionId(UUID parentDivisionId);
}