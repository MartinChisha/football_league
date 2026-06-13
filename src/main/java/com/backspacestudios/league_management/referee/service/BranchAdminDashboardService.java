package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.referee.dto.BranchAdminInfo;
import com.backspacestudios.league_management.referee.dto.BranchRefereeDTO;
import com.backspacestudios.league_management.referee.dto.PendingRequestDTO;
import com.backspacestudios.league_management.referee.entity.BranchAdminAssignment;
import com.backspacestudios.league_management.referee.entity.Referee;
import com.backspacestudios.league_management.referee.entity.RefereeBranch;
import com.backspacestudios.league_management.referee.entity.RefereeBranchMembership;
import com.backspacestudios.league_management.referee.entity.RefereeRegistrationRequest;
import com.backspacestudios.league_management.referee.enums.MembershipStatus;
import com.backspacestudios.league_management.referee.enums.RequestStatus;
import com.backspacestudios.league_management.referee.repository.BranchAdminAssignmentRepository;
import com.backspacestudios.league_management.referee.repository.RefereeBranchMembershipRepository;
import com.backspacestudios.league_management.referee.repository.RefereeBranchRepository;
import com.backspacestudios.league_management.referee.repository.RefereeRegistrationRequestRepository;
import com.backspacestudios.league_management.referee.repository.RefereeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchAdminDashboardService {

    private final BranchAdminAssignmentRepository assignmentRepository;
    private final RefereeBranchRepository branchRepository;
    private final RefereeBranchMembershipRepository membershipRepository;
    private final RefereeRepository refereeRepository;
    private final RefereeRegistrationRequestRepository requestRepository;
    private final UserRepository userRepository;

    /**
     * Returns info about the branch that the given user administers.
     */
    @Transactional(readOnly = true)
    public BranchAdminInfo getBranchAdminInfo(UUID userId) {
        BranchAdminAssignment assignment = assignmentRepository.findByUserId(userId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Not a branch admin"));

        RefereeBranch branch = branchRepository.findById(assignment.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        BranchAdminInfo info = new BranchAdminInfo();
        info.setBranchId(branch.getBranchId());
        info.setBranchName(branch.getBranchName());
        info.setBranchCode(branch.getBranchCode());
        info.setDistrict(branch.getDistrict());
        info.setProfessionalLevel(branch.getProfessionalLevel());
        info.setContactEmail(branch.getContactEmail());
        info.setContactPhone(branch.getContactPhone());
        return info;
    }

    /**
     * Returns the branch ID for a branch admin user.
     */
    @Transactional(readOnly = true)
    public UUID getBranchIdByUserId(UUID userId) {
        BranchAdminAssignment assignment = assignmentRepository.findByUserId(userId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Not a branch admin"));
        return assignment.getBranchId();
    }

    /**
     * Returns all active referees for a given branch with user details.
     */
    @Transactional(readOnly = true)
    public List<BranchRefereeDTO> getReferees(UUID branchId) {
        List<RefereeBranchMembership> memberships = membershipRepository.findByBranchId(branchId)
                .stream()
                .filter(m -> m.getStatus() == MembershipStatus.active)
                .collect(Collectors.toList());

        return memberships.stream()
                .map(m -> {
                    Referee referee = refereeRepository.findById(m.getRefereeId()).orElse(null);
                    if (referee == null) return null;

                    User user = userRepository.findById(referee.getUserId()).orElse(null);
                    if (user == null) return null;

                    BranchRefereeDTO dto = new BranchRefereeDTO();
                    dto.setRefereeId(referee.getRefereeId());
                    dto.setUserId(referee.getUserId());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setEmail(user.getEmail());
                    dto.setRefereeCode(referee.getRefereeCode());
                    dto.setCurrentClass(referee.getCurrentClass());
                    dto.setMembershipStatus(m.getStatus().name());
                    return dto;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Returns all pending referee registration requests for a given branch.
     */
    @Transactional(readOnly = true)
    public List<PendingRequestDTO> getPendingRequests(UUID branchId) {
        List<RefereeRegistrationRequest> requests = requestRepository.findByBranchIdAndStatus(branchId, RequestStatus.pending);

        return requests.stream()
                .map(req -> {
                    User user = userRepository.findById(req.getUserId()).orElse(null);

                    PendingRequestDTO dto = new PendingRequestDTO();
                    dto.setRequestId(req.getRequestId());
                    dto.setUserId(req.getUserId());
                    if (user != null) {
                        dto.setUserFirstName(user.getFirstName());
                        dto.setUserLastName(user.getLastName());
                        dto.setUserEmail(user.getEmail());
                    }
                    dto.setBranchId(req.getBranchId());
                    dto.setRequestedClass(req.getRequestedClass());
                    dto.setCreatedAt(req.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}