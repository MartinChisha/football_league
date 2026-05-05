package com.backspacestudios.league_management.team.entity;

import com.backspacestudios.league_management.team.enums.ManagerStatus;
import com.backspacestudios.league_management.team.enums.ManagerType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_managers", schema = "team")
@Data
@NoArgsConstructor
public class TeamManager {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "team_id", nullable = false, columnDefinition = "UUID")
    private UUID teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_type", columnDefinition = "team_manager_type DEFAULT 'head_manager'")
    private ManagerType managerType = ManagerType.head_manager;

    @Column(name = "can_manage_roster")
    private Boolean canManageRoster = true;

    @Column(name = "can_view_financials")
    private Boolean canViewFinancials = false;

    @Column(name = "can_communicate_league")
    private Boolean canCommunicateLeague = true;

    @Column(name = "contract_expiry_date")
    private LocalDate contractExpiryDate;

    @Column(name = "assigned_by", columnDefinition = "UUID")
    private UUID assignedBy;  // league admin who requested

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "team_manager_status DEFAULT 'pending'")
    private ManagerStatus status = ManagerStatus.pending;

    @Column(name = "approved_by", columnDefinition = "UUID")
    private UUID approvedBy;  // super admin who approved

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}