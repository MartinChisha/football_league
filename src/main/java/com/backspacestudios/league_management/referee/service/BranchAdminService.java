package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentRequest;
import com.backspacestudios.league_management.referee.entity.BranchAdminAssignment;
import com.backspacestudios.league_management.referee.repository.BranchAdminAssignmentRepository;
import com.backspacestudios.league_management.referee.repository.RefereeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BranchAdminService {

    @Autowired
    private BranchAdminAssignmentRepository adminAssignmentRepository;

    @Autowired
    private RefereeRepository refereeRepository;

    @Autowired
    private UserRepository userRepository;

    // Only super admin can assign branch admin
    @Transactional
    public void assignBranchAdmin(UUID assignedByUserId, BranchAdminAssignmentRequest request) {
        // Verify the assignedBy user is super admin
        User assignedBy = userRepository.findById(assignedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (assignedBy.getRole() != UserRole.super_admin) {
            throw new RuntimeException("Only super admin can assign branch admin");
        }

        // Verify the target user is a referee
        if (!refereeRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("User is not a referee");
        }

        // Check if already assigned
        if (adminAssignmentRepository.existsByUserIdAndBranchId(request.getUserId(), request.getBranchId())) {
            throw new RuntimeException("User is already an admin for this branch");
        }

        BranchAdminAssignment assignment = new BranchAdminAssignment();
        assignment.setUserId(request.getUserId());
        assignment.setBranchId(request.getBranchId());
        assignment.setAssignedBy(assignedByUserId);
        adminAssignmentRepository.save(assignment);
    }

    @Transactional
    public void removeBranchAdmin(UUID removedByUserId, UUID userId, UUID branchId) {
        User removedBy = userRepository.findById(removedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (removedBy.getRole() != UserRole.super_admin) {
            throw new RuntimeException("Only super admin can remove branch admin");
        }
        if (!adminAssignmentRepository.existsByUserIdAndBranchId(userId, branchId)) {
            throw new RuntimeException("User is not an admin for this branch");
        }
        adminAssignmentRepository.deleteByUserIdAndBranchId(userId, branchId);
    }

    public List<BranchAdminAssignment> getBranchAdmins(UUID branchId) {
        return adminAssignmentRepository.findByBranchId(branchId);
    }

    public boolean isBranchAdmin(UUID userId, UUID branchId) {
        return adminAssignmentRepository.existsByUserIdAndBranchId(userId, branchId);
    }
}