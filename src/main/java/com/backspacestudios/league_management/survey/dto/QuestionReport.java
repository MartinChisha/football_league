package com.backspacestudios.league_management.survey.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class QuestionReport {
    private String questionText;
    private String questionType;
    private Map<String, Integer> optionCounts;
    private BigDecimal averageLikert;
    private List<String> freeTextAnswers;
}