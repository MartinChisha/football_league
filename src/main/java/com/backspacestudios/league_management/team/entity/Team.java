package com.backspacestudios.league_management.team.entity;

import com.backspacestudios.league_management.team.enums.FinancialStatus;
import com.backspacestudios.league_management.team.enums.TeamStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "teams", schema = "team")
@Data
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "team_id", columnDefinition = "UUID")
    private UUID teamId;

    @Column(name = "league_id", nullable = false, columnDefinition = "UUID")
    private UUID leagueId;

    @Column(name = "division_id", columnDefinition = "UUID")
    private UUID divisionId;

    @Column(name = "team_name", nullable = false, length = 255)
    private String teamName;

    @Column(name = "team_code", nullable = false, length = 50)
    private String teamCode;

    @Column(name = "short_name", length = 100)
    private String shortName;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "home_city", length = 100)
    private String homeCity;

    @Column(name = "home_stadium", length = 255)
    private String homeStadium;

    @Column(name = "stadium_capacity")
    private Integer stadiumCapacity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "club_colors", columnDefinition = "JSONB")
    private Map<String, Object> clubColors;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "website", length = 500)
    private String website;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private TeamStatus status = TeamStatus.active;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status", columnDefinition = "VARCHAR(10) DEFAULT 'solvent'")
    private FinancialStatus financialStatus = FinancialStatus.solvent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "JSONB")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}