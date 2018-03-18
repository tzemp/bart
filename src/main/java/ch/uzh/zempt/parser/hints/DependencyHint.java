package ch.uzh.zempt.parser.hints;

import ch.uzh.zempt.parser.Parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hint implementation for extracting additional information regarding Dependency errors
 */
public class DependencyHint extends Hint {

    // Extract Project and Reason for the failure
    private static Pattern projectReasonExtractPattern = Pattern.compile("for project (\\S*[.|:]+\\S+): (.*)");
    private static int projectReasonExtractGroupCount = 2;

    // Extract failed Dependency
    private static Pattern dependencyExtractPattern = Pattern.compile("artifact (\\S*[.|:]+\\S+)|Failure to find (\\S*[.|:]+\\S+)");
    private static int dependencyExtractionGroupCount = 2;

    private String project;
    private String dependency;
    private String reason;

    public static String getName() {
        return "Dependency";
    }

    /*
     * This will never be true, that why the stange string
     */
    public static String getGoalKeyword() {
        return "asdfasdfasdfasdfasdfasdf";
    }

    public static String getView() { return "dependency"; }

    public static String getReasonKeyword() {
        return "dependencies";
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void extract() {

        //extract dependency
        Matcher matcherDependency = DependencyHint.dependencyExtractPattern.matcher(this.getBuildError());
        if (matcherDependency.find() && matcherDependency.groupCount() == DependencyHint.dependencyExtractionGroupCount) {
            if (matcherDependency.group(1) != null) {
                this.setDependency(matcherDependency.group(1));
            } else if (matcherDependency.group(2) != null) {
                this.setDependency(matcherDependency.group(2));
            }
        }

        //extract project and reason
        Matcher matcherProjectReason = DependencyHint.projectReasonExtractPattern.matcher(this.getBuildError());
        if (matcherProjectReason.find() && matcherProjectReason.groupCount() == DependencyHint.projectReasonExtractGroupCount) {
            this.setProject(matcherProjectReason.group(1));
            this.setReason(matcherProjectReason.group(2).replace("-> [Help 1]",""));
        }
    }

    @Override
    public String getStackExchangeQuery() {
        String reason =  this.getReason().replace(this.getProject(),"").replace(this.getDependency(),"");
        return Parser.config.executeParserRuleGroup(reason, "cleanString");
    }

    @Override
    public String getTXTOutput() {

        StringBuilder sb = new StringBuilder();

        sb.append("Your build contains a dependency error, check the following dependency: \n");
        sb.append("Project: ").append(this.getProject()).append("\n");
        sb.append("Dependency: ").append(this.getDependency()).append("\n");
        sb.append("Reason: ").append(this.getReason()).append("\n");
        sb.append("!! Check your pom.xml file !!\n");

        return sb.toString();
    }
}
