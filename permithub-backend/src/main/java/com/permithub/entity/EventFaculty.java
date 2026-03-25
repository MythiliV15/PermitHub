package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_faculty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFaculty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "eventId", nullable = false)
    private Long eventId; // FK -> events.id
    
    @Column(name = "facultyId", nullable = false)
    private Long facultyId; // FK -> faculty_profiles.id
}
