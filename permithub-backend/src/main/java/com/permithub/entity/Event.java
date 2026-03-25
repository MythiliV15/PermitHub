package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "departmentId", nullable = false)
    private Long departmentId;
    
    @Column(length = 150, nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "startDate", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "endDate", nullable = false)
    private LocalDateTime endDate;
    
    @Column(length = 200)
    private String location;
    
    @Column(name = "eventType", length = 50)
    private String eventType; // ACADEMIC, CULTURAL, PLACEMENT, SPORTS
    
    @Column(name = "createdBy", nullable = false)
    private Long createdBy; // user_id
    
    @Column(name = "createdAt")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updatedAt")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
