package com.permithub.service;

public interface EmailService {
    
    void sendPasswordResetEmail(String to, String token);
    
    void sendWelcomeEmail(String to, String name, String temporaryPassword);
    
    void sendParentApprovalEmail(String to, String studentName, String approvalLink);
    
    void sendNotificationEmail(String to, String subject, String content);
}