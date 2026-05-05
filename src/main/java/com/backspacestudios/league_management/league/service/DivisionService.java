package com.backspacestudios.league_management.league.service;

import com.backspacestudios.league_management.league.dto.DivisionApprovalRequest;
import com.backspacestudios.league_management.league.dto.DivisionRequest;
import com.backspacestudios.league_management.league.dto.DivisionResponse;
import com.backspacestudios.league_management.league.entity.Division;
import com.backspacestudios.league_management.league.enums.DivisionStatus;
import com.backspacestudios.league_management.league.repository.DivisionRepository;
import com.backspacestudios.league_management.league.repository.LeagueRepository;
import com.backspacestudios.league_management.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DivisionService {

    private static final Logger logger = LoggerFactory.getLogger(DivisionService.class);

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== LEAGUE ADMIN OPERATIONS ====================

    @Transactional
    public DivisionResponse createDivision(DivisionRequest request) {
        logger.info("Creating division '{}' for league {}", request.getDivisionName(), request.getLeagueId());

        // Validate league exists
        if (!leagueRepository.existsById(request.getLeagueId())) {
            throw new RuntimeException("League not found");
        }

        // Validate parent division if provided
        if (request.getParentDivisionId() != null) {
            Division parent = divisionRepository.findById(request.getParentDivisionId())
                    .orElseThrow(() -> new RuntimeException("Parent division not found"));
            if (!parent.getLeagueId().equals(request.getLeagueId())) {
                throw new RuntimeException("Parent division does not belong to the same league");
            }
        }

        // Check unique division code within league
        if (divisionRepository.existsByLeagueIdAndDivisionCode(request.getLeagueId(), request.getDivisionCode())) {
            throw new RuntimeException("Division code already exists in this league");
        }

        UUID currentUserId = getCurrentUserId();

        Division division = new Division();
        division.setLeagueId(request.getLeagueId());
        division.setParentDivisionId(request.getParentDivisionId());
        division.setDivisionName(request.getDivisionName());
        division.setDivisionCode(request.getDivisionCode());
        division.setDivisionLevel(request.getDivisionLevel());
        division.setDescription(request.getDescription());
        division.setPromotionSpots(request.getPromotionSpots());
        division.setRelegationSpots(request.getRelegationSpots());
        division.setMaxTeams(request.getMaxTeams());
        division.setMinTeams(request.getMinTeams());
        division.setSortingRules(request.getSortingRules());
        division.setDivisionConfig(request.getDivisionConfig());
        division.setStatus(DivisionStatus.pending);  // pending until super admin approves
        division.setCreatedBy(currentUserId);

        division = divisionRepository.save(division);
        logger.info("Division created with ID: {} (pending approval)", division.getDivisionId());
        return mapToResponse(division);
    }

    @Transactional
    public DivisionResponse updateDivision(UUID divisionId, DivisionRequest request) {
        logger.info("Updating division {}", divisionId);
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new RuntimeException("Division not found"));

        // Cannot update if already active? We'll allow only if status is pending.
        if (division.getStatus() != DivisionStatus.pending) {
            throw new RuntimeException("Only pending divisions can be updated");
        }

        // Validate league unchanged? We'll assume leagueId same as in request.
        if (!division.getLeagueId().equals(request.getLeagueId())) {
            throw new RuntimeException("Cannot change league of a division");
        }

        // Validate parent division
        if (request.getParentDivisionId() != null) {
            Division parent = divisionRepository.findById(request.getParentDivisionId())
                    .orElseThrow(() -> new RuntimeException("Parent division not found"));
            if (!parent.getLeagueId().equals(division.getLeagueId())) {
                throw new RuntimeException("Parent division does not belong to the same league");
            }
        }

        // Check unique code if changed
        if (!division.getDivisionCode().equals(request.getDivisionCode()) &&
                divisionRepository.existsByLeagueIdAndDivisionCode(division.getLeagueId(), request.getDivisionCode())) {
            throw new RuntimeException("Division code already exists in this league");
        }

        division.setParentDivisionId(request.getParentDivisionId());
        division.setDivisionName(request.getDivisionName());
        division.setDivisionCode(request.getDivisionCode());
        division.setDivisionLevel(request.getDivisionLevel());
        division.setDescription(request.getDescription());
        division.setPromotionSpots(request.getPromotionSpots());
        division.setRelegationSpots(request.getRelegationSpots());
        division.setMaxTeams(request.getMaxTeams());
        division.setMinTeams(request.getMinTeams());
        division.setSortingRules(request.getSortingRules());
        division.setDivisionConfig(request.getDivisionConfig());

        division = divisionRepository.save(division);
        return mapToResponse(division);
    }

    @Transactional(readOnly = true)
    public List<DivisionResponse> getDivisionsByLeague(UUID leagueId) {
        logger.debug("Fetching divisions for league {}", leagueId);
        return divisionRepository.findByLeagueId(leagueId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DivisionResponse getDivisionById(UUID divisionId) {
        Division division = divisionRepository.findById(divisionId)
                .orElseThrow(() -> new RuntimeException("Division not found"));
        return mapToResponse(division);
    }

    // ==================== SUPER ADMIN OPERATIONS ====================

    @Transactional
    public DivisionResponse approveDivision(DivisionApprovalRequest request) {
        logger.info("Super admin approving division {}", request.getDivisionId());
        Division division = divisionRepository.findById(request.getDivisionId())
                .orElseThrow(() -> new RuntimeException("Division not found"));

        if (division.getStatus() != DivisionStatus.pending) {
            throw new RuntimeException("Division is not pending approval");
        }

        if (request.isApprove()) {
            division.setStatus(DivisionStatus.active);
            logger.info("Division {} approved", request.getDivisionId());
        } else {
            division.setStatus(DivisionStatus.inactive);
            logger.info("Division {} rejected", request.getDivisionId());
        }

        division = divisionRepository.save(division);
        return mapToResponse(division);
    }

    @Transactional(readOnly = true)
    public List<DivisionResponse> getPendingDivisions() {
        logger.debug("Fetching all pending divisions");
        return divisionRepository.findByStatus(DivisionStatus.pending).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private UUID getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"))
                .getUserId();
    }

    private DivisionResponse mapToResponse(Division division) {
        DivisionResponse response = new DivisionResponse();
        response.setDivisionId(division.getDivisionId());
        response.setLeagueId(division.getLeagueId());
        response.setParentDivisionId(division.getParentDivisionId());
        response.setDivisionName(division.getDivisionName());
        response.setDivisionCode(division.getDivisionCode());
        response.setDivisionLevel(division.getDivisionLevel());
        response.setDescription(division.getDescription());
        response.setPromotionSpots(division.getPromotionSpots());
        response.setRelegationSpots(division.getRelegationSpots());
        response.setMaxTeams(division.getMaxTeams());
        response.setMinTeams(division.getMinTeams());
        response.setSortingRules(division.getSortingRules());
        response.setStatus(division.getStatus());
        response.setDivisionConfig(division.getDivisionConfig());
        response.setCreatedBy(division.getCreatedBy());
        response.setCreatedAt(division.getCreatedAt());
        response.setUpdatedAt(division.getUpdatedAt());
        return response;
    }
}