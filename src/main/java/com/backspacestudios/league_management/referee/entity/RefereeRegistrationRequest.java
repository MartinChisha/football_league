package com.backspacestudios.league_management.referee.entity;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import com.backspacestudios.league_management.referee.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "referee_registration_requests", schema = "referee")
@Data
@NoArgsConstructor
public class RefereeRegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id", columnDefinition = "UUID")
    private UUID requestId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "branch_id", nullable = false, columnDefinition = "UUID")
    private UUID branchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_class", nullable = false)
    private RefereeClass requestedClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.pending;

    @Column(name = "approved_by_user_id", columnDefinition = "UUID")
    private UUID approvedByUserId;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}