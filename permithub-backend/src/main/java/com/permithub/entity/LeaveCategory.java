package com.permithub.entity;

public enum LeaveCategory {
    SICK("Sick Leave"),
    EMERGENCY("Emergency Leave"),
    OTHER("Other Leave");
    
    private final String displayName;
    
    LeaveCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}