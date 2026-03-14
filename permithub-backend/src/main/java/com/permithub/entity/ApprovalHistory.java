package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistory extends BaseEntity {
    
    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType; // LEAVE, OD, OUTPASS
    
    @Column(name = "request_id", nullable = false)
    private Long requestId;
    
    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;
    
    @Column(name = "approver_role", nullable = false, length = 50)
    private String approverRole; // MENTOR, CLASS_ADVISOR, HOD, WARDEN, AO, PRINCIPAL, PARENT
    
    @Column(name = "approver_name", nullable = false)
    private String approverName;
    
    @Column(nullable = false, length = 20)
    private String action; // APPROVED, REJECTED, PENDING, CANCELLED, EXPIRED
    
    @Column(length = 500)
    private String remarks;
    
    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate;
    
    @Column(name = "previous_status", length = 50)
    private String previousStatus;
    
    @Column(name = "new_status", nullable = false, length = 50)
    private String newStatus;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 255)
    private String userAgent;
    
    // Additional metadata
    @Column(name = "approval_level")
    private Integer approvalLevel; // 1,2,3,4,5,6 for different levels
    
    @Column(name = "is_auto_approved")
    @Builder.Default
    private Boolean isAutoApproved = false;
    
    @Column(name = "notification_sent")
    @Builder.Default
    private Boolean notificationSent = false;
    
    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;
    
    // Helper methods
    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(action);
    }
    
    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(action);
    }
    
    public static ApprovalHistory createApproval(
            String requestType, Long requestId, User approver, 
            String approverRole, String remarks, 
            String previousStatus, String newStatus,
            String ipAddress, String userAgent) {
        
        return ApprovalHistory.builder()
                .requestType(requestType)
                .requestId(requestId)
                .approver(approver)
                .approverRole(approverRole)
                .approverName(approver.getFullName())
                .action("APPROVED")
                .remarks(remarks)
                .actionDate(LocalDateTime.now())
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isAutoApproved(false)
                .notificationSent(false)
                .build();
    }
    
    public static ApprovalHistory createRejection(
            String requestType, Long requestId, User approver,
            String approverRole, String remarks,
            String previousStatus, String newStatus,
            String ipAddress, String userAgent) {
        
        return ApprovalHistory.builder()
                .requestType(requestType)
                .requestId(requestId)
                .approver(approver)
                .approverRole(approverRole)
                .approverName(approver.getFullName())
                .action("REJECTED")
                .remarks(remarks)
                .actionDate(LocalDateTime.now())
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isAutoApproved(false)
                .notificationSent(false)
                .build();
    }
}
