package com.backspacestudios.league_management.player.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backspacestudios.league_management.player.dto.PlayerRequest;
import com.backspacestudios.league_management.player.dto.PlayerResponse;
import com.backspacestudios.league_management.player.entity.Player;
import com.backspacestudios.league_management.player.repository.PlayerRepository;

@Service
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private PlayerRepository playerRepository;

    @Transactional
    public PlayerResponse createPlayer(PlayerRequest request) {
        logger.info("Creating player: {} {}", request.getFirstName(), request.getLastName());
        Player player = new Player();
        mapRequestToEntity(request, player);
        player = playerRepository.save(player);
        return mapToResponse(player);
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(UUID playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        return mapToResponse(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlayerResponse updatePlayer(UUID playerId, PlayerRequest request) {
        logger.info("Updating player {}", playerId);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        mapRequestToEntity(request, player);
        player = playerRepository.save(player);
        return mapToResponse(player);
    }

    @Transactional
    public void deletePlayer(UUID playerId) {
        logger.info("Deleting player {}", playerId);
        if (!playerRepository.existsById(playerId)) {
            throw new RuntimeException("Player not found");
        }
        playerRepository.deleteById(playerId);
    }

    private void mapRequestToEntity(PlayerRequest request, Player player) {
        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setDisplayName(request.getDisplayName());
        player.setDateOfBirth(request.getDateOfBirth());
        player.setPlaceOfBirth(request.getPlaceOfBirth());
        player.setNationality(request.getNationality());
        player.setNationalitySecondary(request.getNationalitySecondary());
        player.setPreferredFoot(request.getPreferredFoot());
        player.setPrimaryPosition(request.getPrimaryPosition());
        player.setSecondaryPositions(request.getSecondaryPositions());
        player.setPlayerAgent(request.getPlayerAgent());
        player.setInternationalStatus(request.getInternationalStatus());
        player.setStatus(request.getStatus());
        player.setProfileImageUrl(request.getProfileImageUrl());
        player.setMetadata(request.getMetadata());
    }

    private PlayerResponse mapToResponse(Player player) {
        PlayerResponse response = new PlayerResponse();
        response.setPlayerId(player.getPlayerId());
        response.setFirstName(player.getFirstName());
        response.setLastName(player.getLastName());
        response.setDisplayName(player.getDisplayName());
        response.setDateOfBirth(player.getDateOfBirth());
        response.setPlaceOfBirth(player.getPlaceOfBirth());
        response.setNationality(player.getNationality());
        response.setNationalitySecondary(player.getNationalitySecondary());
        response.setPreferredFoot(player.getPreferredFoot());
        response.setPrimaryPosition(player.getPrimaryPosition());
        response.setSecondaryPositions(player.getSecondaryPositions());
        response.setPlayerAgent(player.getPlayerAgent());
        response.setInternationalStatus(player.getInternationalStatus());
        response.setStatus(player.getStatus());
        response.setProfileImageUrl(player.getProfileImageUrl());
        response.setMetadata(player.getMetadata());
        response.setCreatedAt(player.getCreatedAt());
        response.setUpdatedAt(player.getUpdatedAt());
        return response;
    }
}