package ch.uzh.zempt.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parser Configuration
 * Holds several fields and options for the parsing process
 * which are required, it is generated through the config.yaml
 */
public class Config {
    public Map<String, String> travisCommand;
    public Map<String, String> reactor;
    public Map<String, ParsingRule> parsingRules;
    public Map<String, List<String>> parsingRuleGroups;

    public Map<String, String> getTravisCommand() {
        return this.travisCommand;
    }

    public Map<String, String> getReactor() {
        return this.reactor;
    }

    public List<String> stopWords;

    public Map<String, List<String>> getParsingRuleGroups() {
        return this.parsingRuleGroups;
    }

    /*
     * Execute a group of Parsing Rules
     */
    public String executeParserRuleGroup(String line, String parserRuleGroup) {
        List<String> parsingRules = this.getParsingRuleGroups().get(parserRuleGroup);
        for (String parsingRuleIdentifier : parsingRules) {
            ParsingRule parsingRule = this.getParsingRules().get(parsingRuleIdentifier);
            if (parsingRule.getType().equals(ParsingRuleType.REGEX)) {
                line = line.replaceAll(parsingRule.getPattern(), parsingRule.getReplacement());
            } else {
                line = line.replace(parsingRule.getPattern(), parsingRule.getReplacement());
            }
        }
        return line;
    }

    public String getTravisCommandIdentifier(String identifier) {
        return getTravisCommand().get(identifier);
    }

    public Map<String, ParsingRule> getParsingRules() {
        return this.parsingRules;
    }

    public List<String> getStopWords() {
        return this.stopWords;
    }

    /*
     * Helper function to filter a set of strings by a list of stop words
     */
    public List<String> filterListByStopWords(Set<String> strings) {
        List<String> filteredStrings = new ArrayList<>();
        for (String string : strings) {
            StringBuffer clean = new StringBuffer();
            int index = 0;

            while (index < string.length()) {
                // the only word delimiter supported is space, if you want other
                // delimiters you have to do a series of indexOf calls and see which
                // one gives the smallest index, or use regex
                int nextIndex = string.indexOf(" ", index);
                if (nextIndex == -1) {
                    nextIndex = string.length() - 1;
                }
                String word = string.substring(index, nextIndex);
                if (!this.getStopWords().contains(word.toLowerCase())) {
                    clean.append(word);
                    if (nextIndex < string.length()) {
                        // this adds the word delimiter, e.g. the following space
                        clean.append(string.substring(nextIndex, nextIndex + 1));
                    }
                }
                index = nextIndex + 1;
            }
            filteredStrings.add(clean.toString());
        }
        return filteredStrings;
    }

    /**
     * Helper to insert a string into a HashMap and counts it.
     * @return int
     */
    public int countWordInText(String word, String text) {
        int index = text.indexOf(word);
        int count = 0;
        while (index != -1) {
            count++;
            text = text.substring(index + 1);
            index = text.indexOf(word);
        }
        return count;
    }

    /**
     * Helper function to remove stop words from a string
     * @return string
     */
    public String filterStringByStopWords(String string) {
        StringBuffer clean = new StringBuffer();
        int index = 0;

        while (index < string.length()) {
            // the only word delimiter supported is space, if you want other
            // delimiters you have to do a series of indexOf calls and see which
            // one gives the smallest index, or use regex
            int nextIndex = string.indexOf(" ", index);
            if (nextIndex == -1) {
                nextIndex = string.length() - 1;
            }
            String word = string.substring(index, nextIndex);
            if (!this.getStopWords().contains(word.toLowerCase())) {
                clean.append(word);
                if (nextIndex < string.length()) {
                    // this adds the word delimiter, e.g. the following space
                    clean.append(string.substring(nextIndex, nextIndex + 1));
                }
            }
            index = nextIndex + 1;
        }
        return clean.toString();
    }
}
