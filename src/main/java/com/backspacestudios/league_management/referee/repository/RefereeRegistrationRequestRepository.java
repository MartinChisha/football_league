package com.backspacestudios.league_management.referee.repository;

import com.backspacestudios.league_management.referee.entity.RefereeRegistrationRequest;
import com.backspacestudios.league_management.referee.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefereeRegistrationRequestRepository extends JpaRepository<RefereeRegistrationRequest, UUID> {
    Optional<RefereeRegistrationRequest> findByUserIdAndBranchId(UUID userId, UUID branchId);
    List<RefereeRegistrationRequest> findByStatus(RequestStatus status);
    List<RefereeRegistrationRequest> findByBranchIdAndStatus(UUID branchId, RequestStatus status);
}