package com.backspacestudios.league_management.match.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backspacestudios.league_management.match.entity.MatchEvent;
public interface MatchEventRepository extends JpaRepository<MatchEvent, UUID> {
    List<MatchEvent> findByReportReportId(UUID reportId);
}
