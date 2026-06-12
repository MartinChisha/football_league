package com.backspacestudios.league_management.referee.service;

import com.backspacestudios.league_management.referee.dto.RefereeBranchRequest;
import com.backspacestudios.league_management.referee.dto.RefereeBranchResponse;
import com.backspacestudios.league_management.referee.entity.RefereeBranch;
import com.backspacestudios.league_management.referee.repository.RefereeBranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefereeBranchService {
    private final RefereeBranchRepository branchRepository;

    RefereeBranchService(RefereeBranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public RefereeBranchResponse createBranch(RefereeBranchRequest request) {
        if (branchRepository.existsByBranchCode(request.getBranchCode())) {
            throw new RuntimeException("Branch code already exists");
        }
        RefereeBranch branch = new RefereeBranch();
        branch.setBranchName(request.getBranchName());
        branch.setBranchCode(request.getBranchCode());
        branch.setDistrict(request.getDistrict());
        branch.setProfessionalLevel(request.getProfessionalLevel());
        branch.setMotherBodyId(request.getMotherBodyId());
        branch.setContactEmail(request.getContactEmail());
        branch.setContactPhone(request.getContactPhone());
        branch.setStatus("active");
        RefereeBranch saved = branchRepository.save(branch);
        return mapToResponse(saved);
    }

    public RefereeBranchResponse updateBranch(UUID branchId, RefereeBranchRequest request) {
        RefereeBranch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        branch.setBranchName(request.getBranchName());
        branch.setDistrict(request.getDistrict());
        branch.setProfessionalLevel(request.getProfessionalLevel());
        branch.setMotherBodyId(request.getMotherBodyId());
        branch.setContactEmail(request.getContactEmail());
        branch.setContactPhone(request.getContactPhone());
        // branchCode cannot be changed because it is unique identifier
        return mapToResponse(branchRepository.save(branch));
    }

    public void deleteBranch(UUID branchId) {
        branchRepository.deleteById(branchId);
    }

    public RefereeBranchResponse getBranch(UUID branchId) {
        RefereeBranch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        return mapToResponse(branch);
    }

    public List<RefereeBranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RefereeBranchResponse mapToResponse(RefereeBranch branch) {
        RefereeBranchResponse response = new RefereeBranchResponse();
        response.setBranchId(branch.getBranchId());
        response.setBranchName(branch.getBranchName());
        response.setBranchCode(branch.getBranchCode());
        response.setDistrict(branch.getDistrict());
        response.setProfessionalLevel(branch.getProfessionalLevel());
        response.setMotherBodyId(branch.getMotherBodyId());
        response.setContactEmail(branch.getContactEmail());
        response.setContactPhone(branch.getContactPhone());
        response.setStatus(branch.getStatus());
        response.setCreatedAt(branch.getCreatedAt());
        response.setUpdatedAt(branch.getUpdatedAt());
        return response;
    }
}