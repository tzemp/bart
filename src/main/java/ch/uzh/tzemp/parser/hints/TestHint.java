package ch.uzh.tzemp.parser.hints;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hint implementation for extracting additional information regarding Test errors
 */
public class TestHint extends Hint {

    // Extract test information
    private static Pattern extractPattern = Pattern.compile("([^:]+):{0,1}(\\d+)?\\S?(.+)?");

    // If first fails, we try this regex
    private static Pattern extractWithBrackets = Pattern.compile("(\\S+)\\((\\S+)\\)");

    private static final List<String> summaryStart = new ArrayList<String>() {{
        add("Failed tests:");
        add("Tests in error:");
    }};

    private static String summaryEnd = "Tests run:";
    private static int groupCount = 3;

    private List<FailedJavaTest> failedJavaTests = new ArrayList<>();

    public static String getName() {
        return "Test";
    }

    public static String getView() {
        return "test";
    }

    public static String getGoalKeyword() {
        return "test";
    }

    public static String getReasonKeyword() {
        return "test failures";
    }

    public List<FailedJavaTest> getFailedJavaTests() {
        return this.failedJavaTests;
    }

    @Override
    public void extract() {
        // find test summary
        boolean summaryFound = false;
        boolean firstMatch = true;
        FailedJavaTest failedJavaTest = null;

        for (String logLine : this.getLogLines()) {
            if (!summaryFound) {
                for (String startString : TestHint.summaryStart) {
                    if (logLine.contains(startString)) {
                        summaryFound = true;
                        break;
                    }
                }
                continue;
            }
            if (summaryFound && logLine.contains(TestHint.summaryEnd)) {
                break;
            }
            if (summaryFound) {
                String trimmedLogLine = logLine.trim();
                Matcher matcher = TestHint.extractPattern.matcher(trimmedLogLine);
                if (matcher.find()) {
                    if (failedJavaTest != null) {
                        this.getFailedJavaTests().add(failedJavaTest);
                        failedJavaTest = new FailedJavaTest();
                    }
                    if (firstMatch) {
                        failedJavaTest = new FailedJavaTest();
                        firstMatch = false;
                    }
                    if (matcher.groupCount() == TestHint.groupCount) {

                        String classAndTest = matcher.group(1);
                        String lineNumber = matcher.group(2);
                        String value = matcher.group(3);
                        String testClass = "";
                        String testMethod = "";

                        Matcher matcher2 = TestHint.extractWithBrackets.matcher(classAndTest);

                        if (matcher2.find()) {
                            testClass = matcher2.group(2);
                            testMethod = matcher2.group(1);

                        } else {
                            String[] tmp = classAndTest.split("\\.");
                            if (tmp.length == 2) {
                                testClass = tmp[0];
                                testMethod = tmp[1];
                            } else {
                                testClass = classAndTest;
                                testMethod = classAndTest;
                            }
                        }
                        failedJavaTest.setJavaFile(testClass);
                        failedJavaTest.setTestName(testMethod);
                        failedJavaTest.setLineNumber(lineNumber);
                        failedJavaTest.setValue(value);
                    }
                } else {
                    if (failedJavaTest != null) {
                        failedJavaTest.setValue(failedJavaTest.getValue() + " " + trimmedLogLine);
                    }
                }
            }
        }
        this.getFailedJavaTests().add(failedJavaTest);
    }

    @Override
    public String getStackExchangeQuery() {
        return "there are test failures";
    }

    @Override
    public String getTXTOutput() {
        StringBuilder sb = new StringBuilder();
        String miniDivider = "------\n";

        sb.append("Your build contains ").append(this.getFailedJavaTests().size()).append(" test error(s). Please check the test(s) below: \n");
        sb.append(miniDivider);
        for (FailedJavaTest failedJavaTest : this.getFailedJavaTests()) {
            sb.append("File: ").append(failedJavaTest.getJavaFile()).append("\n");
            sb.append("Test: ").append(failedJavaTest.getTestName()).append("\n");
            sb.append("Line: ").append(failedJavaTest.getLineNumber()).append("\n");
            sb.append("Reason: ").append(failedJavaTest.getValue()).append("\n");
            sb.append(miniDivider);
        }
        return sb.toString();
    }
}
