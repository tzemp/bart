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
    private List<StackExchangeQuestionEvaluation> evaluations;
    private Build build;


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
                parser.parse();
                parser.stackOverflowAnalysis();

                String output = Parser.createTXTOutput(parser);

                PrintWriter writer = new PrintWriter("summary.txt", "UTF-8");
                writer.println(output);
                writer.close();

                // if we allow reporting, we report!
                if (allowStats) {
                    ReportingRequest request = new ReportingRequest();
                    request.post(parser, "testproject");
                }


            } catch (IOException e) {
                System.out.println("File not readable");
            }
        } else {
            System.out.println("Please provide a path to a log file");
        }
    }

    private static String createTXTOutput(Parser parser) {

        Build build = parser.getBuild();

        String divider = "===============================\n";

        StringBuilder sb = new StringBuilder();

        // Create Title
        sb.append(divider);
        sb.append("BART - Build Summarization\n");
        sb.append(divider);

        // Create Overview

        sb.append("Build Status: Failed \n");
        sb.append("Failed goal: ").append(build.getBuildSection().getFailedGoal()).append("\n");
        sb.append("Error cause: ").append(build.getBuildSection().getErrorCause()).append("\n");

        if (build.hasReactor()) {
            sb.append("Reactor: \n");
            for (Module reactorModule : build.getModules()) {
                sb.append(reactorModule.getStatus()).append(": ").append(reactorModule.getName()).append("\n");
            }
        }

        sb.append(divider);
        sb.append("Reason for Build Failure:\n");
        sb.append(build.getBuildSection().getErrorCause()).append("\n");
        sb.append(divider);

        if (build.hasHint()) {
            sb.append(build.getHint().getTXTOutput());
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
    }

    private void parse() {
        this.build = new Build(this.log);
        this.build.parse();
        System.out.println("Parsing done");
    }

    public Build getBuild() {
        return build;
    }

    public Config getConfig() {
        return config;
    }

    public List<String> getLog() {
        return log;
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

        if (this.getBuild().hasBuildSection()) {
            BuildSection buildSection = this.getBuild().getBuildSection();
            List<StackExchangeQuestion> stackExchangeQuestions = new ArrayList<>();
            String failedGoal = buildSection.getFailedGoal();

            try {

                // do we have a hint?
                if (this.getBuild().hasHint()) {
                    Hint hint = this.getBuild().getHint();
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
