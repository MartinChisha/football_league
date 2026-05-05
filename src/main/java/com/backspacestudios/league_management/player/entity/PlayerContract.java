package com.backspacestudios.league_management.player.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.backspacestudios.league_management.player.enums.ContractStatus;
import com.backspacestudios.league_management.player.enums.ContractType;
import com.backspacestudios.league_management.player.enums.RegistrationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "player_contracts", schema = "player")
@Data
@NoArgsConstructor
public class PlayerContract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contract_id", columnDefinition = "UUID")
    private UUID contractId;

    @Column(name = "player_id", nullable = false, columnDefinition = "UUID")
    private UUID playerId;

    @Column(name = "team_id", nullable = false, columnDefinition = "UUID")
    private UUID teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", columnDefinition = "VARCHAR(20) DEFAULT 'amateur'")
    private ContractType contractType = ContractType.amateur;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "salary_amount", precision = 12, scale = 2)
    private BigDecimal salaryAmount;

    @Column(name = "salary_currency", length = 3)
    private String salaryCurrency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private ContractStatus contractStatus = ContractStatus.active;

    @Column(name = "is_loan")
    private Boolean isLoan = false;

    @Column(name = "loan_from_team_id", columnDefinition = "UUID")
    private UUID loanFromTeamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", columnDefinition = "VARCHAR(20) DEFAULT 'pending'")
    private RegistrationStatus registrationStatus = RegistrationStatus.pending;

    @Column(name = "squad_number")
    private Integer squadNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contract_terms", columnDefinition = "JSONB")
    private Map<String, Object> contractTerms;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;  // team manager who created the contract request

    @Column(name = "approved_by", columnDefinition = "UUID")
    private UUID approvedBy; // league admin who approved

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
