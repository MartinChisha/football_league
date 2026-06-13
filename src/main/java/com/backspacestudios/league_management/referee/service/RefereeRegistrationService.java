package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.enums.UserRole;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.referee.dto.RefereeMembershipResponse;
import com.backspacestudios.league_management.referee.dto.RefereeRegistrationApprovalDTO;
import com.backspacestudios.league_management.referee.dto.RefereeRegistrationRequestDTO;
import com.backspacestudios.league_management.referee.dto.RefereeResponse;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.entity.RefereeBranch;
import com.backspacestudios.league_management.referee.entity.RefereeBranchMembership;
import com.backspacestudios.league_management.referee.entity.RefereeRegistrationRequest;
import com.backspacestudios.league_management.referee.enums.MembershipStatus;
import com.backspacestudios.league_management.referee.enums.RequestStatus;
import com.backspacestudios.league_management.referee.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefereeRegistrationService {

    private final RefereeRegistrationRequestRepository requestRepository;
    private final RefereeRepository refereeRepository;
    private final RefereeBranchMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final BranchAdminAssignmentRepository branchAdminAssignmentRepository;
    private final RefereeBranchRepository branchRepository;

    // User submits a request to become a referee for a branch
    @Transactional
    public void submitRequest(UUID userId, RefereeRegistrationRequestDTO dto) {
        // Check if user already has a pending or approved request for this branch
        if (requestRepository.findByUserIdAndBranchId(userId, dto.getBranchId()).isPresent()) {
            throw new RuntimeException("A request already exists for this user and branch");
        }
        // Check if user is already a member of this branch
        Referee existingReferee = refereeRepository.findByUserId(userId).orElse(null);
        if (existingReferee != null) {
            if (membershipRepository.existsByRefereeIdAndBranchId(existingReferee.getRefereeId(), dto.getBranchId())) {
                throw new RuntimeException("User is already a member of this branch");
            }
        }
        // Validate branch exists
        if (!branchRepository.existsById(dto.getBranchId())) {
            throw new RuntimeException("Branch not found");
        }

        RefereeRegistrationRequest request = new RefereeRegistrationRequest();
        request.setUserId(userId);
        request.setBranchId(dto.getBranchId());
        request.setRequestedClass(dto.getRequestedClass());
        request.setStatus(RequestStatus.pending);
        requestRepository.save(request);
    }

    // Approve or reject a request (by super admin or branch admin)
    @Transactional
    public void processApproval(UUID approverUserId, RefereeRegistrationApprovalDTO approvalDto) {
        RefereeRegistrationRequest request = requestRepository.findById(approvalDto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Authorization: approver is super admin OR branch admin of request.branchId
        boolean isSuperAdmin = isSuperAdmin(approverUserId);
        boolean isBranchAdmin = isBranchAdmin(approverUserId, request.getBranchId());
        if (!isSuperAdmin && !isBranchAdmin) {
            throw new RuntimeException("User not authorized to approve this request");
        }

        if (request.getStatus() != RequestStatus.pending) {
            throw new RuntimeException("Request already processed");
        }

        if (approvalDto.isApproved()) {
            // Approve
            request.setStatus(RequestStatus.approved);
            request.setApprovedByUserId(approverUserId);
            request.setApprovedAt(LocalDateTime.now());

            // Create or update Referee entity
            Referee referee = refereeRepository.findByUserId(request.getUserId())
                    .orElseGet(() -> {
                        Referee newRef = new Referee();
                        newRef.setUserId(request.getUserId());
                        newRef.setRefereeCode(generateRefereeCode());
                        newRef.setCurrentClass(request.getRequestedClass());
                        // Set dateOfBirth and nationality from user if needed
                        User user = userRepository.findById(request.getUserId()).orElseThrow();
                        newRef.setDateOfBirth(user.getDateOfBirth());
                        newRef.setNationality(null); // can be set later
                        return refereeRepository.save(newRef);
                    });

            // Create membership
            RefereeBranchMembership membership = new RefereeBranchMembership();
            membership.setRefereeId(referee.getRefereeId());
            membership.setBranchId(request.getBranchId());
            membership.setJoinedDate(LocalDate.now());
            membership.setStatus(MembershipStatus.active);
            // Certificate URL generation (placeholder)
            membership.setCertificateUrl("/certificates/" + referee.getRefereeCode() + "_" + request.getBranchId() + ".pdf");
            membershipRepository.save(membership);

            // Update user role to REFEREE if not already
            User user = userRepository.findById(request.getUserId()).orElseThrow();
            if (user.getRole() != UserRole.referee) {
                user.setRole(UserRole.referee);
                userRepository.save(user);
            }
        } else {
            // Reject
            request.setStatus(RequestStatus.rejected);
            request.setRejectionReason(approvalDto.getRejectionReason());
            request.setApprovedByUserId(approverUserId);
            request.setApprovedAt(LocalDateTime.now());
        }
        requestRepository.save(request);
    }

    public RefereeResponse getRefereeByUserId(UUID userId) {
        Referee referee = refereeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Referee not found"));
        return mapToResponse(referee);
    }

    public RefereeRegistrationRequest getPendingRequestByUserId(UUID userId) {
        return requestRepository.findByUserIdAndStatus(userId, RequestStatus.pending)
                .orElseThrow(() -> new RuntimeException("No pending request found"));
    }
    public List<RefereeMembershipResponse> getMyMemberships(UUID userId) {
    Referee referee = refereeRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Referee not found"));
    List<RefereeBranchMembership> memberships = membershipRepository.findByRefereeId(referee.getRefereeId());
    return memberships.stream().map(m -> {
        RefereeBranch branch = branchRepository.findById(m.getBranchId()).orElse(null);
        RefereeMembershipResponse resp = new RefereeMembershipResponse();
        resp.setRefereeId(referee.getRefereeId());
        resp.setUserId(referee.getUserId());
        resp.setRefereeCode(referee.getRefereeCode());
        resp.setCurrentClass(referee.getCurrentClass());
        resp.setFirstName(null); // we can fill later from user
        resp.setLastName(null);
        resp.setEmail(null);
        resp.setDateOfBirth(referee.getDateOfBirth());
        resp.setNationality(referee.getNationality());
        resp.setBranchId(m.getBranchId());
        resp.setBranchName(branch != null ? branch.getBranchName() : null);
        resp.setBranchCode(branch != null ? branch.getBranchCode() : null);
        resp.setDistrict(branch != null ? branch.getDistrict() : null);
        resp.setProfessionalLevel(branch != null ? String.valueOf(branch.getProfessionalLevel()) : null);
        resp.setJoinedDate(m.getJoinedDate());
        resp.setMembershipStatus(m.getStatus().name());
        resp.setCertificateUrl(m.getCertificateUrl());
        return resp;
    }).collect(Collectors.toList());
}

    private RefereeResponse mapToResponse(Referee referee) {
        RefereeResponse response = new RefereeResponse();
        response.setRefereeId(referee.getRefereeId());
        response.setUserId(referee.getUserId());
        response.setRefereeCode(referee.getRefereeCode());
        response.setCurrentClass(referee.getCurrentClass());
        response.setDateOfBirth(referee.getDateOfBirth());
        response.setNationality(referee.getNationality());
        response.setCreatedAt(referee.getCreatedAt());
        return response;
    }

    private String generateRefereeCode() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isSuperAdmin(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getRole() == UserRole.super_admin;
    }

    private boolean isBranchAdmin(UUID userId, UUID branchId) {
        return branchAdminAssignmentRepository.existsByUserIdAndBranchId(userId, branchId);
    }
}