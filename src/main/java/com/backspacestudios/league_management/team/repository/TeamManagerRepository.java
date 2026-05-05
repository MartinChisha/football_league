package com.backspacestudios.league_management.team.repository;

import com.backspacestudios.league_management.team.entity.TeamManager;
import com.backspacestudios.league_management.team.enums.ManagerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamManagerRepository extends JpaRepository<TeamManager, UUID> {
    Optional<TeamManager> findByUserIdAndTeamId(UUID userId, UUID teamId);
    List<TeamManager> findByTeamId(UUID teamId);
    List<TeamManager> findByStatus(ManagerStatus status);
    boolean existsByUserIdAndTeamIdAndStatus(UUID userId, UUID teamId, ManagerStatus status);
}