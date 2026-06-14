package com.backspacestudios.league_management.competition.repository;

import com.backspacestudios.league_management.competition.entity.FixtureReferee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FixtureRefereeRepository extends JpaRepository<FixtureReferee, UUID> {
    List<FixtureReferee> findByFixtureId(UUID fixtureId);
    Optional<FixtureReferee> findByFixtureIdAndRole(UUID fixtureId, String role);
    void deleteByFixtureIdAndRole(UUID fixtureId, String role);
}