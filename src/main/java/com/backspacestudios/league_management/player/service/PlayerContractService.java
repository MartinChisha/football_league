package com.backspacestudios.league_management.player.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.player.dto.ContractApprovalRequest;
import com.backspacestudios.league_management.player.dto.PlayerContractRequest;
import com.backspacestudios.league_management.player.dto.PlayerContractResponse;
import com.backspacestudios.league_management.player.entity.Player;
import com.backspacestudios.league_management.player.entity.PlayerContract;
import com.backspacestudios.league_management.player.enums.ContractStatus;
import com.backspacestudios.league_management.player.enums.RegistrationStatus;
import com.backspacestudios.league_management.player.repository.PlayerContractRepository;
import com.backspacestudios.league_management.player.repository.PlayerRepository;
import com.backspacestudios.league_management.team.entity.Team;
import com.backspacestudios.league_management.team.repository.TeamRepository;

@Service
public class PlayerContractService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerContractService.class);

    @Autowired
    private PlayerContractRepository contractRepository;

    @Autowired
    private PlayerRepository playerRepository;   // ADDED

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== TEAM MANAGER OPERATIONS ====================

    @Transactional
    public PlayerContractResponse createContractRequest(PlayerContractRequest request) {
        logger.info("Team manager creating contract request for player {} and team {}", request.getPlayerId(), request.getTeamId());

        // Validate player exists (fetch entity)
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Validate team exists
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check if player already has an active contract with this team
        List<PlayerContract> existing = contractRepository.findByPlayerId(request.getPlayerId());
        if (existing.stream().anyMatch(c -> c.getTeamId().equals(request.getTeamId()) &&
                (c.getContractStatus() == ContractStatus.active || c.getRegistrationStatus() == RegistrationStatus.pending))) {
            throw new RuntimeException("Player already has an active or pending contract with this team");
        }

        UUID currentUserId = getCurrentUserId();

        PlayerContract contract = new PlayerContract();
        contract.setPlayerId(request.getPlayerId());
        contract.setTeamId(request.getTeamId());
        contract.setContractType(request.getContractType());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setSalaryAmount(request.getSalaryAmount());
        contract.setSalaryCurrency(request.getSalaryCurrency());
        contract.setIsLoan(request.getIsLoan() != null ? request.getIsLoan() : false);
        contract.setLoanFromTeamId(request.getLoanFromTeamId());
        contract.setSquadNumber(request.getSquadNumber());
        contract.setContractTerms(request.getContractTerms());
        contract.setContractStatus(ContractStatus.active);
        contract.setRegistrationStatus(RegistrationStatus.pending);
        contract.setCreatedBy(currentUserId);

        contract = contractRepository.save(contract);
        logger.info("Contract request created with ID: {}", contract.getContractId());
        return mapToResponse(contract, player, team);
    }

    // ==================== LEAGUE ADMIN OPERATIONS ====================

    @Transactional
    public PlayerContractResponse approveContract(ContractApprovalRequest request) {
        logger.info("League admin approving contract {}", request.getContractId());
        PlayerContract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (contract.getRegistrationStatus() != RegistrationStatus.pending) {
            throw new RuntimeException("Contract is not pending approval");
        }

        UUID currentUserId = getCurrentUserId();

        if (request.isApprove()) {
            contract.setRegistrationStatus(RegistrationStatus.registered);
            contract.setApprovedBy(currentUserId);
            contract.setApprovedAt(LocalDateTime.now());
            logger.info("Contract {} approved", request.getContractId());
        } else {
            contract.setRegistrationStatus(RegistrationStatus.unregistered);
            contract.setContractStatus(ContractStatus.terminated);
            contract.setApprovedBy(currentUserId);
            contract.setApprovedAt(LocalDateTime.now());
            logger.info("Contract {} rejected", request.getContractId());
        }

        contract = contractRepository.save(contract);
        Player player = playerRepository.findById(contract.getPlayerId()).orElse(null);
        Team team = teamRepository.findById(contract.getTeamId()).orElse(null);
        return mapToResponse(contract, player, team);
    }

    @Transactional(readOnly = true)
    public List<PlayerContractResponse> getPendingContractsByLeague(UUID leagueId) {
        List<Team> teams = teamRepository.findByLeagueId(leagueId);
        List<UUID> teamIds = teams.stream().map(Team::getTeamId).collect(Collectors.toList());
        List<PlayerContract> contracts = contractRepository.findByRegistrationStatus(RegistrationStatus.pending);
        contracts = contracts.stream()
                .filter(c -> teamIds.contains(c.getTeamId()))
                .collect(Collectors.toList());
        return contracts.stream()
                .map(c -> {
                    Player player = playerRepository.findById(c.getPlayerId()).orElse(null);
                    Team team = teamRepository.findById(c.getTeamId()).orElse(null);
                    return mapToResponse(c, player, team);
                })
                .collect(Collectors.toList());
    }

    // ==================== TEAM MANAGER VIEW ====================

    @Transactional(readOnly = true)
    public List<PlayerContractResponse> getContractsByTeam(UUID teamId) {
        List<PlayerContract> contracts = contractRepository.findByTeamId(teamId);
        return contracts.stream()
                .map(c -> {
                    Player player = playerRepository.findById(c.getPlayerId()).orElse(null);
                    Team team = teamRepository.findById(c.getTeamId()).orElse(null);
                    return mapToResponse(c, player, team);
                })
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

    private PlayerContractResponse mapToResponse(PlayerContract contract, Player player, Team team) {
        PlayerContractResponse response = new PlayerContractResponse();
        response.setContractId(contract.getContractId());
        response.setPlayerId(contract.getPlayerId());
        if (player != null) {
            response.setPlayerFullName(player.getFirstName() + " " + player.getLastName());
        }
        response.setTeamId(contract.getTeamId());
        if (team != null) {
            response.setTeamName(team.getTeamName());
        }
        response.setContractType(contract.getContractType());
        response.setStartDate(contract.getStartDate());
        response.setEndDate(contract.getEndDate());
        response.setSalaryAmount(contract.getSalaryAmount());
        response.setSalaryCurrency(contract.getSalaryCurrency());
        response.setContractStatus(contract.getContractStatus());
        response.setIsLoan(contract.getIsLoan());
        response.setLoanFromTeamId(contract.getLoanFromTeamId());
        response.setRegistrationStatus(contract.getRegistrationStatus());
        response.setSquadNumber(contract.getSquadNumber());
        response.setContractTerms(contract.getContractTerms());
        response.setCreatedAt(contract.getCreatedAt());
        response.setUpdatedAt(contract.getUpdatedAt());
        response.setCreatedBy(contract.getCreatedBy());
        response.setApprovedBy(contract.getApprovedBy());
        response.setApprovedAt(contract.getApprovedAt());
        return response;
    }
}