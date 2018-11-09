package com.github.tzemp.parser;

import com.github.tzemp.parser.hints.Hint;
import com.github.tzemp.stackoverflow.StackExchangeQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This view object summaries the information we gathered
 * during the parsing process which is then passed to
 * Jenkins
 */
public class ParserSummary {
    private String command = "";
    private List<Module> reactor;
    private String buildStatus = "";
    private String failedGoal = "";
    private String errorCause = "";
    private Map<String, Integer> additionalErrorInformation = new HashMap<String, Integer>();
    private StackExchangeQuestion bestQuestion;
    private Hint hint;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean hasReactor() {
        return this.getReactor() != null && this.getReactor().size() > 0;
    }

    public List<Module> getReactor() {
        return reactor;
    }

    public void setReactor(List<Module> reactor) {
        this.reactor = reactor;
    }

    public String getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getFailedGoal() {
        return failedGoal;
    }

    public void setFailedGoal(String failedGoal) {
        this.failedGoal = failedGoal;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public Map<String, Integer> getAdditionalErrorInformation() {
        return additionalErrorInformation;
    }

    public void setAdditionalErrorInformation(Map<String, Integer> additionalErrorInformation) {
        this.additionalErrorInformation = additionalErrorInformation;
    }

    public String getReadableAdditionalErrorInformation() {
        StringBuilder sb = new StringBuilder();
        for (String cause : this.getAdditionalErrorInformation().keySet()) {
            sb.append(cause);
            sb.append("<br>");
        }
        return sb.toString();
    }

    public void setBestQuestion(StackExchangeQuestion bestQuestion) {
        this.bestQuestion = bestQuestion;
    }

    public StackExchangeQuestion getBestQuestion() {
        return this.bestQuestion;
    }

    public Hint getHint() {
        return this.hint;
    }

    public void setHint(Hint hint) {
        this.hint = hint;
    }

    public boolean hasHint() {
        return this.getHint() != null;
    }
}
