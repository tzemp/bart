package ch.uzh.zempt.parser.hints;

/**
 * Helperclass for TestHint
 */
public class FailedJavaTest {

    private String javaFile;
    private String testName;
    private String lineNumber;
    private String value;

    public FailedJavaTest(String javaFile, String testName, String lineNumber, String value) {
        this.javaFile = javaFile;
        this.testName = testName;
        this.lineNumber = lineNumber;
        this.value = value;
    }

    public FailedJavaTest() {
    }

    public String getJavaFile() {
        return javaFile;
    }

    public void setJavaFile(String javaFile) {
        this.javaFile = javaFile;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
