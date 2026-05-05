package com.backspacestudios.league_management.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.SqlTypes;

import com.backspacestudios.league_management.core.enums.SystemAccessLevel;

import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "super_admins", schema = "core")
@Data
@NoArgsConstructor
public class SuperAdmin {

    @Id
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "system_permissions", columnDefinition = "JSONB")
    private Map<String, Object> systemPermissions;

    @Column(name = "can_impersonate")
    private Boolean canImpersonate = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_access_level", columnDefinition = "VARCHAR(20) DEFAULT 'full'")
    private SystemAccessLevel systemAccessLevel = SystemAccessLevel.full;

    @Column(name = "emergency_contact", length = 255)
    private String emergencyContact;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;
}