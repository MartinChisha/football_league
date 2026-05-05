package com.backspacestudios.league_management.referee.entity;

import com.backspacestudios.league_management.referee.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "referee_branch_memberships", schema = "referee")
@Data
@NoArgsConstructor
public class RefereeBranchMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "membership_id", columnDefinition = "UUID")
    private UUID membershipId;

    @Column(name = "referee_id", nullable = false, columnDefinition = "UUID")
    private UUID refereeId;

    @Column(name = "branch_id", nullable = false, columnDefinition = "UUID")
    private UUID branchId;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status = MembershipStatus.active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}