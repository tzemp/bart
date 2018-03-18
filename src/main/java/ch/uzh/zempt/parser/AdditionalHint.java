package ch.uzh.zempt.parser;

import java.util.ArrayList;
import java.util.List;

/*
 * This is no longer used, first attemt to implement hints
 */
public class AdditionalHint {
    private String name;
    private String message;
    private List<String> logLines = new ArrayList<String>();
    private List<String> cleanedLogLines = new ArrayList<String>();

    public AdditionalHint() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
}
