package ch.uzh.zempt.parser.hints;

/**
 * Helperclass for CheckStyleHint
 */
public class CheckstyleViolation {

    private String javaFile;
    private String lineNumber;
    private String reason;

    public CheckstyleViolation(String javaFile, String lineNumber, String reason) {
        this.javaFile = javaFile;
        this.lineNumber = lineNumber;
        this.reason = reason;
    }

    public CheckstyleViolation() {
    }

    public String getJavaFile() {
        return javaFile;
    }

    public void setJavaFile(String javaFile) {
        this.javaFile = javaFile;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
