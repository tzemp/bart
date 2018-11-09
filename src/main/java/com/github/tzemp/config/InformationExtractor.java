package com.github.tzemp.config;

public class InformationExtractor {
    private String goalIdentifier;

    private String sectionStartIdentifier;
    private String sectionEndIdentifier;

    private String sectionIdentifier;
    private String levelIdentifier;
    private String cleaningGroup;

    public InformationExtractor() {
    }

    public String getGoalIdentifier() {
        return goalIdentifier;
    }

    public String getSectionIdentifier() {
        return sectionIdentifier;
    }

    public String getLevelIdentifier() {
        return levelIdentifier;
    }

    public String getCleaningGroup() {
        return cleaningGroup;
    }

    public String getSectionStartIdentifier() {
        return sectionStartIdentifier;
    }

    public String getSectionEndIdentifier() {
        return sectionEndIdentifier;
    }
}
