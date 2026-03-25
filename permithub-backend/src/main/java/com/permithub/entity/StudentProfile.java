package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StudentProfile extends BaseEntity {
    
    @Column(name = "userId", unique = true, nullable = false)
    private Long userId;
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(name = "semesterId", nullable = false)
    private Long semesterId;
    
    @Column(nullable = false, length = 150)
    private String name;
    
    @Column(name = "regNo", unique = true, nullable = false, length = 50)
    private String regNo;
    
    @Column(length = 20)
    private String phone;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false, length = 5)
    private String section;
    
    @Column(name = "isHosteler", nullable = false)
    @Builder.Default
    private Boolean isHosteler = false;
    
    @Column(name = "hostelType", length = 10)
    private String hostelType;
    
    @Column(name = "roomNo", length = 20)
    private String roomNo;
    
    @Column(name = "parentName", length = 150)
    private String parentName;
    
    @Column(name = "parentPhone", length = 20)
    private String parentPhone;
    
    @Column(name = "parentEmail", length = 150)
    private String parentEmail;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "emergencyContact", length = 20)
    private String emergencyContact;
    
    @Column(name = "profilePicPath", length = 255)
    private String profilePicPath;
    
    @Column(name = "mentorId")
    private Long mentorId;
    
    @Column(name = "classAdvisorId")
    private Long classAdvisorId;
    
    @Column(name = "leaveBalance", nullable = false)
    @Builder.Default
    private Integer leaveBalance = 20;
}
