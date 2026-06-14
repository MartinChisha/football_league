package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentRequest;
import com.backspacestudios.league_management.referee.dto.BranchAdminAssignmentResponse;
import com.backspacestudios.league_management.referee.entity.BranchAdminAssignment;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.enums.RefereeClass;
import com.backspacestudios.league_management.referee.repository.BranchAdminAssignmentRepository;
import com.backspacestudios.league_management.referee.repository.RefereeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing branch admin assignments.
 * <p>
 * Only super admins can assign or remove branch admins.
 * Assigning a branch admin automatically creates a Referee entity if the user
 * doesn't already have one, so the user can access their referee profile and
 * branch admin dashboard without additional steps.
 */
@Service
@RequiredArgsConstructor   // Generates constructor for all final fields
public class BranchAdminService {

    private final BranchAdminAssignmentRepository adminAssignmentRepository;
    private final UserRepository userRepository;
    private final RefereeRepository refereeRepository;   // NEW dependency

    /**
     * Assigns a user as a branch admin.
     *
     * @param assignedByUserId  the UUID of the super admin making the assignment
     * @param request           the assignment details (userId, branchId)
     * @throws RuntimeException if the target user is not a referee or is already an admin for this branch
     */
    @Transactional
    public void assignBranchAdmin(UUID assignedByUserId, BranchAdminAssignmentRequest request) {
        // Verify the caller is a super admin
        User assignedBy = userRepository.findById(assignedByUserId)
                .orElseThrow(() -> new RuntimeException("Assigning user not found"));
        if (assignedBy.getRole() != UserRole.super_admin) {
            throw new RuntimeException("Only super admin can assign branch admin");
        }

        // Verify the target user exists and has the REFEREE role
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Target user not found"));
        if (targetUser.getRole() != UserRole.referee) {
            throw new RuntimeException("User is not a referee");
        }

        refereeRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    Referee newRef = new Referee();
                    newRef.setUserId(request.getUserId());
                    newRef.setRefereeCode("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    newRef.setCurrentClass(RefereeClass.D);   // default class for direct assignments
                    newRef.setDateOfBirth(targetUser.getDateOfBirth());
                    return refereeRepository.save(newRef);
                });

        // Prevent duplicate assignments for the same branch
        if (adminAssignmentRepository.existsByUserIdAndBranchId(request.getUserId(), request.getBranchId())) {
            throw new RuntimeException("User is already an admin for this branch");
        }

        // Create the assignment
        BranchAdminAssignment assignment = new BranchAdminAssignment();
        assignment.setUserId(request.getUserId());
        assignment.setBranchId(request.getBranchId());
        assignment.setAssignedBy(assignedByUserId);
        adminAssignmentRepository.save(assignment);
    }

    /**
     * Removes a branch admin assignment.
     *
     * @param removedByUserId   the UUID of the super admin performing the removal
     * @param userId            the target user to remove
     * @param branchId          the branch from which to remove the admin
     * @throws RuntimeException if the caller is not a super admin or the assignment doesn't exist
     */
    @Transactional
    public void removeBranchAdmin(UUID removedByUserId, UUID userId, UUID branchId) {
        User removedBy = userRepository.findById(removedByUserId)
                .orElseThrow(() -> new RuntimeException("Removing user not found"));
        if (removedBy.getRole() != UserRole.super_admin) {
            throw new RuntimeException("Only super admin can remove branch admin");
        }
        if (!adminAssignmentRepository.existsByUserIdAndBranchId(userId, branchId)) {
            throw new RuntimeException("User is not an admin for this branch");
        }
        adminAssignmentRepository.deleteByUserIdAndBranchId(userId, branchId);
    }

    /**
     * Checks whether a user is a branch admin for the given branch.
     *
     * @param userId   the user's UUID
     * @param branchId the branch's UUID
     * @return true if the user is an admin of that branch
     */
    public boolean isBranchAdmin(UUID userId, UUID branchId) {
        return adminAssignmentRepository.existsByUserIdAndBranchId(userId, branchId);
    }

    /**
     * Retrieves all branch admins for a given branch, including user details.
     *
     * @param branchId the branch's UUID
     * @return a list of {@link BranchAdminAssignmentResponse} containing admin info
     */
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