package com.backspacestudios.league_management.referee.repository;

import com.backspacestudios.league_management.referee.entity.Referee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefereeRepository extends JpaRepository<Referee, UUID> {
    Optional<Referee> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    Optional<Referee> findByRefereeCode(String refereeCode);
}