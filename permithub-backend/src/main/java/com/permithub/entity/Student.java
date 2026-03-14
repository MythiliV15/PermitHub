package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Student extends User {
    
    @Column(name = "register_number", unique = true, nullable = false)
    private String registerNumber;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "section")
    private String section;
    
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Faculty mentor;
    
    @ManyToOne
    @JoinColumn(name = "class_advisor_id")
    private Faculty classAdvisor;
    
    @Column(name = "is_hosteler")
    @Builder.Default
    private Boolean isHosteler = false;
    
    // Hostel details (if hosteler)
    @Column(name = "hostel_name")
    private String hostelName;
    
    @Column(name = "room_number")
    private String roomNumber;
    
    // Parent/Guardian details
    @Column(name = "parent_name")
    private String parentName;
    
    @Column(name = "parent_phone")
    private String parentPhone;
    
    @Column(name = "parent_email")
    private String parentEmail;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    // Academic details
    @Column(name = "batch")
    private String batch;  // e.g., "2024-2028"
    
    @Column(name = "admission_year")
    private Integer admissionYear;
    
    @Column(name = "current_semester")
    private Integer currentSemester;
    
    @Column(name = "leave_balance")
    @Builder.Default
    private Integer leaveBalance = 20;  // Default 20 per semester
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "address")
    private String address;
}
