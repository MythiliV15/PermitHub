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
@Table(name = "od_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ODRequest extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false)
    private Integer totalDays;
    
    @Column(nullable = false)
    private String eventType; // SYMPOSIUM, HACKATHON, INTERNSHIP, WORKSHOP, etc.
    
    @Column(nullable = false)
    private String eventName;
    
    private String organizer;
    
    private String location;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "proof_document_path")
    private String proofDocumentPath;
    
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
    @JoinColumn(name = "event_coordinator_id")
    private Faculty eventCoordinator;
    
    @Column(name = "coordinator_remark", length = 500)
    private String coordinatorRemark;
    
    @Column(name = "coordinator_action_date")
    private LocalDateTime coordinatorActionDate;
    
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
    
    @Column(name = "is_outstation")
    @Builder.Default
    private Boolean isOutstation = false;
    
    @Column(name = "accommodation_required")
    @Builder.Default
    private Boolean accommodationRequired = false;
}