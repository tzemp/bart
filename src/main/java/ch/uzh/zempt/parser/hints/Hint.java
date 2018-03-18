package ch.uzh.zempt.parser.hints;

import java.util.ArrayList;
import java.util.List;

/**
 * The Hint interface aims to extract further information from the corresponding log section according to the failure type
 */
public abstract class Hint {
    private List<String> logLines = new ArrayList<>();
    private List<String> cleanedLogLines = new ArrayList<>();
    private String failedGoal;
    private String errorReason;
    private String buildError;

    public List<String> getLogLines() {
        return logLines;
    }

    public void setLogLines(List<String> logLines) {
        this.logLines = logLines;
    }

    public List<String> getCleanedLogLines() {
        return cleanedLogLines;
    }

    public void setCleanedLogLines(List<String> cleanedLogLines) {
        this.cleanedLogLines = cleanedLogLines;
    }

    public String getFailedGoal() {
        return failedGoal;
    }

    public void setFailedGoal(String failedGoal) {
        this.failedGoal = failedGoal;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getBuildError() {
        return buildError;
    }

    public void setBuildError(String buildError) {
        this.buildError = buildError;
    }


    /**
     * Extracts the information from the log lines
     */
    public void extract() {
    }

    /**
     * Returns the string which we use in the StackExchange Query
     * @return String
     */
    public abstract String getStackExchangeQuery();

    public abstract String getTXTOutput();
}
