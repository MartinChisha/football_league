package com.backspacestudios.league_management.league.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.league.enums.FinancialAccessLevel;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "league_admins", schema = "league")
@Data
@NoArgsConstructor
public class LeagueAdmin {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "league_id", nullable = false, columnDefinition = "UUID")
    private UUID leagueId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "admin_permissions", columnDefinition = "JSONB")
    private Map<String, Object> adminPermissions;

    @Column(name = "can_manage_referees")
    private Boolean canManageReferees = true;

    @Column(name = "can_manage_teams")
    private Boolean canManageTeams = true;

    @Column(name = "can_schedule_fixtures")
    private Boolean canScheduleFixtures = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_access_level", columnDefinition = "VARCHAR(10) DEFAULT 'none'")
    private FinancialAccessLevel financialAccessLevel = FinancialAccessLevel.none;

    @Column(name = "assigned_by", columnDefinition = "UUID")
    private UUID assignedBy;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;
}
