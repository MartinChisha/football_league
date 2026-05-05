package com.backspacestudios.league_management.survey.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "answers", schema = "survey")
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID answerId;

    @Column(name = "response_id")
    private UUID responseId;

    @Column(name = "question_id")
    private UUID questionId;

    private String answerText;
    private UUID selectedOptionId;
    private Integer likertValue;
}