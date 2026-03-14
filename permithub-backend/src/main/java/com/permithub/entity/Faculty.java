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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faculty")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Faculty extends User {

    @Column(name = "employee_id", unique = true, nullable = false)
    private String employeeId;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "designation")
    private String designation;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    // Role flags (also stored as booleans for quick querying)
    @Column(name = "is_mentor")
    @Builder.Default
    private Boolean isMentor = false;

    @Column(name = "is_class_advisor")
    @Builder.Default
    private Boolean isClassAdvisor = false;

    @Column(name = "is_event_coordinator")
    @Builder.Default
    private Boolean isEventCoordinator = false;

    // Mentor capacity
    @Column(name = "max_mentees")
    @Builder.Default
    private Integer maxMentees = 20;

    @Column(name = "current_mentees")
    @Builder.Default
    private Integer currentMentees = 0;

    // Profile details
    @Column(name = "specialization")
    private String specialization;

    @Column(name = "cabin_number")
    private String cabinNumber;

    @Column(name = "office_phone")
    private String officePhone;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "deactivated_reason")
    private String deactivatedReason;

    @Column(name = "deactivated_date")
    private LocalDateTime deactivatedDate;

    // As Mentor
    @OneToMany(mappedBy = "mentor")
    @Builder.Default
    private List<Student> mentees = new ArrayList<>();

    // As Class Advisor
    @OneToMany(mappedBy = "classAdvisor")
    @Builder.Default
    private List<Student> advisedClass = new ArrayList<>();

    // As Event Coordinator - can coordinate multiple event types
    @ElementCollection
    @CollectionTable(name = "faculty_event_types",
                     joinColumns = @JoinColumn(name = "faculty_id"))
    @Column(name = "event_type")
    @Builder.Default
    private List<String> coordinatedEventTypes = new ArrayList<>();
}
