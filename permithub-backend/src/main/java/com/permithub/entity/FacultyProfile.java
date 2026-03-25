package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "faculty_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacultyProfile extends BaseEntity {
    
    @Column(name = "userId", unique = true, nullable = false)
    private Long userId;
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(nullable = false, length = 150)
    private String name;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 100)
    private String designation;
    
    @Column(name = "employeeId", unique = true, length = 50)
    private String employeeId;
    
    @Column(name = "profilePicPath", length = 255)
    private String profilePicPath;
}
