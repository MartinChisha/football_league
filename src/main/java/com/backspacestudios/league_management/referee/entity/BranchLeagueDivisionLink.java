package com.backspacestudios.league_management.referee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "branch_league_division_links", schema = "referee",
       uniqueConstraints = @UniqueConstraint(columnNames = {"branch_id", "league_id", "division_id"}))
@Data
@NoArgsConstructor
public class BranchLeagueDivisionLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "link_id", columnDefinition = "UUID")
    private UUID linkId;

    @Column(name = "branch_id", nullable = false, columnDefinition = "UUID")
    private UUID branchId;

    @Column(name = "league_id", nullable = false, columnDefinition = "UUID")
    private UUID leagueId;

    @Column(name = "division_id", columnDefinition = "UUID")
    private UUID divisionId;  // null means whole league

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}