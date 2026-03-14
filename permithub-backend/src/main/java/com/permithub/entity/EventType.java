package com.permithub.entity;

public enum EventType {
    SYMPOSIUM("Symposium"),
    HACKATHON("Hackathon"),
    INTERNSHIP("Internship"),
    WORKSHOP("Workshop"),
    CONFERENCE("Conference"),
    SEMINAR("Seminar"),
    INDUSTRIAL_VISIT("Industrial Visit"),
    RESEARCH_PROJECT("Research Project"),
    COMPETITION("Competition"),
    OTHER("Other");
    
    private final String displayName;
    
    EventType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
