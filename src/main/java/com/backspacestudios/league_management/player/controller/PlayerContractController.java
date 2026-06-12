package com.backspacestudios.league_management.player.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backspacestudios.league_management.player.dto.ContractApprovalRequest;
import com.backspacestudios.league_management.player.dto.PlayerContractRequest;
import com.backspacestudios.league_management.player.dto.PlayerContractResponse;
import com.backspacestudios.league_management.player.service.PlayerContractService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contracts")
public class PlayerContractController {

   private final PlayerContractService contractService;

    PlayerContractController(PlayerContractService contractService) {
        this.contractService = contractService;
    }

    // Team manager creates contract request
    @PostMapping
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<PlayerContractResponse> createContractRequest(@Valid @RequestBody PlayerContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createContractRequest(request));
    }

    // League admin views pending contracts for their league (needs leagueId param)
    @GetMapping("/pending/league/{leagueId}")
    @PreAuthorize("hasRole('league_admin')")
    public ResponseEntity<List<PlayerContractResponse>> getPendingContractsByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(contractService.getPendingContractsByLeague(leagueId));
    }

    // League admin approves/rejects
    @PutMapping("/approve")
    @PreAuthorize("hasRole('league_admin')")
    public ResponseEntity<PlayerContractResponse> approveContract(@Valid @RequestBody ContractApprovalRequest request) {
        return ResponseEntity.ok(contractService.approveContract(request));
    }

    // Team manager views contracts for their team
    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<List<PlayerContractResponse>> getContractsByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(contractService.getContractsByTeam(teamId));
    }
}