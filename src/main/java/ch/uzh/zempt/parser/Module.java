package ch.uzh.zempt.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This represents a module which is created during
 * the parsing of the reactor summary.
 */
public class Module {
    private String goalRegex = "^\\[INFO\\] --- (.*)@ %project% ---";
    private String extractGoalInformationRegex = "(.*):(.*):(\\S*)";
    private static Pattern extractGoalInformationPattern = Pattern.compile("(.*):(.*):(\\S*)");

    private String name;
    private String status;
    private List<String> lines;
    private String duration;
    private List<Goal> goals;

    public Module(String name, String status, String duration) {
        this.name = name;
        this.status = status;
        this.duration = duration;
        this.lines = new ArrayList<>();
        this.goals = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getStatus() {
        return status;
    }

    public void clean() {
        this.removeEndingInfoLines();
    }

    /*
     * Helper function to remove Endling info lines in the log lines.
     */
    private void removeEndingInfoLines() {
        if (this.getLines().size() > 0) {
            String lastLine = this.getLines().get(this.getLines().size() - 1);
            while (lastLine.contains("[INFO] -------------------------") || lastLine.length() == 0) {
                this.getLines().remove(lastLine);
                lastLine = this.getLines().get(this.getLines().size() - 1);
            }
        }
    }

    public List<Goal> getGoals() {
        return goals;
    }

    private void addGoal(Goal goal) {
        this.getGoals().add(goal);
    }

    public void parseGoals() {
        String goalRegex = this.goalRegex.replace("%project%", this.getName());
        Pattern pattern = Pattern.compile(goalRegex);

        boolean foundGoal = false;
        Goal tempGoal = null;
        for (String logLine : this.getLines()) {
            Matcher matcher = pattern.matcher(logLine);
            if (matcher.find()) {
                // we have found a new goal, add the previous one to the list of goals
                if (tempGoal != null) {
                    this.addGoal(tempGoal);
                }
                foundGoal = true;
                Matcher matcherInfo = Module.extractGoalInformationPattern.matcher(matcher.group(1));
                if (matcherInfo.find()) {
                    String plugin = matcherInfo.group(1);
                    String version = matcherInfo.group(2);
                    String name = matcherInfo.group(3);
                    tempGoal = new Goal(plugin, version, name);
                }
            }
            if (foundGoal) {
                tempGoal.addLine(logLine);
            }
        }
        this.addGoal(tempGoal);
        this.setLastGoalFailed();
    }

    public void setLastGoalFailed() {
        if (this.getStatus().equals("FAILURE")) {
            this.getGoals().get(this.getGoals().size()-1).setStatus("FAILURE");
        }
    }
}
