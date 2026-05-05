package com.backspacestudios.league_management.league.service;

import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.league.dto.LeagueRequest;
import com.backspacestudios.league_management.league.dto.LeagueResponse;
import com.backspacestudios.league_management.league.entity.League;
import com.backspacestudios.league_management.league.enums.LeagueStatus;
import com.backspacestudios.league_management.league.enums.LeagueStructure;
import com.backspacestudios.league_management.league.enums.LeagueType;
import com.backspacestudios.league_management.league.repository.LeagueRepository;

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
public class LeagueService {

    private static final Logger logger = LoggerFactory.getLogger(LeagueService.class);

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public LeagueResponse createLeague(LeagueRequest request) {
        logger.info("Creating league with code: {}", request.getLeagueCode());

        if (leagueRepository.existsByLeagueCode(request.getLeagueCode())) {
            logger.warn("League code already exists: {}", request.getLeagueCode());
            throw new RuntimeException("League code already exists");
        }

        // Get current authenticated user (must be super admin)
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        UUID createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getUserId();

        League league = new League();
        league.setLeagueName(request.getLeagueName());
        league.setLeagueCode(request.getLeagueCode());
        league.setDescription(request.getDescription());
        league.setCountryCode(request.getCountryCode());
        league.setRegion(request.getRegion());
        league.setLeagueType(request.getLeagueType() != null ? request.getLeagueType() : LeagueType.amateur);
        league.setOverallStructure(request.getOverallStructure() != null ? request.getOverallStructure() : LeagueStructure.flat);
        league.setStatus(request.getStatus() != null ? request.getStatus() : LeagueStatus.active);
        league.setFoundedYear(request.getFoundedYear());
        league.setLogoUrl(request.getLogoUrl());
        league.setWebsite(request.getWebsite());
        league.setContactEmail(request.getContactEmail());
        league.setGlobalConfig(request.getGlobalConfig());
        league.setCreatedBy(createdBy);

        league = leagueRepository.save(league);
        logger.info("League created successfully with ID: {}", league.getLeagueId());
        return mapToResponse(league);
    }

    @Transactional(readOnly = true)
    public LeagueResponse getLeagueById(UUID leagueId) {
        logger.debug("Fetching league by ID: {}", leagueId);
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        return mapToResponse(league);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponse> getAllLeagues() {
        logger.debug("Fetching all leagues");
        return leagueRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeagueResponse updateLeague(UUID leagueId, LeagueRequest request) {
        logger.info("Updating league with ID: {}", leagueId);

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        // Check if league code is being changed and if new code already exists
        if (!league.getLeagueCode().equals(request.getLeagueCode()) &&
                leagueRepository.existsByLeagueCode(request.getLeagueCode())) {
            logger.warn("League code already exists: {}", request.getLeagueCode());
            throw new RuntimeException("League code already exists");
        }

        league.setLeagueName(request.getLeagueName());
        league.setLeagueCode(request.getLeagueCode());
        league.setDescription(request.getDescription());
        league.setCountryCode(request.getCountryCode());
        league.setRegion(request.getRegion());
        league.setLeagueType(request.getLeagueType());
        league.setOverallStructure(request.getOverallStructure());
        league.setStatus(request.getStatus());
        league.setFoundedYear(request.getFoundedYear());
        league.setLogoUrl(request.getLogoUrl());
        league.setWebsite(request.getWebsite());
        league.setContactEmail(request.getContactEmail());
        league.setGlobalConfig(request.getGlobalConfig());

        league = leagueRepository.save(league);
        logger.info("Updating league {} with data: {}", leagueId, request);
        logger.info("League updated successfully: {}", leagueId);
        return mapToResponse(league);
    }

    @Transactional
    public void deleteLeague(UUID leagueId) {
        logger.info("Deleting league with ID: {}", leagueId);
        if (!leagueRepository.existsById(leagueId)) {
            logger.warn("League not found for deletion: {}", leagueId);
            throw new RuntimeException("League not found");
        }
        leagueRepository.deleteById(leagueId);
        logger.info("League deleted: {}", leagueId);
    }
    

    // ==================== MAPPING ====================

    private LeagueResponse mapToResponse(League league) {
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