package com.backspacestudios.league_management.player.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backspacestudios.league_management.player.entity.PlayerContract;
import com.backspacestudios.league_management.player.enums.RegistrationStatus;

@Repository
public interface PlayerContractRepository extends JpaRepository<PlayerContract, UUID> {
    List<PlayerContract> findByTeamId(UUID teamId);
    List<PlayerContract> findByPlayerId(UUID playerId);
    List<PlayerContract> findByRegistrationStatus(RegistrationStatus status);
    List<PlayerContract> findByTeamIdAndRegistrationStatus(UUID teamId, RegistrationStatus status);
}
