package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "outpass_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OutpassRequest extends BaseEntity {
    
    @Column(name = "studentId", nullable = false)
    private Long studentId; // Reference to student_profiles.id
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(name = "outpassType", length = 50, nullable = false)
    private String outpassType; // DAY_EXIT, HOME_VISIT
    
    @Column(name = "departureTime", nullable = false)
    private LocalDateTime departureTime;
    
    @Column(name = "arrivalTime")
    private LocalDateTime arrivalTime;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";
    
    @Column(name = "wardenId")
    private Long wardenId; // user_id or faculty_profile_id as authorized
    
    @Column(name = "hodId")
    private Long hodId; // user_id
    
    @Column(name = "qrCodePath", length = 255)
    private String qrCodePath;
    
    @Column(name = "approvedAt")
    private LocalDateTime approvedAt;
    
    @Column(name = "appliedAt")
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();
}
