package com.backspacestudios.league_management.referee.repository;

import com.backspacestudios.league_management.referee.entity.RefereeBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefereeBranchRepository extends JpaRepository<RefereeBranch, UUID> {
    Optional<RefereeBranch> findByBranchCode(String branchCode);
    boolean existsByBranchCode(String branchCode);
}