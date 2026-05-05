package com.backspacestudios.league_management.referee.entity;

import com.backspacestudios.league_management.referee.enums.ProfessionalLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "referee_branches", schema = "referee")
@Data
@NoArgsConstructor
public class RefereeBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "branch_id", columnDefinition = "UUID")
    private UUID branchId;

    @Column(name = "branch_name", nullable = false, length = 255)
    private String branchName;

    @Column(name = "branch_code", unique = true, nullable = false, length = 50)
    private String branchCode;

    @Column(name = "district", length = 100)
    private String district;

    @Enumerated(EnumType.STRING)
    @Column(name = "professional_level")
    private ProfessionalLevel professionalLevel;

    @Column(name = "mother_body_id") // UUID reference to future mother_body table
    private UUID motherBodyId;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "status", length = 20)
    private String status = "active"; // active, inactive

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}