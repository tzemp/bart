package com.github.tzemp.parser;

import com.github.tzemp.stackoverflow.StackExchangeQuestion;

/**
 * Created by timothyzemp on 07.08.17.
 */
public class StackExchangeQuestionEvaluation {
    private StackExchangeQuestion question;
    private int contextScore;
    private int stackExchangeScore;
    private double stackExchangeVoteIndex;
    private int keywordHits;
    private int phraseHits;

    private double contextRating;
    private double scoreRating;
    private double voteRating;

    public StackExchangeQuestionEvaluation(StackExchangeQuestion question) {
        this.question = question;
        this.keywordHits = 0;
        this.phraseHits = 0;
    }

    public void setKeywordHits(int keywordHits) {
        this.keywordHits = keywordHits;
    }

    public void setPhraseHits(int phraseHits) {
        this.phraseHits = phraseHits;
    }

    public StackExchangeQuestion getQuestion() {
        return question;
    }

    public void setQuestion(StackExchangeQuestion question) {
        this.question = question;
    }

    public int getContextScore() {
        return contextScore;
    }

    public void setContextScore(int contextScore) {
        this.contextScore = contextScore;
    }

    public int getStackExchangeScore() {
        return stackExchangeScore;
    }

    public void setStackExchangeScore(int stackExchangeScore) {
        this.stackExchangeScore = stackExchangeScore;
    }

    public int getKeywordHits() {
        return keywordHits;
    }

    public int getPhraseHits() {
        return phraseHits;
    }

    public double getStackExchangeVoteIndex() {
        return stackExchangeVoteIndex;
    }

    public void setStackExchangeVoteIndex(double stackExchangeVoteIndex) {
        this.stackExchangeVoteIndex = stackExchangeVoteIndex;
    }

    public double getContextRating() {
        return contextRating;
    }

    public void setContextRating(double contextRating) {
        this.contextRating = contextRating;
    }

    public double getScoreRating() {
        return scoreRating;
    }

    public void setScoreRating(double scoreRating) {
        this.scoreRating = scoreRating;
    }

    public double getVoteRating() {
        return voteRating;
    }

    public void setVoteRating(double voteRating) {
        this.voteRating = voteRating;
    }
}
