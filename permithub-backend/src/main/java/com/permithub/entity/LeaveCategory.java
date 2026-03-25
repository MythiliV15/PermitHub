package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(name = "categoryName", length = 50, nullable = false)
    private String categoryName;
    
    @Column(name = "maxDays")
    @Builder.Default
    private Integer maxDays = 5;
    
    @Column(name = "requiresAttachment")
    @Builder.Default
    private Boolean requiresAttachment = false;
    
    @Column(name = "isAutoApprove")
    @Builder.Default
    private Boolean isAutoApprove = false;
    
    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "createdAt")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}