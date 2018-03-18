package ch.uzh.zempt.parser.hints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hint implementation for extracting additional information regarding Compilation errors
 */
public class CompilationHint extends Hint {

    // Regex to extract information from the error string
    private static Pattern pattern = Pattern.compile("/\\S+/+(\\S*.java):\\[(\\d+),(\\d+)\\] error: (.+)");

    // Regex to eliminate all class Names
    private static String classRegex = "([^\\.:;]|class) (\\b[A-Z]\\w*\\b)";

    private static int groupCount = 4;

    private String javaFile;
    private String errorLine;
    private String errorReason;

    public static String getName() {
        return "Compilation";
    }

    public static String getGoalKeyword() {
        return "compile";
    }

    public static String getView() {
        return "compile";
    }

    public static String getReasonKeyword() {
        return "asdfasdfasdfasdfasdfasf";
    }

    public String getJavaFile() {
        return javaFile;
    }

    private void setJavaFile(String javaFile) {
        this.javaFile = javaFile;
    }

    public String getErrorLine() {
        return errorLine;
    }

    private void setErrorLine(String errorLine) {
        this.errorLine = errorLine;
    }

    @Override
    public void extract() {
        for (String logLine : this.getLogLines()) {
            Matcher matcher = CompilationHint.pattern.matcher(logLine);
            if (matcher.find()) {
                if (matcher.groupCount() == CompilationHint.groupCount) {
                    setJavaFile(matcher.group(1));
                    setErrorLine(matcher.group(2));
                    setErrorReason(matcher.group(4));
                    break;
                }
            }
        }
    }

    @Override
    public String getStackExchangeQuery() {

        //we do some magic and remove all class names, Experimental!
        String errorReason = this.getErrorReason();

        //we remove all multiletter words starting with a uppercase letter which are not at the start of a sentence.
        errorReason = errorReason.replaceAll(CompilationHint.classRegex, "$1");

        return errorReason;
    }

    @Override
    public String getTXTOutput() {
        StringBuilder sb = new StringBuilder();

        sb.append("Your build contains a compilation error. Please check the following file: \n");
        sb.append("File: ").append(this.getJavaFile()).append("\n");
        sb.append("Error Line: ").append(this.getErrorLine()).append("\n");
        sb.append("Reason: ").append(this.getErrorReason()).append("\n");

        return sb.toString();
    }
}
