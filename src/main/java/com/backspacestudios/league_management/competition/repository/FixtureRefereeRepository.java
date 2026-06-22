package com.backspacestudios.league_management.competition.repository;

import com.backspacestudios.league_management.competition.entity.FixtureReferee;
import com.backspacestudios.league_management.competition.entity.RefereeRole;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FixtureRefereeRepository extends JpaRepository<FixtureReferee, UUID> {
    List<FixtureReferee> findByFixtureId(UUID fixtureId);
    Optional<FixtureReferee> findByFixtureIdAndRole(UUID fixtureId, String role);
    void deleteByFixtureIdAndRole(UUID fixtureId, String role);
Optional<FixtureReferee> findByFixtureIdAndRole(UUID fixtureId, RefereeRole role);
    List<FixtureReferee> findByRefereeId(UUID refereeId);
    List<FixtureReferee> findByRefereeIdAndIsNotified(UUID refereeId, boolean isNotified);
}