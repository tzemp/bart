package com.github.tzemp.parser;

import com.github.tzemp.stackoverflow.StackExchangeAnswer;
import com.github.tzemp.stackoverflow.StackExchangeQuestion;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by timothyzemp on 07.08.17.
 */
public class StackExchangeQuestionRater {

    private final double CONTEXT_WEIGHT = 0.5;
    private final double VOTING_WEIGHT = 0.1;
    private final double GOAL_WEIGHT = 0.2;

    private List<StackExchangeQuestion> questions;
    private List<StackExchangeQuestionEvaluation> evaluations;
    private BuildSection buildSection;
    private Map<Long, Integer> rating;

    public StackExchangeQuestionRater(List<StackExchangeQuestion> questions, BuildSection buildSection) {
        this.questions = questions;
        this.buildSection = buildSection;
        this.rating = new HashMap<>();
        this.evaluations = new ArrayList<>();
    }

    public void rate() {
        for (StackExchangeQuestion question : this.getQuestions()) {
            StackExchangeQuestionEvaluation evaluation = new StackExchangeQuestionEvaluation(question);
            this.evaluateContext(question, evaluation);
            //this.evaluateStackOverflowVoteIndex(question, evaluation);
            //this.evaluateStackOverflowScore(question, evaluation);
            this.getEvaluations().add(evaluation);
        }

        //OptionalInt minKeywordHit = this.getEvaluations().stream().mapToInt(StackExchangeQuestionEvaluation::getKeywordHits).min();
        OptionalInt maxKeywordHit = this.getEvaluations().stream().mapToInt(StackExchangeQuestionEvaluation::getKeywordHits).max();
        //OptionalInt maxStackExchangeScore = this.getEvaluations().stream().mapToInt(StackExchangeQuestionEvaluation::getStackExchangeScore).max();
        //OptionalDouble maxVoting = this.getEvaluations().stream().mapToDouble(StackExchangeQuestionEvaluation::getStackExchangeVoteIndex).max();

        for (StackExchangeQuestionEvaluation evaluation : this.getEvaluations()) {
            // Evaluate Keyword Hits
            evaluation.setContextRating((double) evaluation.getKeywordHits() / (double) maxKeywordHit.orElse(1));
            // Evaluate Score
            //evaluation.setScoreRating(evaluation.getStackExchangeScore()/maxStackExchangeScore.orElse(1));
            // Evaluate Votes
            //evaluation.setVoteRating(evaluation.getStackExchangeVoteIndex()/maxVoting.orElse(1));
        }
    }

    private void evaluateStackOverflowScore(StackExchangeQuestion question, StackExchangeQuestionEvaluation evaluation) {
        evaluation.setStackExchangeScore((int) question.getScore());
    }

    private void evaluateStackOverflowVoteIndex(StackExchangeQuestion question, StackExchangeQuestionEvaluation evaluation) {
        if ((question.getUpVoteCount() + question.getDownVoteCount()) == 0) {
            evaluation.setStackExchangeVoteIndex(0);
        } else {
            double voteIndex = question.getUpVoteCount() / (question.getUpVoteCount() + question.getDownVoteCount());
            evaluation.setStackExchangeVoteIndex(voteIndex);
        }
    }

    private List<StackExchangeQuestion> getQuestions() {
        return this.questions;
    }

    public Map<Long, Integer> getRating() {
        return this.rating;
    }

    private BuildSection getBuildSection() {
        return this.buildSection;
    }

    private void evaluateContext(StackExchangeQuestion question, StackExchangeQuestionEvaluation evaluation) {

        Map<String, Integer> countedKeywords = this.countKeywords(question);


        evaluation.setKeywordHits((int) countedKeywords.values().stream().filter(s -> s >= 1).count());


    }


    public Set<String> getKeyWords() {
        Set<String> keywords = new HashSet<>();
        /*
        List<String> filteredList = Parser.config.filterListByStopWords(this.getBuildSection().getAdditionalErrorInformation().keySet());
        filteredList.add(this.getBuildSection().getErrorCause());
        for (String string : filteredList) {
            String[] words = string.split(" ");
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].replaceAll("[^a-zA-Z]+", "");
                if (words[i].length() > 0) {
                    keywords.add(words[i]);
                }
            }
        }*/
        String[] words = this.getBuildSection().getErrorCause().split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^a-zA-Z]+", "");
            if (words[i].length() > 0) {
                keywords.add(words[i]);
            }
        }
        return keywords;
    }

    public Map<String, Integer> countKeywords(StackExchangeQuestion question) {
        Map<String, Integer> keywordsCount = new HashMap<String, Integer>();

        for (String keyword : this.getKeyWords()) {

            Integer n = keywordsCount.get(keyword);
            n = (n == null) ? Parser.config.countWordInText(keyword, question.getBody()) : Parser.config.countWordInText(keyword, question.getBody()) + n;
            keywordsCount.put(keyword, n);

        }

        for (StackExchangeAnswer answer : question.getAnswers()) {
            for (String keyword : this.getKeyWords()) {

                Integer n = keywordsCount.get(keyword);
                n = (n == null) ? Parser.config.countWordInText(keyword, answer.getBody()) : Parser.config.countWordInText(keyword, answer.getBody()) + n;
                keywordsCount.put(keyword, n);

            }
        }
        return keywordsCount;
    }

    private Map<String, Integer> countPhrases(StackExchangeQuestion question) {
        Set<String> filteredList = this.getBuildSection().getAdditionalErrorInformation().keySet();

        Map<String, Integer> phraseCount = new HashMap<String, Integer>();

        for (String phrase : filteredList) {

            Integer n = phraseCount.get(phrase);
            n = (n == null) ? Parser.config.countWordInText(phrase, question.getBody()) : Parser.config.countWordInText(phrase, question.getBody()) + n;
            phraseCount.put(phrase, n);

        }

        for (StackExchangeAnswer answer : question.getAnswers()) {
            for (String phrase : filteredList) {

                Integer n = phraseCount.get(phrase);
                n = (n == null) ? Parser.config.countWordInText(phrase, answer.getBody()) : Parser.config.countWordInText(phrase, answer.getBody()) + n;
                phraseCount.put(phrase, n);

            }
        }
        return phraseCount;
    }


    public List<StackExchangeQuestionEvaluation> getEvaluations() {
        return evaluations;
    }

    public List<StackExchangeQuestionEvaluation> getQuestionsWithHigestRating(int i) {
        int size = this.getEvaluations().size() < i ? this.getEvaluations().size() : i;
        return this.getEvaluations().stream().sorted(Comparator.comparing(StackExchangeQuestionEvaluation::getContextRating, Double::compareTo).reversed()).collect(Collectors.toList()).subList(0, size);
    }
}
