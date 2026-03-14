package com.permithub.service;

import com.permithub.util.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            Context context = new Context();
            context.setVariable("resetLink", baseUrl + "/reset-password?token=" + token);
            context.setVariable("token", token);
            context.setVariable("expiryMinutes", Constants.PASSWORD_RESET_TOKEN_EXPIRY_MINUTES);

            String htmlContent = templateEngine.process("email/forgot-password", context);
            
            sendHtmlEmail(to, Constants.EMAIL_SUBJECT_PASSWORD_RESET, htmlContent);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String name, String temporaryPassword) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("temporaryPassword", temporaryPassword);
            context.setVariable("loginLink", baseUrl + "/login");

            String htmlContent = templateEngine.process("email/welcome", context);
            
            sendHtmlEmail(to, Constants.EMAIL_SUBJECT_WELCOME, htmlContent);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendParentApprovalEmail(String to, String studentName, String approvalLink) {
        try {
            Context context = new Context();
            context.setVariable("studentName", studentName);
            context.setVariable("approvalLink", approvalLink);
            context.setVariable("expiryHours", Constants.PARENT_LINK_EXPIRY_HOURS);

            String htmlContent = templateEngine.process("email/parent-approval", context);
            
            sendHtmlEmail(to, Constants.EMAIL_SUBJECT_PARENT_APPROVAL, htmlContent);
            log.info("Parent approval email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send parent approval email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendNotificationEmail(String to, String subject, String content) {
        try {
            Context context = new Context();
            context.setVariable("content", content);

            String htmlContent = templateEngine.process("email/notification", context);
            
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Notification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send notification email to {}: {}", to, e.getMessage());
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, java.io.UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}
