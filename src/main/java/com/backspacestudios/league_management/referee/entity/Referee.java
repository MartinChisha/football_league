package com.backspacestudios.league_management.referee.entity;

import com.backspacestudios.league_management.referee.enums.RefereeClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "referees", schema = "referee")
@Data
@NoArgsConstructor
public class Referee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "referee_id", columnDefinition = "UUID")
    private UUID refereeId;

    @Column(name = "user_id", unique = true, nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "referee_code", unique = true, nullable = false, length = 50)
    private String refereeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_class", nullable = false)
    private RefereeClass currentClass;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "nationality", length = 3)
    private String nationality;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}