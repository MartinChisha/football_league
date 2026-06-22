package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.competition.dto.AssignRefereeRequest;
import com.backspacestudios.league_management.competition.dto.FixtureRefereeResponse;
import com.backspacestudios.league_management.competition.entity.Fixture;
import com.backspacestudios.league_management.competition.entity.FixtureReferee;
import com.backspacestudios.league_management.competition.repository.FixtureRefereeRepository;
import com.backspacestudios.league_management.competition.repository.FixtureRepository;
import com.backspacestudios.league_management.referee.dto.RefereeAppointmentResponse;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.repository.RefereeRepository;
import com.backspacestudios.league_management.team.repository.TeamRepository;
import com.backspacestudios.league_management.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefereeAssignmentService {

    private final FixtureRefereeRepository fixtureRefereeRepository;
    private final RefereeRepository refereeRepository;
    private final UserRepository userRepository;
    private final FixtureRepository fixtureRepository;
    private final TeamRepository teamRepository;

   @Transactional
public FixtureRefereeResponse assignReferee(AssignRefereeRequest request, UUID assignedBy) {
    List<FixtureReferee> existing = fixtureRefereeRepository.findByFixtureId(request.getFixtureId());
    boolean alreadyAssigned = existing.stream()
            .anyMatch(fr -> fr.getRefereeId().equals(request.getRefereeId()));
    if (alreadyAssigned) {
        throw new RuntimeException("This referee is already assigned to this fixture");
    }

    // Remove existing assignment for the requested role (to replace)
    fixtureRefereeRepository.findByFixtureIdAndRole(request.getFixtureId(), request.getRole())
            .ifPresent(fixtureRefereeRepository::delete);

    FixtureReferee assignment = new FixtureReferee();
    assignment.setFixtureId(request.getFixtureId());
    assignment.setRefereeId(request.getRefereeId());
    assignment.setRole(request.getRole());               // ← enum passed directly
    assignment.setAssignedAt(LocalDateTime.now());
    assignment.setAssignedBy(assignedBy);
    assignment.setIsNotified(false);
    assignment = fixtureRefereeRepository.save(assignment);

    return toFixtureRefereeResponse(assignment);
}
    @Transactional
    public void removeAssignment(UUID fixtureId, String role) {
        fixtureRefereeRepository.findByFixtureIdAndRole(fixtureId, role)
                .ifPresent(fixtureRefereeRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<FixtureRefereeResponse> getAssignmentsForFixture(UUID fixtureId) {
        return fixtureRefereeRepository.findByFixtureId(fixtureId).stream()
                .map(this::toFixtureRefereeResponse)
                .toList();
    }

    // ---------- Referee's own appointments ----------
    @Transactional(readOnly = true)
    public List<RefereeAppointmentResponse> getMyAppointments(UUID userId) {
        Referee referee = refereeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Referee not found"));
        List<FixtureReferee> assignments = fixtureRefereeRepository.findByRefereeId(referee.getRefereeId());

        List<RefereeAppointmentResponse> result = new ArrayList<>();
        for (FixtureReferee fr : assignments) {
            Fixture fixture = fixtureRepository.findById(fr.getFixtureId()).orElse(null);
            if (fixture == null) continue;

            RefereeAppointmentResponse dto = new RefereeAppointmentResponse();
            dto.setAssignmentId(fr.getAssignmentId());
            dto.setFixtureId(fixture.getFixtureId());
            dto.setMatchWeek(fixture.getMatchWeek());
            dto.setHomeTeamId(fixture.getHomeTeamId());
            dto.setAwayTeamId(fixture.getAwayTeamId());
            dto.setScheduledDate(fixture.getScheduledDate());
            dto.setRole(fr.getRole().name());
            dto.setAssignedAt(fr.getAssignedAt());
            dto.setNotified(Boolean.TRUE.equals(fr.getIsNotified()));

            // Team names
            teamRepository.findById(fixture.getHomeTeamId())
                    .ifPresent(t -> dto.setHomeTeamName(t.getTeamName()));
            teamRepository.findById(fixture.getAwayTeamId())
                    .ifPresent(t -> dto.setAwayTeamName(t.getTeamName()));

            result.add(dto);
        }
        return result;
    }

    @Transactional
    public void markAsNotified(UUID assignmentId) {
        fixtureRefereeRepository.findById(assignmentId).ifPresent(assignment -> {
            assignment.setIsNotified(true);
            fixtureRefereeRepository.save(assignment);
        });
    }
    public Map<UUID, List<FixtureRefereeResponse>> getAssignmentsForSeason(UUID seasonId) {
    List<Fixture> fixtures = fixtureRepository.findBySeasonId(seasonId);
    Map<UUID, List<FixtureRefereeResponse>> map = new HashMap<>();
    for (Fixture f : fixtures) {
        List<FixtureReferee> assignments = fixtureRefereeRepository.findByFixtureId(f.getFixtureId());
        map.put(f.getFixtureId(), assignments.stream().map(this::toFixtureRefereeResponse).toList());
    }
    return map;
}

    // ---------- Private helpers ----------
    private FixtureRefereeResponse toFixtureRefereeResponse(FixtureReferee fr) {
        String refereeName = refereeRepository.findById(fr.getRefereeId())
                .map(Referee::getUserId)
                .flatMap(userRepository::findById)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Unknown");

        return FixtureRefereeResponse.builder()
                .assignmentId(fr.getAssignmentId())
                .fixtureId(fr.getFixtureId())
                .refereeId(fr.getRefereeId())
                .refereeName(refereeName)
                .role(fr.getRole().name())
                .assignedAt(fr.getAssignedAt() != null ? fr.getAssignedAt().toString() : null)
                .build();
    }
}