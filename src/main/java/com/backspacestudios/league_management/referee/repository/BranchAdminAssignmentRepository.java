package com.backspacestudios.league_management.referee.repository;

import com.backspacestudios.league_management.referee.entity.BranchAdminAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchAdminAssignmentRepository extends JpaRepository<BranchAdminAssignment, UUID> {
    Optional<BranchAdminAssignment> findByUserIdAndBranchId(UUID userId, UUID branchId);
    List<BranchAdminAssignment> findByUserId(UUID userId);
    List<BranchAdminAssignment> findByBranchId(UUID branchId);
    boolean existsByUserIdAndBranchId(UUID userId, UUID branchId);
    void deleteByUserIdAndBranchId(UUID userId, UUID branchId);
}