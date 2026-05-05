package com.backspacestudios.league_management.league.entity;

import com.backspacestudios.league_management.league.enums.LeagueStatus;
import com.backspacestudios.league_management.league.enums.LeagueStructure;
import com.backspacestudios.league_management.league.enums.LeagueType;

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
@Table(name = "leagues", schema = "league")
@Data
@NoArgsConstructor
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "league_id", columnDefinition = "UUID")
    private UUID leagueId;

    @Column(name = "league_name", nullable = false, length = 255)
    private String leagueName;

    @Column(name = "league_code", unique = true, nullable = false, length = 50)
    private String leagueCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(name = "region", length = 100)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "league_type", columnDefinition = "VARCHAR(20) DEFAULT 'amateur'")
    private LeagueType leagueType = LeagueType.amateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_structure", columnDefinition = "VARCHAR(20) DEFAULT 'flat'")
    private LeagueStructure overallStructure = LeagueStructure.flat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private LeagueStatus status = LeagueStatus.active;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "website", length = 500)
    private String website;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "global_config", columnDefinition = "JSONB")
    private Map<String, Object> globalConfig;

    @Column(name = "created_by", nullable = false, columnDefinition = "UUID")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}