package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.league.entity.Division;
import com.backspacestudios.league_management.league.entity.League;
import com.backspacestudios.league_management.league.enums.LeagueStatus;
import com.backspacestudios.league_management.league.repository.DivisionRepository;
import com.backspacestudios.league_management.league.repository.LeagueRepository;
import com.backspacestudios.league_management.referee.dto.BranchLeagueLinkRequest;
import com.backspacestudios.league_management.referee.dto.BranchLeagueLinkResponse;
import com.backspacestudios.league_management.referee.dto.RefereeForLeagueResponse;
import com.backspacestudios.league_management.referee.entity.BranchLeagueDivisionLink;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.entity.RefereeBranch;
import com.backspacestudios.league_management.referee.entity.RefereeBranchMembership;
import com.backspacestudios.league_management.referee.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BranchLeagueLinkService {
private final BranchLeagueDivisionLinkRepository linkRepository;

private final RefereeBranchRepository branchRepository;

private final LeagueRepository leagueRepository;

private final DivisionRepository divisionRepository;
private final RefereeBranchMembershipRepository membershipRepository;

private final RefereeRepository refereeRepository;

    
    private UserRepository userRepository;

    BranchLeagueLinkService(BranchLeagueDivisionLinkRepository linkRepository, RefereeBranchRepository branchRepository, LeagueRepository leagueRepository, DivisionRepository divisionRepository, RefereeRepository refereeRepository, RefereeBranchMembershipRepository membershipRepository) {
        this.linkRepository = linkRepository;
        this.branchRepository = branchRepository;
        this.leagueRepository = leagueRepository;
        this.divisionRepository = divisionRepository;
        this.refereeRepository = refereeRepository;
        this.membershipRepository = membershipRepository;
    }

    // ========== SUPER ADMIN OPERATIONS ==========

    @Transactional
public BranchLeagueLinkResponse createLink(BranchLeagueLinkRequest request) {
    // Validate branch
    RefereeBranch branch = branchRepository.findById(request.getBranchId())
            .orElseThrow(() -> new RuntimeException("Referee branch not found"));
    // Validate league
    League league = leagueRepository.findById(request.getLeagueId())
            .orElseThrow(() -> new RuntimeException("League not found"));
    // Validate division if provided
    if (request.getDivisionId() != null) {
        Division division = divisionRepository.findById(request.getDivisionId())
                .orElseThrow(() -> new RuntimeException("Division not found"));
        if (!division.getLeagueId().equals(request.getLeagueId())) {
            throw new RuntimeException("Division does not belong to the specified league");
        }
    }
    
    // Optional: Add logging or additional validation using branch/league
    if (!branch.getStatus().equals("active")) {
        throw new RuntimeException("Branch is not active");
    }
    if (league.getStatus() != LeagueStatus.active) {
        throw new RuntimeException("League is not active");
    }
    
    // Check for duplicate
    if (linkRepository.existsByBranchIdAndLeagueIdAndDivisionId(
            request.getBranchId(), request.getLeagueId(), request.getDivisionId())) {
        throw new RuntimeException("Link already exists");
    }

    BranchLeagueDivisionLink link = new BranchLeagueDivisionLink();
    link.setBranchId(request.getBranchId());
    link.setLeagueId(request.getLeagueId());
    link.setDivisionId(request.getDivisionId());
    link.setActive(true);
    link = linkRepository.save(link);

    return mapToResponse(link);
}
    @Transactional
    public void removeLink(UUID linkId) {
        if (!linkRepository.existsById(linkId)) {
            throw new RuntimeException("Link not found");
        }
        linkRepository.deleteById(linkId);
    }

    @Transactional
    public BranchLeagueLinkResponse toggleActive(UUID linkId, boolean active) {
        BranchLeagueDivisionLink link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Link not found"));
        link.setActive(active);
        link = linkRepository.save(link);
        return mapToResponse(link);
    }

    public List<BranchLeagueLinkResponse> getLinksByLeague(UUID leagueId) {
        return linkRepository.findByLeagueId(leagueId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BranchLeagueLinkResponse> getLinksByBranch(UUID branchId) {
        return linkRepository.findByBranchId(branchId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========== LEAGUE ADMIN OPERATIONS ==========

    /**
     * Returns all referees who belong to branches linked to the given league.
     * If a link has divisionId, only referees with membership in that branch are included,
     * but we don't filter by division at referee level because referee belongs to branch, not division.
     * Division filtering is for match appointment later.
     */
    public List<RefereeForLeagueResponse> getRefereesForLeague(UUID leagueId) {
        // Get all branch IDs linked to this league
        List<UUID> branchIds = linkRepository.findDistinctBranchIdsByLeagueId(leagueId);
        if (branchIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Fetch all active memberships for these branches
        List<RefereeBranchMembership> memberships = membershipRepository.findByBranchIdInAndStatus(branchIds, "active");
        // Extract referee IDs
        List<UUID> refereeIds = memberships.stream()
                .map(RefereeBranchMembership::getRefereeId)
                .distinct()
                .collect(Collectors.toList());

        if (refereeIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Fetch referees and their users
        List<Referee> referees = refereeRepository.findAllById(refereeIds);
        List<User> users = userRepository.findAllById(referees.stream().map(Referee::getUserId).collect(Collectors.toList()));
        java.util.Map<UUID, User> userMap = users.stream().collect(Collectors.toMap(User::getUserId, u -> u));

        // Build response: for each membership, we get referee + branch info
        List<RefereeForLeagueResponse> result = new ArrayList<>();
        for (RefereeBranchMembership membership : memberships) {
            Referee referee = referees.stream()
                    .filter(r -> r.getRefereeId().equals(membership.getRefereeId()))
                    .findFirst().orElse(null);
            if (referee == null) continue;

            User user = userMap.get(referee.getUserId());
            if (user == null) continue;

            RefereeBranch branch = branchRepository.findById(membership.getBranchId()).orElse(null);
            if (branch == null) continue;

            RefereeForLeagueResponse resp = new RefereeForLeagueResponse();
            resp.setRefereeId(referee.getRefereeId());
            resp.setUserId(user.getUserId());
            resp.setFirstName(user.getFirstName());
            resp.setLastName(user.getLastName());
            resp.setEmail(user.getEmail());
            resp.setRefereeCode(referee.getRefereeCode());
            resp.setCurrentClass(referee.getCurrentClass());
            resp.setBranchId(branch.getBranchId());
            resp.setBranchName(branch.getBranchName());
            result.add(resp);
        }
        return result;
    }

    // Helper mapping
    private BranchLeagueLinkResponse mapToResponse(BranchLeagueDivisionLink link) {
        BranchLeagueLinkResponse resp = new BranchLeagueLinkResponse();
        resp.setLinkId(link.getLinkId());
        resp.setBranchId(link.getBranchId());
        resp.setLeagueId(link.getLeagueId());
        resp.setDivisionId(link.getDivisionId());
        resp.setActive(link.getActive());
        resp.setCreatedAt(link.getCreatedAt());

        // Fetch names (optional, can be null if not needed)
        branchRepository.findById(link.getBranchId()).ifPresent(b -> resp.setBranchName(b.getBranchName()));
        leagueRepository.findById(link.getLeagueId()).ifPresent(l -> resp.setLeagueName(l.getLeagueName()));
        if (link.getDivisionId() != null) {
            divisionRepository.findById(link.getDivisionId()).ifPresent(d -> resp.setDivisionName(d.getDivisionName()));
        }
        return resp;
    }
}