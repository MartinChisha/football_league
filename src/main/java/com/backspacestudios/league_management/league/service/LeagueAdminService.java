package com.backspacestudios.league_management.league.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.league.dto.LeagueAdminRequest;
import com.backspacestudios.league_management.league.dto.LeagueAdminResponse;
import com.backspacestudios.league_management.league.dto.LeagueResponse;
import com.backspacestudios.league_management.league.entity.League;
import com.backspacestudios.league_management.league.entity.LeagueAdmin;
import com.backspacestudios.league_management.league.enums.FinancialAccessLevel;
import com.backspacestudios.league_management.league.repository.LeagueAdminRepository;
import com.backspacestudios.league_management.league.repository.LeagueRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeagueAdminService {

    private static final Logger logger = LoggerFactory.getLogger(LeagueAdminService.class);
    private final LeagueAdminRepository leagueAdminRepository;
    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;

    LeagueAdminService(LeagueAdminRepository leagueAdminRepository, UserRepository userRepository, LeagueRepository leagueRepository) {
        this.leagueAdminRepository = leagueAdminRepository;
        this.userRepository = userRepository;
        this.leagueRepository = leagueRepository;
    }

    // ==================== SUPER ADMIN OPERATIONS ====================

    @Transactional
    public LeagueAdminResponse assignLeagueAdmin(LeagueAdminRequest request) {
        logger.info("Assigning user {} as league admin for league {}", request.getUserId(), request.getLeagueId());

        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if league exists
        League league = leagueRepository.findById(request.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));

        // Check if already a league admin for the same league
        if (leagueAdminRepository.existsByUserIdAndLeagueId(request.getUserId(), request.getLeagueId())) {
            throw new RuntimeException("User is already a league admin for this league");
        }

        // Get current admin (assigned by)
        UserDetails currentAdmin = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID assignedBy = userRepository.findByEmail(currentAdmin.getUsername())
                .orElseThrow(() -> new RuntimeException("Current admin not found"))
                .getUserId();

        // Update user's role to league_admin
        user.setRole(UserRole.league_admin);
        userRepository.save(user);

        // Create league admin record
        LeagueAdmin leagueAdmin = new LeagueAdmin();
        leagueAdmin.setUser(user);
        leagueAdmin.setLeagueId(request.getLeagueId());
        leagueAdmin.setAdminPermissions(request.getAdminPermissions());
        leagueAdmin.setCanManageReferees(request.getCanManageReferees() != null ? request.getCanManageReferees() : true);
        leagueAdmin.setCanManageTeams(request.getCanManageTeams() != null ? request.getCanManageTeams() : true);
        leagueAdmin.setCanScheduleFixtures(request.getCanScheduleFixtures() != null ? request.getCanScheduleFixtures() : true);
        leagueAdmin.setFinancialAccessLevel(request.getFinancialAccessLevel() != null ? request.getFinancialAccessLevel() : FinancialAccessLevel.none);
        leagueAdmin.setAssignedBy(assignedBy);

        leagueAdmin = leagueAdminRepository.save(leagueAdmin);
        logger.info("User {} promoted to league admin for league {}", user.getUserId(), league.getLeagueId());
        return mapToResponse(leagueAdmin, user, league);
    }
    public boolean isUserLeagueAdminForLeague(UUID userId, UUID leagueId) {
        return leagueAdminRepository.existsByUserIdAndLeagueId(userId, leagueId);
    }

    public LeagueAdminResponse getLeagueAdminByUserId(UUID userId) {
        LeagueAdmin leagueAdmin = leagueAdminRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("League admin not found"));
        User user = leagueAdmin.getUser();
        League league = leagueRepository.findById(leagueAdmin.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));
        return mapToResponse(leagueAdmin, user, league);
    }

    public List<LeagueAdminResponse> getLeagueAdminsByLeague(UUID leagueId) {
        return leagueAdminRepository.findByLeagueId(leagueId).stream()
                .map(la -> {
                    User user = la.getUser();
                    League league = leagueRepository.findById(la.getLeagueId()).orElse(null);
                    return mapToResponse(la, user, league);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeLeagueAdmin(UUID userId, UUID leagueId) {
        logger.info("Removing league admin assignment for user {} and league {}", userId, leagueId);
        LeagueAdmin leagueAdmin = leagueAdminRepository.findByUserIdAndLeagueId(userId, leagueId)
                .orElseThrow(() -> new RuntimeException("League admin assignment not found"));
        leagueAdminRepository.delete(leagueAdmin);
        // Optionally revert user's role if they have no other admin assignments
        // For simplicity, we leave the role as league_admin for now.
        logger.info("League admin removed");
    }

    // ==================== LEAGUE ADMIN OPERATIONS ====================

    @Transactional(readOnly = true)
    public LeagueResponse getCurrentLeagueAdminLeague() {
        logger.info("Fetching assigned league for current league admin");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeagueAdmin leagueAdmin = leagueAdminRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Not a league admin"));

        League league = leagueRepository.findById(leagueAdmin.getLeagueId())
                .orElseThrow(() -> new RuntimeException("Assigned league not found"));

        return mapLeagueToResponse(league);
    }

    // ==================== MAPPING METHODS ====================

    private LeagueAdminResponse mapToResponse(LeagueAdmin la, User user, League league) {
        LeagueAdminResponse response = new LeagueAdminResponse();
        response.setUserId(user.getUserId());
        response.setUserEmail(user.getEmail());
        response.setUserFirstName(user.getFirstName());
        response.setUserLastName(user.getLastName());
        response.setLeagueId(league != null ? league.getLeagueId() : la.getLeagueId());
        response.setLeagueName(league != null ? league.getLeagueName() : null);
        response.setAdminPermissions(la.getAdminPermissions());
        response.setCanManageReferees(la.getCanManageReferees());
        response.setCanManageTeams(la.getCanManageTeams());
        response.setCanScheduleFixtures(la.getCanScheduleFixtures());
        response.setFinancialAccessLevel(la.getFinancialAccessLevel());
        response.setAssignedBy(la.getAssignedBy());
        response.setAssignedAt(la.getAssignedAt());
        return response;
    }

    private LeagueResponse mapLeagueToResponse(League league) {
        LeagueResponse response = new LeagueResponse();
        response.setLeagueId(league.getLeagueId());
        response.setLeagueName(league.getLeagueName());
        response.setLeagueCode(league.getLeagueCode());
        response.setDescription(league.getDescription());
        response.setCountryCode(league.getCountryCode());
        response.setRegion(league.getRegion());
        response.setLeagueType(league.getLeagueType());
        response.setOverallStructure(league.getOverallStructure());
        response.setStatus(league.getStatus());
        response.setFoundedYear(league.getFoundedYear());
        response.setLogoUrl(league.getLogoUrl());
        response.setWebsite(league.getWebsite());
        response.setContactEmail(league.getContactEmail());
        response.setGlobalConfig(league.getGlobalConfig());
        response.setCreatedBy(league.getCreatedBy());
        response.setCreatedAt(league.getCreatedAt());
        response.setUpdatedAt(league.getUpdatedAt());
        return response;
    }
}