package ch.uzh.zempt.config;

/**
 * This Class represent a parsing rule to either remove information
 * or extract them, according to the type
 */
public class ParsingRule {

    private String pattern;
    private String replacement;
    private ParsingRuleType type;

    public ParsingRule() {
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public ParsingRuleType getType() {
        return type;
    }

    public void setType(ParsingRuleType type) {
        this.type = type;
    }
}
