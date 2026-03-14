package com.permithub.util;

public class Constants {
    
    private Constants() {
        // Private constructor to hide implicit public one
    }
    
    // API Endpoints
    public static final String API_BASE = "/api";
    public static final String AUTH_API = "/auth";
    public static final String USER_API = "/users";
    public static final String STUDENT_API = "/students";
    public static final String FACULTY_API = "/faculty";
    public static final String DEPARTMENT_API = "/departments";
    
    // Roles
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_FACULTY_MENTOR = "FACULTY_MENTOR";
    public static final String ROLE_FACULTY_CLASS_ADVISOR = "FACULTY_CLASS_ADVISOR";
    public static final String ROLE_FACULTY_EVENT_COORDINATOR = "FACULTY_EVENT_COORDINATOR";
    public static final String ROLE_HOD = "HOD";
    public static final String ROLE_WARDEN = "WARDEN";
    public static final String ROLE_AO = "AO";
    public static final String ROLE_PRINCIPAL = "PRINCIPAL";
    public static final String ROLE_SECURITY = "SECURITY";
    public static final String ROLE_PARENT = "PARENT";
    
    // Default Values
    public static final int DEFAULT_LEAVE_BALANCE = 20;
    public static final int MAX_OUTPASS_HOURS = 48;
    public static final int PARENT_LINK_EXPIRY_HOURS = 24;
    public static final int PASSWORD_RESET_TOKEN_EXPIRY_MINUTES = 30;
    
    // Messages
    public static final String MSG_USER_NOT_FOUND = "User not found";
    public static final String MSG_INVALID_CREDENTIALS = "Invalid email or password";
    public static final String MSG_ACCOUNT_DISABLED = "Account is disabled";
    public static final String MSG_EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String MSG_PASSWORD_RESET_EMAIL_SENT = "Password reset email sent successfully";
    public static final String MSG_INVALID_TOKEN = "Invalid or expired token";
    public static final String MSG_PASSWORD_RESET_SUCCESS = "Password reset successful";
    public static final String MSG_PASSWORD_CHANGE_SUCCESS = "Password changed successfully";
    public static final String MSG_FIRST_LOGIN_PASSWORD_CHANGE = "Please change your password on first login";
    
    // Email Subjects
    public static final String EMAIL_SUBJECT_PASSWORD_RESET = "PermitHub - Password Reset Request";
    public static final String EMAIL_SUBJECT_WELCOME = "Welcome to PermitHub";
    public static final String EMAIL_SUBJECT_PARENT_APPROVAL = "PermitHub - Parent Approval Required";
    
    // QR Code
    public static final String QR_CODE_DIRECTORY = "qr-codes";
    public static final int QR_CODE_WIDTH = 300;
    public static final int QR_CODE_HEIGHT = 300;
}