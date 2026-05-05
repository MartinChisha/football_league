package com.backspacestudios.league_management.referee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "branch_admin_assignments", schema = "referee")
@Data
@NoArgsConstructor
public class BranchAdminAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id", columnDefinition = "UUID")
    private UUID assignmentId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "branch_id", nullable = false, columnDefinition = "UUID")
    private UUID branchId;

    @Column(name = "assigned_by", columnDefinition = "UUID")
    private UUID assignedBy;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;
}