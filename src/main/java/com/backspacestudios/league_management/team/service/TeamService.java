package com.backspacestudios.league_management.team.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.backspacestudios.league_management.core.service.FileUploadService;
import com.backspacestudios.league_management.league.entity.Division;
import com.backspacestudios.league_management.league.repository.DivisionRepository;
import com.backspacestudios.league_management.league.service.LeagueService;
import com.backspacestudios.league_management.team.dto.TeamRequest;
import com.backspacestudios.league_management.team.dto.TeamResponse;
import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.entity.TeamManager;
import com.backspacestudios.league_management.team.enums.FinancialStatus;
import com.backspacestudios.league_management.team.enums.ManagerStatus;
import com.backspacestudios.league_management.team.enums.TeamStatus;
import com.backspacestudios.league_management.team.repository.TeamManagerRepository;
import com.backspacestudios.league_management.team.repository.TeamRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TeamService {
    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);
    private final TeamRepository teamRepository;
    private final LeagueService leagueService;
    private final DivisionRepository divisionRepository;
     private final FileUploadService fileUploadService;  
     private final TeamManagerRepository teamManagerRepository;   // NEW
 
    TeamService(TeamRepository teamRepository, LeagueService leagueService, DivisionRepository divisionRepository, FileUploadService fileUploadService, TeamManagerRepository teamManagerRepository) {
        this.teamRepository = teamRepository;
        this.leagueService = leagueService;
        this.divisionRepository = divisionRepository;
        this.fileUploadService = fileUploadService;
        this.teamManagerRepository = teamManagerRepository;
    }
   @Transactional
public TeamResponse createTeam(TeamRequest request) {
    logger.info("Creating team '{}' in league '{}'", request.getTeamName(), request.getLeagueId());

    // Validate that the league exists (throws exception if not found)
    leagueService.getLeagueById(request.getLeagueId());

    // Validate division if provided
    if (request.getDivisionId() != null) {
        Division division = divisionRepository.findById(request.getDivisionId())
                .orElseThrow(() -> new IllegalArgumentException("Division not found"));
        if (!division.getLeagueId().equals(request.getLeagueId())) {
            throw new IllegalArgumentException("Division does not belong to the specified league");
        }
    }

    // Check team code uniqueness within the league
    if (teamRepository.existsByLeagueIdAndTeamCode(request.getLeagueId(), request.getTeamCode())) {
        logger.warn("Team code '{}' already exists in league '{}'", request.getTeamCode(), request.getLeagueId());
        throw new IllegalArgumentException("Team code already exists in this league");
    }
    
    Team team = new Team();
    team.setLeagueId(request.getLeagueId());
    team.setDivisionId(request.getDivisionId());  // may be null
    team.setTeamName(request.getTeamName());
    team.setTeamCode(request.getTeamCode());
    team.setShortName(request.getShortName());
    team.setFoundedYear(request.getFoundedYear());
    team.setHomeCity(request.getHomeCity());
    team.setHomeStadium(request.getHomeStadium());
    team.setStadiumCapacity(request.getStadiumCapacity());
    team.setClubColors(request.getClubColors());
    team.setLogoUrl(request.getLogoUrl());
    team.setWebsite(request.getWebsite());
    team.setContactEmail(request.getContactEmail());
    team.setPhoneNumber(request.getPhoneNumber());
    team.setStatus(request.getStatus() != null ? request.getStatus() : TeamStatus.active);
    team.setFinancialStatus(request.getFinancialStatus());
    team.setMetadata(request.getMetadata());

    team = teamRepository.save(team);
    logger.info("Team created with ID '{}'", team.getTeamId());
    return mapToResponse(team);
}
      @Transactional
    public TeamResponse updateTeam(UUID teamId, TeamRequest request) {
        logger.info("Updating team {}", teamId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // If team code is changed, check uniqueness within the league
        if (!team.getTeamCode().equals(request.getTeamCode()) &&
                teamRepository.existsByLeagueIdAndTeamCode(team.getLeagueId(), request.getTeamCode())) {
            throw new RuntimeException("Team code already exists in this league");
        }

        team.setTeamName(request.getTeamName());
        team.setTeamCode(request.getTeamCode());
        team.setShortName(request.getShortName());
        team.setFoundedYear(request.getFoundedYear());
        team.setHomeCity(request.getHomeCity());
        team.setHomeStadium(request.getHomeStadium());
        team.setStadiumCapacity(request.getStadiumCapacity());
        team.setClubColors(request.getClubColors());
        team.setLogoUrl(request.getLogoUrl());
        team.setWebsite(request.getWebsite());
        team.setContactEmail(request.getContactEmail());
        team.setPhoneNumber(request.getPhoneNumber());
        team.setStatus(request.getStatus());
        team.setFinancialStatus(request.getFinancialStatus());
        team.setMetadata(request.getMetadata());

        team = teamRepository.save(team);
        return mapToResponse(team);
    }
    
    @Transactional
    public void deleteTeam(UUID teamId) {
        logger.info("Deleting team {}", teamId);
        if (!teamRepository.existsById(teamId)) {
            throw new RuntimeException("Team not found");
        }
        teamRepository.deleteById(teamId);
    }

    
    @Transactional
    public TeamResponse getTeamById(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return mapToResponse(team);
    }

    @Transactional
    public List<TeamResponse> getTeamsByLeague(UUID leagueId) {
        return teamRepository.findByLeagueId(leagueId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public TeamResponse updateTeamStatus(UUID teamId, TeamStatus newStatus) {
        logger.info("Updating status of team {} to {}", teamId, newStatus);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        team.setStatus(newStatus);
        team = teamRepository.save(team);
        return mapToResponse(team);
    }

    @Transactional
    public TeamResponse updateFinancialStatus(UUID teamId, FinancialStatus newFinancialStatus) {
        logger.info("Updating financial status of team {} to {}", teamId, newFinancialStatus);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        team.setFinancialStatus(newFinancialStatus);
        team = teamRepository.save(team);
        return mapToResponse(team);
    }
    public void verifyTeamManager(UUID teamId, UUID userId) {
    TeamManager tm = teamManagerRepository.findById(userId)          // userId = PK
            .orElseThrow(() -> new RuntimeException("You are not a team manager"));
    if (!tm.getTeamId().equals(teamId) || tm.getStatus() != ManagerStatus.active) {
        throw new RuntimeException("You do not manage this team");
    }
}
    @Transactional
    public TeamResponse updateTeamLogo(UUID teamId, MultipartFile file) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        try {
            String logoUrl = fileUploadService.saveTeamLogo(file, teamId);
            team.setLogoUrl(logoUrl);
            team = teamRepository.save(team);
            return mapToResponse(team);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload logo", e);
        }
    }
@Transactional(readOnly = true)
public List<TeamResponse> getActiveTeamsByDivision(UUID divisionId) {
    logger.info("Fetching active teams for division: {}", divisionId);
    List<Team> teams = teamRepository.findByDivisionIdAndStatus(divisionId, TeamStatus.active);
    return teams.stream().map(this::mapToResponse).collect(Collectors.toList());
}


     TeamResponse mapToResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setTeamId(team.getTeamId());
        response.setLeagueId(team.getLeagueId());
        response.setTeamName(team.getTeamName());
        response.setTeamCode(team.getTeamCode());
        response.setDivisionId(team.getDivisionId());
        response.setShortName(team.getShortName());
        response.setFoundedYear(team.getFoundedYear());
        response.setHomeCity(team.getHomeCity());
        response.setHomeStadium(team.getHomeStadium());
        response.setStadiumCapacity(team.getStadiumCapacity());
        response.setClubColors(team.getClubColors());
        response.setLogoUrl(team.getLogoUrl());
        response.setWebsite(team.getWebsite());
        response.setContactEmail(team.getContactEmail());
        response.setPhoneNumber(team.getPhoneNumber());
        response.setStatus(team.getStatus());
        response.setFinancialStatus(team.getFinancialStatus());
        response.setMetadata(team.getMetadata());
        response.setCreatedAt(team.getCreatedAt());
        response.setUpdatedAt(team.getUpdatedAt());
        return response;
    }



}
