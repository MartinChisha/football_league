package com.backspacestudios.league_management.team.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.league.service.LeagueAdminService;
import com.backspacestudios.league_management.team.dto.TeamManagerApprovalRequest;
import com.backspacestudios.league_management.team.dto.TeamManagerRequest;
import com.backspacestudios.league_management.team.dto.TeamManagerResponse;
import com.backspacestudios.league_management.team.dto.TeamResponse;
import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.entity.TeamManager;
import com.backspacestudios.league_management.team.enums.ManagerStatus;
import com.backspacestudios.league_management.team.enums.ManagerType;
import com.backspacestudios.league_management.team.repository.TeamManagerRepository;
import com.backspacestudios.league_management.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamManagerService {

    private static final Logger logger = LoggerFactory.getLogger(TeamManagerService.class);

    private final TeamManagerRepository teamManagerRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final LeagueAdminService leagueAdminService;
    private final TeamService teamService;   // <-- inject instead of new TeamService()

    // ==================== LEAGUE ADMIN OPERATIONS ====================

    @Transactional
    public TeamManagerResponse requestTeamManager(TeamManagerRequest request) {
        logger.info("League admin requesting team manager for team {} with email {}", request.getTeamId(), request.getUserEmail());

        // 1. Validate team exists and get its league
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // 2. Validate current user (league admin) has permission for this league
        UUID currentUserId = getCurrentUserId();
        boolean isLeagueAdminForLeague = leagueAdminService.isUserLeagueAdminForLeague(currentUserId, team.getLeagueId());
        if (!isLeagueAdminForLeague) {
            throw new RuntimeException("You are not authorized to manage teams in this league");
        }

        // 3. Find user by email
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getUserEmail()));

        // 4. Check if already has a pending or active assignment for this team
        if (teamManagerRepository.existsByUserIdAndTeamIdAndStatus(user.getUserId(), request.getTeamId(), ManagerStatus.active) ||
            teamManagerRepository.existsByUserIdAndTeamIdAndStatus(user.getUserId(), request.getTeamId(), ManagerStatus.pending)) {
            throw new RuntimeException("User already has an active or pending assignment for this team");
        }

        // 5. Create pending team manager record
        TeamManager tm = new TeamManager();
        tm.setUserId(user.getUserId());
        tm.setTeamId(request.getTeamId());
        tm.setManagerType(request.getManagerType() != null ? request.getManagerType() : ManagerType.head_manager);
        tm.setCanManageRoster(request.getCanManageRoster() != null ? request.getCanManageRoster() : true);
        tm.setCanViewFinancials(request.getCanViewFinancials() != null ? request.getCanViewFinancials() : false);
        tm.setCanCommunicateLeague(request.getCanCommunicateLeague() != null ? request.getCanCommunicateLeague() : true);
        tm.setContractExpiryDate(request.getContractExpiryDate());
        tm.setAssignedBy(currentUserId);
        tm.setStatus(ManagerStatus.pending);

        tm = teamManagerRepository.save(tm);
        logger.info("Team manager request created with user {} for team {}", user.getUserId(), request.getTeamId());

        return mapToResponse(tm, user, team);
    }

    @Transactional(readOnly = true)
    public List<TeamManagerResponse> getTeamManagersByLeague(UUID leagueId) {
        logger.debug("Fetching team managers for league {}", leagueId);
        List<Team> teams = teamRepository.findByLeagueId(leagueId);
        List<UUID> teamIds = teams.stream().map(Team::getTeamId).collect(Collectors.toList());
        List<TeamManager> managers = teamManagerRepository.findAll().stream()
                .filter(tm -> teamIds.contains(tm.getTeamId()))
                .collect(Collectors.toList());

        return managers.stream()
                .map(tm -> {
                    User user = userRepository.findById(tm.getUserId()).orElse(null);
                    Team team = teamRepository.findById(tm.getTeamId()).orElse(null);
                    return mapToResponse(tm, user, team);
                })
                .collect(Collectors.toList());
    }

    // ==================== SUPER ADMIN OPERATIONS ====================

    @Transactional
    public TeamManagerResponse approveTeamManager(TeamManagerApprovalRequest request) {
        logger.info("Super admin approving team manager for user {} and team {}", request.getUserId(), request.getTeamId());

        TeamManager tm = teamManagerRepository.findByUserIdAndTeamId(request.getUserId(), request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team manager request not found"));

        if (tm.getStatus() != ManagerStatus.pending) {
            throw new RuntimeException("This request has already been processed");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID currentUserId = getCurrentUserId();

        if (request.isApprove()) {
            if (user.getRole() != UserRole.team_manager) {
                user.setRole(UserRole.team_manager);
                userRepository.save(user);
            }
            tm.setStatus(ManagerStatus.active);
            tm.setApprovedBy(currentUserId);
            tm.setApprovedAt(LocalDateTime.now());
            logger.info("Team manager request approved for user {}", request.getUserId());
        } else {
            tm.setStatus(ManagerStatus.rejected);
            tm.setApprovedBy(currentUserId);
            tm.setApprovedAt(LocalDateTime.now());
            logger.info("Team manager request rejected for user {}", request.getUserId());
        }

        tm = teamManagerRepository.save(tm);
        Team team = teamRepository.findById(tm.getTeamId()).orElse(null);
        return mapToResponse(tm, user, team);
    }

    // ==================== TEAM MANAGER OPERATIONS ====================

    @Transactional(readOnly = true)
    public TeamResponse getMyTeam() {
        UUID currentUserId = getCurrentUserId();
        TeamManager tm = teamManagerRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("You are not a team manager"));
        if (tm.getStatus() != ManagerStatus.active) {
            throw new RuntimeException("Your assignment is not active");
        }
        Team team = teamRepository.findById(tm.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));
        // Use the injected TeamService to map the team to response
        return teamService.mapToResponse(team);
    }

    // ==================== HELPER METHODS ====================

    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"))
                .getUserId();
    }

    @Transactional(readOnly = true)
    public List<TeamManagerResponse> getAllTeamManagers() {
        return teamManagerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TeamManagerResponse> getPendingTeamManagers() {
        return teamManagerRepository.findByStatus(ManagerStatus.pending).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Convenience overload: map a TeamManager to response by loading related User and Team
    private TeamManagerResponse mapToResponse(TeamManager tm) {
        if (tm == null) return null;
        User user = userRepository.findById(tm.getUserId()).orElse(null);
        Team team = teamRepository.findById(tm.getTeamId()).orElse(null);
        return mapToResponse(tm, user, team);
    }

    private TeamManagerResponse mapToResponse(TeamManager tm, User user, Team team) {
        TeamManagerResponse response = new TeamManagerResponse();
        response.setUserId(tm.getUserId());
        if (user != null) {
            response.setUserEmail(user.getEmail());
            response.setUserFirstName(user.getFirstName());
            response.setUserLastName(user.getLastName());
        }
        response.setTeamId(tm.getTeamId());
        if (team != null) {
            response.setTeamName(team.getTeamName());
        }
        response.setManagerType(tm.getManagerType());
        response.setCanManageRoster(tm.getCanManageRoster());
        response.setCanViewFinancials(tm.getCanViewFinancials());
        response.setCanCommunicateLeague(tm.getCanCommunicateLeague());
        response.setContractExpiryDate(tm.getContractExpiryDate());
        response.setAssignedBy(tm.getAssignedBy());
        response.setAssignedAt(tm.getAssignedAt());
        response.setStatus(tm.getStatus());
        response.setApprovedBy(tm.getApprovedBy());
        response.setApprovedAt(tm.getApprovedAt());
        return response;
    }
}