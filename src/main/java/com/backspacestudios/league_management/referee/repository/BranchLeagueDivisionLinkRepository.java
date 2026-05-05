package com.backspacestudios.league_management.referee.repository;

import com.backspacestudios.league_management.referee.entity.BranchLeagueDivisionLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchLeagueDivisionLinkRepository extends JpaRepository<BranchLeagueDivisionLink, UUID> {

    List<BranchLeagueDivisionLink> findByLeagueId(UUID leagueId);

    List<BranchLeagueDivisionLink> findByBranchId(UUID branchId);

    Optional<BranchLeagueDivisionLink> findByBranchIdAndLeagueIdAndDivisionId(UUID branchId, UUID leagueId, UUID divisionId);

    boolean existsByBranchIdAndLeagueIdAndDivisionId(UUID branchId, UUID leagueId, UUID divisionId);

    @Query("SELECT DISTINCT l.branchId FROM BranchLeagueDivisionLink l WHERE l.leagueId = :leagueId AND l.active = true")
    List<UUID> findDistinctBranchIdsByLeagueId(@Param("leagueId") UUID leagueId);
}