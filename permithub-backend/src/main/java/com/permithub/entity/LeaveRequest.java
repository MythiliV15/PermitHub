package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false)
    private Integer totalDays;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveCategory category; // SICK, EMERGENCY, OTHER
    
    @Column(length = 500)
    private String reason;
    
    @Column(name = "medical_certificate_path")
    private String medicalCertificatePath;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Faculty mentor;
    
    @Column(name = "mentor_remark", length = 500)
    private String mentorRemark;
    
    @Column(name = "mentor_action_date")
    private LocalDateTime mentorActionDate;
    
    @ManyToOne
    @JoinColumn(name = "class_advisor_id")
    private Faculty classAdvisor;
    
    @Column(name = "class_advisor_remark", length = 500)
    private String classAdvisorRemark;
    
    @Column(name = "class_advisor_action_date")
    private LocalDateTime classAdvisorActionDate;
    
    @ManyToOne
    @JoinColumn(name = "hod_id")
    private Faculty hod;
    
    @Column(name = "hod_remark", length = 500)
    private String hodRemark;
    
    @Column(name = "hod_action_date")
    private LocalDateTime hodActionDate;
    
    @Column(name = "applied_date", nullable = false)
    private LocalDateTime appliedDate;
    
    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;
    
    @Column(name = "parent_notified")
    @Builder.Default
    private Boolean parentNotified = false;
}