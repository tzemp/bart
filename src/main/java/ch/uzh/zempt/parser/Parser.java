package ch.uzh.zempt.parser;

import ch.uzh.zempt.config.Config;
import ch.uzh.zempt.parser.hints.Hint;
import ch.uzh.zempt.reporting.ReportingRequest;
import ch.uzh.zempt.stackoverflow.StackExchangeClient;
import ch.uzh.zempt.stackoverflow.StackExchangeQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by timothyzemp on 06.06.17.
 */
public class Parser {
    public static Config config;
    private List<String> log;
    private List<Command> commands;
    private ParserSummary parserSummary;
    private List<StackExchangeQuestionEvaluation> evaluations;


    /**
     * Entry Point for command line version
     *
     * @param args args
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            boolean allowStats = false;
            if (args.length > 1) {
                allowStats = args[2].equals("true");
            }
            String path = args[0];
            List<String> lines = new ArrayList<>();
            try {
                // Read File
                lines = FileUtils.readLines(new File(path));

                // Read Config
                Config config = Parser.readConfig();

                // Create Parser
                Parser parser = new Parser(lines, config);

                String output = Parser.createTXTOutput(parser);

                PrintWriter writer = new PrintWriter("summary.txt", "UTF-8");
                writer.println(output);
                writer.close();

                // if we allow reporting, we report!
                if (allowStats) {
                    ReportingRequest request = new ReportingRequest();
                    request.post(parser.getParserSummary(), parser.getLog(), "testproject");
                }


            } catch (IOException e) {
                System.out.println("File not readable");
            }
        } else {
            System.out.println("Please provide a path to a log file");
        }
    }

    private static String createTXTOutput(Parser parser) {

        ParserSummary parserSummary = parser.getParserSummary();

        String divider = "===============================\n";

        StringBuilder sb = new StringBuilder();

        // Create Title
        sb.append(divider);
        sb.append("BART - Build Summarization\n");
        sb.append(divider);

        // Create Overview

        sb.append("Build Status: Failed \n");
        sb.append("Failed goal: ").append(parserSummary.getFailedGoal()).append("\n");
        sb.append("Error cause: ").append(parserSummary.getErrorCause()).append("\n");

        if (parserSummary.hasReactor()) {
            sb.append("Reactor: \n");
            for (Module reactorModule : parserSummary.getReactor()) {
                sb.append(reactorModule.getStatus()).append(": ").append(reactorModule.getName()).append("\n");
            }
        }

        sb.append(divider);
        sb.append("Reason for Build Failure:\n");
        sb.append(parserSummary.getErrorCause()).append("\n");
        sb.append(divider);

        if (parserSummary.hasHint()) {
            sb.append(parserSummary.getHint().getTXTOutput());
            sb.append(divider);
        }

        sb.append("Stack Exchange Analysis \n");
        sb.append("This may help you fix your build: \n");

        StackExchangeQuestion bestQuestion = parser.getBestQuestion();

        if (bestQuestion != null && bestQuestion.isAnswered()) {
            sb.append(bestQuestion.getBestAnswer().getBody().replaceAll("\\<.*?>","")).append("\n");
            sb.append("Full Discussion: ").append(bestQuestion.getLink()).append("\n");
        } else {
            sb.append("Unfortunately there is no matching solution :-( \n");
        }

        sb.append(divider);
        sb.append("Other useful resources: \n");

        for (StackExchangeQuestionEvaluation stackExchangeQuestionEvaluation : parser.getEvaluations()) {
            sb.append(stackExchangeQuestionEvaluation.getQuestion().getTitle()).append(": ").append(stackExchangeQuestionEvaluation.getQuestion().getLink()).append("\n");
        }
        sb.append(divider);

        return sb.toString();
    }

    /**
     * Constructor.
     * Assigns the necessary dependencies and then starts the parsing
     * and ends with the StackOverflow Analysis.
     */
    public Parser(List<String> log, Config config) {
        this.log = log;
        Parser.config = config;
        this.commands = new ArrayList<>();
        parseCommands();
        parseModules();
        stackOverflowAnalysis();
        createSummary();
    }

    /**
     * Creates the final summary which consists of different entities into a
     * single view entity to be passed to the plugin
     */
    private void createSummary() {
        this.parserSummary = new ParserSummary();
        Command lastCommand = this.getCommands().get(this.getCommands().size() - 1);
        parserSummary.setCommand(lastCommand.getName());
        if (lastCommand.hasBuildSection()) {
            BuildSection buildSection = lastCommand.getBuildSection();
            parserSummary.setBuildStatus("asdf");
            parserSummary.setErrorCause(buildSection.getErrorCause());
            parserSummary.setFailedGoal(buildSection.getFailedGoal());
            parserSummary.setAdditionalErrorInformation(buildSection.getAdditionalErrorInformation());

            if (lastCommand.hasReactor()) {
                parserSummary.setReactor(lastCommand.getModules());
            }
            if (this.getEvaluations() != null) {
                parserSummary.setBestQuestion(this.getBestQuestion());
            }
        }
        if (lastCommand.getHint() != null) {
            parserSummary.setHint(lastCommand.getHint());
        }
    }

    /**
     * Iterates through each command and first parses the modules in it
     * When the modules are parsed, we parse the (potential) build section
     * if there is a build section, we additionally extract further information
     * through the Hint interface
     */
    private void parseModules() {
        for (Command command : this.getCommands()) {
            command.parseModules();
            command.parseBuildSection();
            if (command.hasBuildSection()) {
                command.extractAdditionalInformation();
            }
        }
    }

    /**
     * Iterates through the log and identifies each Command in the log
     * by the identifiers we define in the config.yml and then assigns
     * the sections of the log to the corresponding Command
     */
    private void parseCommands() {
        boolean insideTravisFold = false;
        List<String> tempLines = new ArrayList<String>();
        Command tempCommand = null;

        for (String line : this.getLog()) {

            // Are we in a travis_fold section?
            if (insideTravisFold) {
                tempLines.add(line);

                //does the section ends?
                if (line.contains(Parser.config.getTravisCommandIdentifier("end")) || (line.contains("command") && line.contains("Retrying"))) {
                    insideTravisFold = false;
                    tempCommand.setLines(tempLines);
                    tempLines = new ArrayList<>();
                    this.getCommands().add(tempCommand);
                }

            } else {
                // a new section start?
                if (line.contains(Parser.config.getTravisCommandIdentifier("start")) || (line.contains("command") && line.contains("Retrying"))) {

                    // are we reading?
                    if (tempLines.size() > 0) {
                        tempCommand.setLines(tempLines);
                        this.getCommands().add(tempCommand);
                        tempLines = new ArrayList<>();
                    }
                    insideTravisFold = true;
                    Pattern p = Pattern.compile(Parser.config.getTravisCommandIdentifier("extract"));
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        tempCommand = new Command();
                        tempCommand.setName(m.group(1));
                    }
                } else {
                    if (tempLines.size() > 0) {
                        tempLines.add(line);
                    } else {
                        tempCommand = new Command();
                        tempLines.add(line);
                    }
                }
            }
        }

        if (tempLines.size() > 0) {
            tempCommand.setLines(tempLines);
            this.getCommands().add(tempCommand);
        }
    }

    public Config getConfig() {
        return config;
    }

    public List<String> getLog() {
        return log;
    }

    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Temp Function to print the content of the parser
     * it behaves kinda like var_dump from php.
     */
    public List<String> print() {
        List<String> temp = new ArrayList<>();
        for (Command command : this.getCommands()) {
            temp.addAll(command.print());
        }
        return temp;
    }

    /**
     * This performs the StackOverflow Analysis be creating a connection
     * to StackExchange, perform the necessary queries to obtain discussions and
     * then evaluate those discussions to find the best solution available
     */
    public void stackOverflowAnalysis() {

        // We create a connection to stackoverflow
        StackExchangeClient stackExchangeClient = new StackExchangeClient("stackoverflow");

        // we get the last command, because this is the one which lead to the build failure
        Command command = this.getCommands().get(this.getCommands().size() - 1);

        if (command.hasBuildSection()) {
            BuildSection buildSection = command.getBuildSection();
            List<StackExchangeQuestion> stackExchangeQuestions = new ArrayList<>();
            String failedGoal = buildSection.getFailedGoal();

            try {

                // do we have a hint?
                if (command.getHint() != null) {
                    Hint hint = command.getHint();
                    List<StackExchangeQuestion> stackExchangeQuestionsHint = stackExchangeClient.search(hint.getStackExchangeQuery());
                    stackExchangeQuestions.addAll(stackExchangeQuestionsHint);

                } else {

                    // we are searching for the goal as well as for the cause (2 queries)
                    if (failedGoal.equals("none")) {
                        failedGoal = Parser.config.filterStringByStopWords(buildSection.getErrorCause());
                    }

                    // Query for the failed goal
                    String cleanFailedGoal = failedGoal.replace(":", "\\").replaceAll("[0-9]", "");
                    List<StackExchangeQuestion> stackExchangeQuestionsGoal = stackExchangeClient.search(cleanFailedGoal);
                    stackExchangeQuestions.addAll(stackExchangeQuestionsGoal);

                    // Query for the error cause
                    List<StackExchangeQuestion> stackExchangeQuestionsCause = stackExchangeClient.search(buildSection.getErrorCause());
                    stackExchangeQuestions.addAll(stackExchangeQuestionsCause);
                }

                // rate
                StackExchangeQuestionRater rater = new StackExchangeQuestionRater(stackExchangeQuestions, buildSection);
                rater.rate();

                this.evaluations = rater.getQuestionsWithHigestRating(3);

            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public List<StackExchangeQuestionEvaluation> getEvaluations() {
        return this.evaluations;
    }

    public ParserSummary getParserSummary() {
        return parserSummary;
    }

    /**
     * returns the best Discussions from the evaluated Discussions
     */
    public StackExchangeQuestion getBestQuestion() {
        if (this.getEvaluations().size() > 0) {
            for (StackExchangeQuestionEvaluation stackExchangeQuestionEvaluation : this.getEvaluations()) {
                if (stackExchangeQuestionEvaluation.getQuestion().isAnswered()) {
                    return stackExchangeQuestionEvaluation.getQuestion();
                }
            }
            return this.getEvaluations().get(0).getQuestion();
        } else {
            return null;
        }
    }

    /**
     * Reads the config and returns it
     *
     * @return config
     */
    private static Config readConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(new File("src/config/config.yaml"), Config.class);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
