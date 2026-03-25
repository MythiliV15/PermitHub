package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "od_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ODRequest extends BaseEntity {
    
    @Column(name = "studentId", nullable = false)
    private Long studentId; // FK -> student_profiles.id
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(name = "odType", length = 50, nullable = false)
    private String odType; // ACADEMIC, SPORTS, CULTURAL, PLACEMENT
    
    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "eventName", length = 150)
    private String eventName;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "attachmentPath")
    private String attachmentPath;
    
    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";
    
    @Column(name = "advisorId")
    private Long advisorId; // FK -> faculty_profiles.id
    
    @Column(name = "hodId")
    private Long hodId; // user_id (the HOD)
    
    @Column(name = "approvedAt")
    private LocalDateTime approvedAt;
    
    @Column(name = "appliedAt")
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();
}