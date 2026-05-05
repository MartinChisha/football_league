package com.backspacestudios.league_management.survey.service;

import com.backspacestudios.league_management.survey.dto.*;
import com.backspacestudios.league_management.survey.entity.*;
import com.backspacestudios.league_management.survey.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final SurveyResponseRepository responseRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public void submitResponses(SubmissionRequest request) {
        SurveyResponse response = new SurveyResponse();
        response.setSurveyId(request.getSurveyId());
        response.setRespondentType(request.getRespondentType());
        response.setRespondentEmail(request.getRespondentEmail());
        SurveyResponse savedResponse = responseRepository.save(response);

        for (AnswerRequest ansReq : request.getAnswers()) {
            Answer answer = new Answer();
            answer.setResponseId(savedResponse.getResponseId());
            answer.setQuestionId(ansReq.getQuestionId());
            answer.setSelectedOptionId(ansReq.getOptionId());
            answer.setLikertValue(ansReq.getLikertValue());
            answer.setAnswerText(ansReq.getTextAnswer());
            answerRepository.save(answer);
        }
    }

    @Transactional(readOnly = true)
    public SurveyReport getReport(UUID surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        List<Question> questions = questionRepository.findBySurveyIdOrderBySortOrderAsc(surveyId);
        long totalResponses = responseRepository.countBySurveyId(surveyId);
        List<SurveyResponse> responses = responseRepository.findBySurveyId(surveyId);

        List<QuestionReport> questionReports = new ArrayList<>();

        for (Question q : questions) {
            QuestionReport qr = new QuestionReport();
            qr.setQuestionText(q.getQuestionText());
            qr.setQuestionType(q.getQuestionType());

            List<Answer> answersForQuestion = new ArrayList<>();
            for (SurveyResponse r : responses) {
                List<Answer> ans = answerRepository.findByResponseId(r.getResponseId());
                ans.stream()
                   .filter(a -> a.getQuestionId().equals(q.getQuestionId()))
                   .forEach(answersForQuestion::add);
            }

            if ("likert".equals(q.getQuestionType())) {
                double sum = answersForQuestion.stream()
                        .filter(a -> a.getLikertValue() != null)
                        .mapToInt(Answer::getLikertValue)
                        .sum();
                long count = answersForQuestion.stream().filter(a -> a.getLikertValue() != null).count();
                if (count > 0) {
                    BigDecimal avg = BigDecimal.valueOf(sum / count).setScale(2, RoundingMode.HALF_UP);
                    qr.setAverageLikert(avg);
                }
                Map<String, Integer> dist = new LinkedHashMap<>();
                for (int i = 1; i <= 5; i++) {
                    int finalI = i;
                    long c = answersForQuestion.stream().filter(a -> a.getLikertValue() != null && a.getLikertValue() == finalI).count();
                    dist.put(String.valueOf(i), (int) c);
                }
                qr.setOptionCounts(dist);
            }
            else if ("radio".equals(q.getQuestionType()) || "select".equals(q.getQuestionType())) {
                List<Option> options = optionRepository.findByQuestionId(q.getQuestionId());
                Map<String, Integer> counts = new LinkedHashMap<>();
                for (Option opt : options) {
                    long c = answersForQuestion.stream()
                            .filter(a -> a.getSelectedOptionId() != null && a.getSelectedOptionId().equals(opt.getOptionId()))
                            .count();
                    counts.put(opt.getOptionText(), (int) c);
                }
                qr.setOptionCounts(counts);
            }
            else if ("checkbox".equals(q.getQuestionType())) {
                List<Option> options = optionRepository.findByQuestionId(q.getQuestionId());
                Map<String, Integer> counts = new LinkedHashMap<>();
                for (Option opt : options) {
                    long c = answersForQuestion.stream()
                            .filter(a -> a.getSelectedOptionId() != null && a.getSelectedOptionId().equals(opt.getOptionId()))
                            .count();
                    counts.put(opt.getOptionText(), (int) c);
                }
                qr.setOptionCounts(counts);
            }
            else if ("text".equals(q.getQuestionType())) {
                List<String> texts = answersForQuestion.stream()
                        .map(Answer::getAnswerText)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                qr.setFreeTextAnswers(texts);
            }

            questionReports.add(qr);
        }

        SurveyReport report = new SurveyReport();
        report.setSurveyId(surveyId);
        report.setSurveyTitle(survey.getTitle());
        report.setTotalResponses((int) totalResponses);
        report.setQuestions(questionReports);
        return report;
    }
}