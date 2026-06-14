package com.backspacestudios.league_management.competition.service;

import com.backspacestudios.league_management.competition.dto.AssignRefereeRequest;
import com.backspacestudios.league_management.competition.dto.FixtureRefereeResponse;
import com.backspacestudios.league_management.competition.entity.FixtureReferee;
import com.backspacestudios.league_management.competition.repository.FixtureRefereeRepository;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.repository.RefereeRepository;
import com.backspacestudios.league_management.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefereeAssignmentService {

    private final FixtureRefereeRepository fixtureRefereeRepository;
    private final RefereeRepository refereeRepository;
    private final UserRepository userRepository;

    @Transactional
    public FixtureRefereeResponse assignReferee(AssignRefereeRequest request, UUID assignedBy) {
        // Remove existing assignment for this role
        fixtureRefereeRepository.findByFixtureIdAndRole(request.getFixtureId(), request.getRole().name())
                .ifPresent(fixtureRefereeRepository::delete);

        FixtureReferee assignment = new FixtureReferee();
        assignment.setFixtureId(request.getFixtureId());
        assignment.setRefereeId(request.getRefereeId());
        assignment.setRole(request.getRole());
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setAssignedBy(assignedBy);
        assignment = fixtureRefereeRepository.save(assignment);

        return toResponse(assignment);
    }

    @Transactional
    public void removeAssignment(UUID fixtureId, String role) {
        fixtureRefereeRepository.deleteByFixtureIdAndRole(fixtureId, role);
    }

    @Transactional(readOnly = true)
    public List<FixtureRefereeResponse> getAssignmentsForFixture(UUID fixtureId) {
        return fixtureRefereeRepository.findByFixtureId(fixtureId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private FixtureRefereeResponse toResponse(FixtureReferee fr) {
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