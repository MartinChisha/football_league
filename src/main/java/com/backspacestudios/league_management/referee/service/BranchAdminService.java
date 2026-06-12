package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentRequest;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentResponse;
import com.backspacestudios.league_management.referee.entity.BranchAdminAssignment;
import com.backspacestudios.league_management.referee.repository.BranchAdminAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BranchAdminService {

   private final BranchAdminAssignmentRepository adminAssignmentRepository;
    private final UserRepository userRepository;

   BranchAdminService(BranchAdminAssignmentRepository adminAssignmentRepository, UserRepository userRepository) {
      this.adminAssignmentRepository = adminAssignmentRepository;
      this.userRepository = userRepository;
   }

    // Only super admin can assign branch admin
   @Transactional
public void assignBranchAdmin(UUID assignedByUserId, BranchAdminAssignmentRequest request) {
    // Verify the assignedBy user is super admin
    User assignedBy = userRepository.findById(assignedByUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    if (assignedBy.getRole() != UserRole.super_admin) {
        throw new RuntimeException("Only super admin can assign branch admin");
    }

    // Verify the target user exists and has the REFEREE role
    User targetUser = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    if (targetUser.getRole() != UserRole.referee) {
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

    public boolean isBranchAdmin(UUID userId, UUID branchId) {
        return adminAssignmentRepository.existsByUserIdAndBranchId(userId, branchId);
    }

    public List<BranchAdminAssignmentResponse> getBranchAdmins(UUID branchId) {
    List<BranchAdminAssignment> assignments = adminAssignmentRepository.findByBranchId(branchId);
    List<BranchAdminAssignmentResponse> result = new ArrayList<>();
    for (BranchAdminAssignment a : assignments) {
        User user = userRepository.findById(a.getUserId()).orElse(null);
        if (user == null) continue;
        BranchAdminAssignmentResponse dto = new BranchAdminAssignmentResponse();
        dto.setAssignmentId(a.getAssignmentId());
        dto.setUserId(a.getUserId());
        dto.setUserFirstName(user.getFirstName());
        dto.setUserLastName(user.getLastName());
        dto.setUserEmail(user.getEmail());
        dto.setBranchId(a.getBranchId());
        dto.setAssignedBy(a.getAssignedBy());
        dto.setAssignedAt(a.getAssignedAt());
        result.add(dto);
    }
    return result;
}
}