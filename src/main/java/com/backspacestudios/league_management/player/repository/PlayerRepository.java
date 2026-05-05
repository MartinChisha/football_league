package com.backspacestudios.league_management.player.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backspacestudios.league_management.player.entity.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
}
