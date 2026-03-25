package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LeaveRequest extends BaseEntity {
    
    @Column(name = "studentId", nullable = false)
    private Long studentId; // Reference to student_profiles.id
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(name = "categoryId", nullable = false)
    private Long categoryId;
    
    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "attachmentPath")
    private String attachmentPath;
    
    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING"; // PENDING, MENTOR_APPROVED...
    
    @Column(name = "mentorId")
    private Long mentorId; // faculty_profile_id
    
    @Column(name = "advisorId")
    private Long advisorId; // faculty_profile_id
    
    @Column(name = "hodId")
    private Long hodId; // user_id (HOD)
    
    @Column(name = "approvedAt")
    private LocalDateTime approvedAt;
    
    @Column(name = "appliedAt")
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();
}