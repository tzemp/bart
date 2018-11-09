package com.github.tzemp.reporting;

import com.github.tzemp.parser.ParserSummary;
import com.github.tzemp.parser.hints.Hint;
import com.github.tzemp.stackoverflow.StackExchangeAnswer;
import com.github.tzemp.stackoverflow.StackExchangeQuestion;

import java.util.List;

/*
 * Objet to report the result of the parsing and
 * analysis to the remote reporting tool
 */
public class Report {
    private String project;
    private String goal;
    private String errorCause;
    private Hint hint;
    private StackExchangeAnswer bestSolution;
    private StackExchangeQuestion bestQuestion;
    private List<String> log;

    public Report (String project, ParserSummary parserSummary, List<String> log) {
        this.project = project;
        this.goal = parserSummary.getFailedGoal();
        this.errorCause = parserSummary.getErrorCause();
        this.hint = parserSummary.getHint();
        this.bestQuestion = parserSummary.getBestQuestion();
        if (this.bestQuestion != null) {
            this.bestSolution = parserSummary.getBestQuestion().getBestAnswer();
        }
        this.log = log;
    }

    public String getProject() {
        return project;
    }

    public String getGoal() {
        return goal;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public Hint getHint() {
        return hint;
    }

    public StackExchangeAnswer getBestSolution() {
        return bestSolution;
    }

    public List<String> getLog() {
        return log;
    }

    public StackExchangeQuestion getBestQuestion() {
        return bestQuestion;
    }
}
