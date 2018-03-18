package ch.uzh.zempt.reporting;

import ch.uzh.zempt.parser.Parser;
import ch.uzh.zempt.parser.hints.Hint;
import ch.uzh.zempt.stackoverflow.StackExchangeAnswer;
import ch.uzh.zempt.stackoverflow.StackExchangeQuestion;

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

    public Report (String project, Parser parser) {
        this.project = project;
        this.goal = parser.getBuild().getBuildSection().getFailedGoal();
        this.errorCause = parser.getBuild().getBuildSection().getErrorCause();
        this.hint = parser.getBuild().getHint();
        this.bestQuestion = parser.getBestQuestion();
        if (this.bestQuestion != null) {
            this.bestSolution = parser.getBestQuestion().getBestAnswer();
        }
        this.log = parser.getBuild().getLog();
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
