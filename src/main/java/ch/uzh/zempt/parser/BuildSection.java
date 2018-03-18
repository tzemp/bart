package ch.uzh.zempt.parser;

import ch.uzh.zempt.config.ParsingRule;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This represents a BuildSection inside a command where the
 * informations required to understand the build failure are
 * extracted
 */
public class BuildSection {
    private List<String> lines;
    private BuildStatus status;
    private String failedGoal;
    private String errorCause;
    private Map<String, Integer> additionalErrorInformation;

    /**
     * Constructor
     * Created if a BuildSection is present, automatically
     * extract the information from the BuildSection
     */
    public BuildSection(List<String> lines) {
        this.lines = lines;
        this.additionalErrorInformation = new HashMap<>();
        extractGoal();
        extractErrorReason();
        extractAdditionalInformation();
    }

    /**
     * Extract the failed goal from the log lines
     */
    private void extractGoal() {
        //extract the goal
        ParsingRule parsingRule = Parser.config.getParsingRules().get("extract_goal");
        Pattern p = Pattern.compile(parsingRule.getPattern());
        Matcher m = p.matcher(this.getLines().get(0));
        if (m.find()) {
            String goal = m.group(0).replace(parsingRule.getReplacement(), "");
            setFailedGoal(goal.equals("on") ? "none" : goal);
        }
    }

    /**
     * Extract the error reason from the log lines
     */
    private void extractErrorReason() {

        ParsingRule parsingRule = Parser.config.getParsingRules().get("extract_error_cause");
        String errorReason = this.getLines().get(0);
        Pattern p = Pattern.compile(parsingRule.getPattern());
        Matcher m = p.matcher(errorReason);
        if (m.find()) {
            String match = errorReason.substring(m.end());
            match = cleanupString(match);
            setErrorCause(match);
        }

    }

    /**
     * Extract further information which we found in the BuildSection
     */
    private void extractAdditionalInformation() {
        boolean helpTagFound = false;
        boolean first = true;
        List<String> additionalInformation = new ArrayList<>();

        for (String logLine : this.getLines()) {
            logLine = logLine.substring(8);
            if (!helpTagFound) {
                if (logLine.contains("-> [Help 1]")) {
                    if (!logLine.startsWith("-> [Help 1]") && !first) {
                        additionalInformation.add(logLine);
                    }
                    helpTagFound = true;
                } else {
                    additionalInformation.add(logLine);
                }
            }
            first = false;
        }

        for (String string : additionalInformation) {
            String[] multipleLines = string.split(";");
            if (multipleLines.length > 0) {
                for (String line : multipleLines) {
                    String cleanedLine = cleanupString(line);
                    insertAndCount(cleanedLine);
                }
            } else {
                String cleanedLine = cleanupString(string);
                insertAndCount(cleanedLine);
            }
        }
    }

    public void insertAndCount(String line) {
        if (line.length() > 0) {
            Integer n = this.getAdditionalErrorInformation().get(line);
            n = (n == null) ? 1 : ++n;
            getAdditionalErrorInformation().put(line, n);
        }
    }


    protected String cleanupString(String line) {
        return Parser.config.executeParserRuleGroup(line, "cleanString");
    }

    public List<String> getLines() {
        return this.lines;
    }

    public void setFailedGoal(String failedGoal) {
        this.failedGoal = failedGoal;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public Map<String, Integer> getAdditionalErrorInformation() {
        return additionalErrorInformation;
    }

    public String getUncleanedError() {
        return this.getLines().get(0);
    }

    public BuildStatus getStatus() {
        return status;
    }

    public String getFailedGoal() {
        return failedGoal;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public boolean hasError() {
        return this.getFailedGoal() != null && this.getFailedGoal().length() > 0;
    }

    /**
     * Helper function to return the short version of the failed goal (without the modules name)
     */
    public String getShortFailedGoal() {
        String[] temp2 = this.getFailedGoal().split(":");
        List<String> temp = new ArrayList<>();
        temp.addAll(Arrays.asList(temp2));
        temp.remove(0);
        return String.join(":",temp);
    }
}

