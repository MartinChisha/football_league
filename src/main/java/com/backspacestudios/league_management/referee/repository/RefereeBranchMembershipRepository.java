package com.backspacestudios.league_management.referee.repository;

import com.backspacestudios.league_management.referee.entity.RefereeBranchMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefereeBranchMembershipRepository extends JpaRepository<RefereeBranchMembership, UUID> {
    Optional<RefereeBranchMembership> findByRefereeIdAndBranchId(UUID refereeId, UUID branchId);
    List<RefereeBranchMembership> findByRefereeId(UUID refereeId);
    List<RefereeBranchMembership> findByBranchId(UUID branchId);
    List<RefereeBranchMembership> findByBranchIdInAndStatus(List<UUID> branchIds, String status);
    boolean existsByRefereeIdAndBranchId(UUID refereeId, UUID branchId);
}