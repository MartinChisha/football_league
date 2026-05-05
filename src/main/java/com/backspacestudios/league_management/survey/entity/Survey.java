package com.backspacestudios.league_management.survey.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "surveys", schema = "survey")
@Data
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID surveyId;

    private String title;
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean active = true;
}