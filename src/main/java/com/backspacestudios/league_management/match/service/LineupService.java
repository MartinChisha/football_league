package com.backspacestudios.league_management.match.service;

import com.backspacestudios.league_management.competition.entity.Fixture;
import com.backspacestudios.league_management.competition.repository.FixtureRepository;
import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.match.dto.FixtureLineupsResponse;
import com.backspacestudios.league_management.match.dto.LineupRequest;
import com.backspacestudios.league_management.match.dto.LineupResponse;
import com.backspacestudios.league_management.match.entity.MatchLineup;
import com.backspacestudios.league_management.match.repository.MatchLineupRepository;
import com.backspacestudios.league_management.player.entity.Player;
import com.backspacestudios.league_management.player.repository.PlayerRepository;
import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LineupService {

    private final MatchLineupRepository lineupRepository;
    private final FixtureRepository fixtureRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;   // Jackson injected by Spring Boot

    /**
     * Retrieves an existing lineup for a fixture/team, or throws if not found.
     * The frontend should call this to check if a draft exists.
     */
    @Transactional(readOnly = true)
    public LineupResponse getOrCreate(UUID fixtureId, UUID teamId) {
        MatchLineup lineup = lineupRepository.findByFixtureIdAndTeamId(fixtureId, teamId)
                .orElseThrow(() -> new RuntimeException("Lineup not found – create a draft first"));
        return mapToResponse(lineup);
    }

    /**
     * Returns both teams' lineups for a fixture with player names and submitter info.
     */
    @Transactional(readOnly = true)
    public FixtureLineupsResponse getFixtureLineups(UUID fixtureId) {
        Fixture fixture = fixtureRepository.findById(fixtureId)
                .orElseThrow(() -> new RuntimeException("Fixture not found"));
        return FixtureLineupsResponse.builder()
                .home(buildTeamLineupDetail(fixtureId, fixture.getHomeTeamId()))
                .away(buildTeamLineupDetail(fixtureId, fixture.getAwayTeamId()))
                .build();
    }

    /**
     * Saves or updates a draft lineup.  Used for both initial creation and later edits.
     * If a lineup already exists for this fixture/team, it is updated.
     */
    @Transactional
    public LineupResponse saveDraft(UUID userId, LineupRequest request) {
        MatchLineup lineup = lineupRepository
                .findByFixtureIdAndTeamId(request.getFixtureId(), request.getTeamId())
                .orElse(new MatchLineup());

        lineup.setFixtureId(request.getFixtureId());
        lineup.setTeamId(request.getTeamId());
        lineup.setSubmittedBy(userId);
        lineup.setStatus("draft");
        lineup.setStartingEleven(toJson(request.getStartingEleven()));
        lineup.setSubstitutes(toJson(request.getSubstitutes()));
        lineup.setTechnicalStaff(toJson(request.getTechnicalStaff()));
        lineup = lineupRepository.save(lineup);
        return mapToResponse(lineup);
    }

    /**
     * Submits the lineup for league/referee approval.
     * Once submitted it cannot be edited by the manager (status = 'submitted').
     */
    @Transactional
    public LineupResponse submit(UUID lineupId) {
        MatchLineup lineup = lineupRepository.findById(lineupId)
                .orElseThrow(() -> new RuntimeException("Lineup not found"));
        lineup.setStatus("submitted");
        lineup.setSubmittedAt(LocalDateTime.now());
        lineup = lineupRepository.save(lineup);
        return mapToResponse(lineup);
    }

    // ---------- JSON helpers ----------
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialise JSON", e);
        }
    }

    private <T> List<T> fromJson(String json, Class<T> clazz) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialise JSON", e);
        }
    }

    private LineupResponse mapToResponse(MatchLineup l) {
        return LineupResponse.builder()
                .lineupId(l.getLineupId())
                .fixtureId(l.getFixtureId())
                .teamId(l.getTeamId())
                .submittedBy(l.getSubmittedBy())
                .submittedAt(l.getSubmittedAt())
                .status(l.getStatus())
                .startingEleven(fromJson(l.getStartingEleven(), LineupRequest.LineupPlayer.class))
                .substitutes(fromJson(l.getSubstitutes(), LineupRequest.LineupPlayer.class))
                .technicalStaff(fromJson(l.getTechnicalStaff(), LineupRequest.TechnicalStaff.class))
                .version(l.getVersion())
                .build();
    }

    private FixtureLineupsResponse.TeamLineupDetail buildTeamLineupDetail(UUID fixtureId, UUID teamId) {
        String teamName = teamRepository.findById(teamId)
                .map(Team::getTeamName)
                .orElse("Unknown team");

        Optional<MatchLineup> lineupOpt = lineupRepository.findByFixtureIdAndTeamId(fixtureId, teamId);
        if (lineupOpt.isEmpty()) {
            return FixtureLineupsResponse.TeamLineupDetail.builder()
                    .teamId(teamId)
                    .teamName(teamName)
                    .status("not_submitted")
                    .startingEleven(List.of())
                    .substitutes(List.of())
                    .technicalStaff(List.of())
                    .build();
        }

        MatchLineup lineup = lineupOpt.get();
        String submittedByName = null;
        if (lineup.getSubmittedBy() != null) {
            submittedByName = userRepository.findById(lineup.getSubmittedBy())
                    .map(this::formatUserName)
                    .orElse(null);
        }

        return FixtureLineupsResponse.TeamLineupDetail.builder()
                .teamId(teamId)
                .teamName(teamName)
                .lineupId(lineup.getLineupId())
                .submittedBy(lineup.getSubmittedBy())
                .submittedByName(submittedByName)
                .submittedAt(lineup.getSubmittedAt())
                .status(lineup.getStatus())
                .startingEleven(enrichPlayers(fromJson(lineup.getStartingEleven(), LineupRequest.LineupPlayer.class)))
                .substitutes(enrichPlayers(fromJson(lineup.getSubstitutes(), LineupRequest.LineupPlayer.class)))
                .technicalStaff(fromJson(lineup.getTechnicalStaff(), LineupRequest.TechnicalStaff.class))
                .build();
    }

    private List<FixtureLineupsResponse.EnrichedLineupPlayer> enrichPlayers(List<LineupRequest.LineupPlayer> players) {
        return players.stream().map(p -> {
            String name = playerRepository.findById(p.getPlayerId())
                    .map(this::formatPlayerName)
                    .orElse("Unknown player");
            return FixtureLineupsResponse.EnrichedLineupPlayer.builder()
                    .playerId(p.getPlayerId())
                    .name(name)
                    .position(p.getPosition())
                    .shirtNumber(p.getShirtNumber())
                    .build();
        }).toList();
    }

    private String formatPlayerName(Player player) {
        return player.getFirstName() + " " + player.getLastName();
    }

    private String formatUserName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}