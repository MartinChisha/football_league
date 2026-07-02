package com.backspacestudios.league_management.player.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backspacestudios.league_management.player.dto.PlayerRequest;
import com.backspacestudios.league_management.player.dto.PlayerResponse;
import com.backspacestudios.league_management.player.service.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/players")
@PreAuthorize("hasAnyRole('team_manager', 'league_admin', 'super_admin')")
public class PlayerController {

    private PlayerService playerService;
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }


    @PostMapping
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(request));
    }

    @GetMapping("/{playerId}")
    @PreAuthorize("hasAnyRole('team_manager', 'league_admin', 'super_admin', 'referee')")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable UUID playerId) {
        return ResponseEntity.ok(playerService.getPlayerById(playerId));
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @PutMapping("/{playerId}")
    @PreAuthorize("hasRole('team_manager')")
    public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable UUID playerId, @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.ok(playerService.updatePlayer(playerId, request));
    }

    @DeleteMapping("/{playerId}")
    @PreAuthorize("hasRole('super_admin')")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID playerId) {
        playerService.deletePlayer(playerId);
        return ResponseEntity.noContent().build();
    }

     // ---------- Image upload ----------
    @PostMapping("/{playerId}/image")
    @PreAuthorize("hasAnyRole('team_manager','league_admin','super_admin')")
    public ResponseEntity<PlayerResponse> uploadPlayerImage(
            @PathVariable UUID playerId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(playerService.updatePlayerImage(playerId, file));
    }
}