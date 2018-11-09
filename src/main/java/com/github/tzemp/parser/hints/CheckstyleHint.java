package com.github.tzemp.parser.hints;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hint implementation for extracting additional information regarding CheckStyle errors
 */
public class CheckstyleHint extends Hint {

    // Regex to extract the information necessary
    private static Pattern pattern = Pattern.compile("/\\S+/+(\\S*.java):(\\d+):(.+)");
    private static String summaryStart = "Starting audit";
    private static String summaryEnd = "Audit done";
    private static int groupCount = 3;

    private List<CheckstyleViolation> checkstyleViolations = new ArrayList<>();

    public static String getView() {
        return "checkstyle";
    }

    public static String getName() {
        return "Checkstyle";
    }

    public static String getGoalKeyword() {
        return "checkstyle";
    }

    public static String getReasonKeyword() {
        return "asdfasdfasdfasdfasdfasf";
    }

    public List<CheckstyleViolation> getCheckstyleViolations() {
        return this.checkstyleViolations;
    }

    @Override
    public void extract() {
        // find test summary
        boolean summaryFound = false;

        for (String logLine : this.getLogLines()) {
            if (logLine.contains(CheckstyleHint.summaryStart)) {
                summaryFound = true;
                continue;
            }
            if (summaryFound && logLine.contains(CheckstyleHint.summaryEnd)) {
                break;
            }
            if (summaryFound) {
                String trimmedLogLine = logLine.trim();
                Matcher matcher = CheckstyleHint.pattern.matcher(trimmedLogLine);
                if (matcher.find() && matcher.groupCount() == CheckstyleHint.groupCount) {
                    CheckstyleViolation checkstyleViolation = new CheckstyleViolation(matcher.group(1), matcher.group(2), matcher.group(3));
                    this.getCheckstyleViolations().add(checkstyleViolation);
                }
            }
        }
    }

    @Override
    public String getStackExchangeQuery() {
        //TODO: This is bad!
        return this.getCheckstyleViolations().get(0).getReason();
    }


    @Override
    public String getTXTOutput() {
        StringBuilder sb = new StringBuilder();
        String miniDivider = "------\n";

        sb.append("Your build contains ").append(this.getCheckstyleViolations().size()).append(" error. Please check the violation(s) below: \n");
        sb.append(miniDivider);
        for (CheckstyleViolation checkstyleViolation : this.getCheckstyleViolations()) {
            sb.append("File: ").append(checkstyleViolation.getJavaFile()).append("\n");
            sb.append("Line: ").append(checkstyleViolation.getLineNumber()).append("\n");
            sb.append("Reason: ").append(checkstyleViolation.getReason()).append("\n");
            sb.append(miniDivider);
        }
        sb.append("Depending on your checkstyle configuration, your build will ignore violations marked with warning. \n");
        return sb.toString();
    }
}
