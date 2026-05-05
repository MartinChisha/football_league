package com.backspacestudios.league_management.survey.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "options", schema = "survey")
@Data
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID optionId;

    @Column(name = "question_id")
    private UUID questionId;

    private String optionText;
    private Integer optionValue; // for Likert numeric value
}