package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "outpass_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutpassRequest extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(name = "out_datetime", nullable = false)
    private LocalDateTime outDateTime;
    
    @Column(name = "expected_return_datetime", nullable = false)
    private LocalDateTime expectedReturnDateTime;
    
    @Column(name = "actual_return_datetime")
    private LocalDateTime actualReturnDateTime;
    
    @Column(nullable = false)
    private String destination;
    
    @Column(length = 500)
    private String reason;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    // Parent approval
    @Column(name = "parent_token", unique = true)
    private String parentToken;
    
    @Column(name = "parent_token_expiry")
    private LocalDateTime parentTokenExpiry;
    
    @Column(name = "parent_remark", length = 500)
    private String parentRemark;
    
    @Column(name = "parent_action_date")
    private LocalDateTime parentActionDate;
    
    // Mentor approval
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Faculty mentor;
    
    @Column(name = "mentor_remark", length = 500)
    private String mentorRemark;
    
    @Column(name = "mentor_action_date")
    private LocalDateTime mentorActionDate;
    
    // Class Advisor approval
    @ManyToOne
    @JoinColumn(name = "class_advisor_id")
    private Faculty classAdvisor;
    
    @Column(name = "class_advisor_remark", length = 500)
    private String classAdvisorRemark;
    
    @Column(name = "class_advisor_action_date")
    private LocalDateTime classAdvisorActionDate;
    
    // HOD approval
    @ManyToOne
    @JoinColumn(name = "hod_id")
    private Faculty hod;
    
    @Column(name = "hod_remark", length = 500)
    private String hodRemark;
    
    @Column(name = "hod_action_date")
    private LocalDateTime hodActionDate;

    // Warden approval
    @ManyToOne
    @JoinColumn(name = "warden_id")
    private Faculty warden;
    
    @Column(name = "warden_remark", length = 500)
    private String wardenRemark;
    
    @Column(name = "warden_action_date")
    private LocalDateTime wardenActionDate;
    
    // AO approval
    @ManyToOne
    @JoinColumn(name = "ao_id")
    private User ao; // Administrative Officer
    
    @Column(name = "ao_remark", length = 500)
    private String aoRemark;
    
    @Column(name = "ao_action_date")
    private LocalDateTime aoActionDate;
    
    // Principal approval
    @ManyToOne
    @JoinColumn(name = "principal_id")
    private User principal;
    
    @Column(name = "principal_remark", length = 500)
    private String principalRemark;
    
    @Column(name = "principal_action_date")
    private LocalDateTime principalActionDate;
    
    // QR Code
    @Column(name = "qr_code_path")
    private String qrCodePath;
    
    @Column(name = "qr_generated_date")
    private LocalDateTime qrGeneratedDate;
    
    @Column(name = "qr_scanned_exit")
    private LocalDateTime qrScannedExit;
    
    @Column(name = "qr_scanned_entry")
    private LocalDateTime qrScannedEntry;
    
    @Column(name = "is_late_entry")
    @Builder.Default
    private Boolean isLateEntry = false;
    
    @Column(name = "late_minutes")
    @Builder.Default
    private Integer lateMinutes = 0;
    
    @Column(name = "applied_date", nullable = false)
    private LocalDateTime appliedDate;
    
    // Helper methods to check approval stages
    public boolean isParentApproved() {
        return status == RequestStatus.PARENT_APPROVED;
    }
    
    public boolean isMentorApproved() {
        return status == RequestStatus.APPROVED_BY_MENTOR ||
               status == RequestStatus.APPROVED_BY_CLASS_ADVISOR ||
               status == RequestStatus.APPROVED_BY_WARDEN ||
               status == RequestStatus.APPROVED_BY_AO ||
               status == RequestStatus.APPROVED_BY_PRINCIPAL;
    }
    
    public boolean isClassAdvisorApproved() {
        return status == RequestStatus.APPROVED_BY_CLASS_ADVISOR ||
               status == RequestStatus.APPROVED_BY_WARDEN ||
               status == RequestStatus.APPROVED_BY_AO ||
               status == RequestStatus.APPROVED_BY_PRINCIPAL;
    }
    
    public boolean isWardenApproved() {
        return status == RequestStatus.APPROVED_BY_WARDEN ||
               status == RequestStatus.APPROVED_BY_AO ||
               status == RequestStatus.APPROVED_BY_PRINCIPAL;
    }
    
    public boolean isAoApproved() {
        return status == RequestStatus.APPROVED_BY_AO ||
               status == RequestStatus.APPROVED_BY_PRINCIPAL;
    }
    
    public boolean isPrincipalApproved() {
        return status == RequestStatus.APPROVED_BY_PRINCIPAL;
    }
    
    public boolean isFullyApproved() {
        return status == RequestStatus.APPROVED_BY_PRINCIPAL;
    }
    
    public boolean isRejected() {
        return status.name().startsWith("REJECTED");
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expectedReturnDateTime) && 
               !isFullyApproved();
    }
    
    public boolean isLate() {
        if (actualReturnDateTime != null && expectedReturnDateTime != null) {
            return actualReturnDateTime.isAfter(expectedReturnDateTime);
        }
        return false;
    }
    
    public long calculateLateMinutes() {
        if (actualReturnDateTime != null && expectedReturnDateTime != null && 
            actualReturnDateTime.isAfter(expectedReturnDateTime)) {
            return java.time.Duration.between(expectedReturnDateTime, actualReturnDateTime).toMinutes();
        }
        return 0;
    }
}
