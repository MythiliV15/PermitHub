package com.permithub.entity;

public enum Role {
    // Student Role
    STUDENT,
    
    // Faculty Roles (multi-role capable)
    FACULTY,
    FACULTY_MENTOR,
    FACULTY_CLASS_ADVISOR,
    FACULTY_EVENT_COORDINATOR,
    
    // Administrative Roles
    HOD,
    WARDEN,
    AO,
    PRINCIPAL,
    SECURITY,
    
    // Parent Role (limited access)
    PARENT
}