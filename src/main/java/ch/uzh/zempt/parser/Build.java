package ch.uzh.zempt.parser;

import ch.uzh.zempt.config.Config;
import ch.uzh.zempt.parser.hints.Hint;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Build {
    private List<String> log;
    private List<Module> modules;
    private Date startedAt;
    private Duration duration;
    private int totalMemory;
    private BuildSection buildSection;
    private Hint hint;

    public Build(List<String> log) {
        this.log = log;
        this.modules = new ArrayList<>();
    }

    public void parse() {
        this.parseModules();
        this.parseBuildSection();
        this.extractAdditionalInformation();
    }

    public List<String> getLog() {
        return log;
    }

    public List<Module> getModules() {
        return modules;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    /**
     * This parsers the log lines assigned to the commands and tries to find
     * the reactor if available. If he is present, it parsers the reactor and
     * creates the Modules. If not, the whole logsection is treated as one Module
     * Afterwards, ech log line is assigned to its Module for further parsing.
     *
     * @return void
     */
    public void parseModules() {
        boolean foundStart = false;
        boolean foundEnd = false;
        for (String logLine : this.getLog()) {
            if (foundStart && !foundEnd) {
                Pattern pattern = Pattern.compile(Parser.config.getReactor().get("extractRegex"));
                Matcher matcher = pattern.matcher(logLine);
                String name, status, duration = "";
                if (matcher.find()) {
                    name = matcher.group(1).trim();
                    status = matcher.group(2).trim();
                    duration = matcher.group(4) != null ? matcher.group(4) : "";
                    if (name.length() > 0 && status.length() > 0) {
                        this.getModules().add(new Module(name, status, duration));
                    }
                }

                if (logLine.contains(Parser.config.getReactor().get("summaryEnd"))) {
                    foundEnd = true;
                }
            } else {
                if (logLine.contains(Parser.config.getReactor().get("summaryStart"))) {
                    foundStart = true;
                }
            }
            if (foundStart && foundEnd) {
                break;
            }
        }

        boolean foundModule = false;
        Module foundedModule = null;

        // assign lines to each module
        for (String logLine : this.getLog()) {

            for (Module module : this.getModules()) {
                if (logLine.contains("Building") && logLine.contains(module.getName())) {
                    foundModule = true;
                    foundedModule = module;
                }
            }

            if (logLine.contains("BUILD FAILURE") || logLine.contains("BUILD SUCCESS") || logLine.contains("Reactor Summary")) {
                break;
            }

            if (foundModule) {
                foundedModule.getLines().add(logLine);
            }
        }

        if (this.getModules().size() == 0) {
            Module module = new Module("SingleModuleBuild", "FAILURE", "unknown");
            module.getLines().addAll(this.getLog());
            this.getModules().add(module);
        }

        for (Module module : this.getModules()) {
            module.clean();
            module.parseGoals();
        }
        System.out.println("Goals parsed");

    }

    /**
     * Parses the build section (if we encounter a build failure)
     * and extract the necessary information with in such as
     * failed goal and error reason
     */
    public void parseBuildSection() {
        List<String> tempList = new ArrayList<>();
        boolean startFound = false;
        boolean errorSectionFound = false;

        for (String logLine : this.getLog()) {

            if (logLine.contains("BUILD FAILURE")) {
                startFound = true;
            }
            if (startFound && !errorSectionFound) {
                if (logLine.startsWith("[ERROR]")) {
                    errorSectionFound = true;
                }
            }
            if (startFound && errorSectionFound && logLine.startsWith("[ERROR]")) {
                tempList.add(logLine);
            }
        }
        if (tempList.size() > 0) {
            this.buildSection = new BuildSection(tempList);
        }
    }

    public BuildSection getBuildSection() {
        return buildSection;
    }

    public Hint getHint() {
        return hint;
    }

    public boolean hasBuildSection() {
        return this.getBuildSection() != null;
    }

    /**
     * Returns if we encounter an additional Hint
     * This function checks all the implemented Hints through Reflection and checks
     * if a keyword matches any given Hint. If yes, the hint is instantiated and
     * assigned to the command
     *
     * @return bool hasHint
     */
    public boolean createAdditionalHint() {

        if (this.hasBuildSection()) {
            String failedGoal = this.getBuildSection().getFailedGoal();
            String errorReason = this.getBuildSection().getUncleanedError();

            Reflections reflections = new Reflections("ch.uzh.zempt.parser.hints");
            Set<Class<? extends Hint>> classes = reflections.getSubTypesOf(Hint.class);

            for (Class hintClass : classes) {
                try {
                    Method mGetGoalKeyword = hintClass.getMethod("getGoalKeyword");
                    String goalKeyword = (String) mGetGoalKeyword.invoke(null);
                    if (failedGoal.contains(goalKeyword)) {
                        this.hint = (Hint) hintClass.newInstance();
                        this.hint.setFailedGoal(failedGoal);
                        this.hint.setErrorReason(errorReason);
                        this.hint.setBuildError(this.getBuildSection().getUncleanedError());
                        return true;
                    }

                    Method mGetReasonKeyword = hintClass.getMethod("getReasonKeyword");
                    String reasonKeyword = (String) mGetReasonKeyword.invoke(null);
                    if (errorReason.contains(reasonKeyword)) {
                        this.hint = (Hint) hintClass.newInstance();
                        this.hint.setFailedGoal(failedGoal);
                        this.hint.setErrorReason(errorReason);
                        this.hint.setBuildError(this.getBuildSection().getUncleanedError());
                        return true;
                    }

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ignored) {
                }
            }
        }
        return false;
    }

    public Module getFailedModule() {
        for (Module module : this.getModules()) {
            if (module.getStatus().equals("FAILURE")) {
                return module;
            }
        }
        return null;
    }


    /**
     * This extract additional information from the log lines
     * It first find the call to the failed goal in the log and extract the
     * lines therein. If a hint is present, we pass this informtion to the hint
     * and finally extract the information within the hint using the Hint's method
     * extract().
     */
    public void extractAdditionalInformation() {
        boolean hasAdditionalHint = this.createAdditionalHint();
        String shortGoal = this.getBuildSection().getShortFailedGoal();
        Module failedModule = this.getFailedModule();
        List<String> extractedLines = new ArrayList<>();

        if (failedModule != null) {
            boolean found = false;
            for (String logLine : failedModule.getLines()) {
                if (logLine.contains("[INFO] -----------------------------------") || logLine.equals("[ERROR] ")) {
                    found = false;
                }
                if (found) {
                    if (!logLine.contains("Downloaded") && !logLine.contains("Downloading")) {
                        extractedLines.add(logLine);
                    }
                }
                if (logLine.contains(shortGoal)) {
                    found = true;
                }
            }

            for (String logLine : extractedLines) {
                /*String[] multipleLines = logLine.split(";");
                if (multipleLines.length > 0) {
                    for (String line : multipleLines) {
                        String cleanedLine = this.getBuildSection().cleanupString(line);
                        if (hasAdditionalHint) {
                            this.getHint().getCleanedLogLines().add(cleanedLine);
                            this.getHint().getLogLines().add(line);
                        }
                        this.getBuildSection().insertAndCount(cleanedLine);
                    }
                } else {*/
                String cleanedLine = this.getBuildSection().cleanupString(logLine);
                if (hasAdditionalHint) {
                    this.getHint().getCleanedLogLines().add(cleanedLine);
                    this.getHint().getLogLines().add(logLine);
                }
                this.getBuildSection().insertAndCount(cleanedLine);
                /*}*/
            }
            if (hasAdditionalHint) {
                this.getHint().extract();
            }
        }
    }

    public boolean hasReactor() {
        return getModules().size() > 1;
    }

    public boolean hasHint() {
        return this.getHint() != null;
    }
}
